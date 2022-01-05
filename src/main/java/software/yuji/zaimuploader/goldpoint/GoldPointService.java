package software.yuji.zaimuploader.goldpoint;

import oauth.signpost.exception.OAuthException;
import org.springframework.stereotype.Service;
import software.yuji.zaimuploader.Payment;
import software.yuji.zaimuploader.PaymentService;
import software.yuji.zaimuploader.account.Account;
import software.yuji.zaimuploader.account.AccountService;
import software.yuji.zaimuploader.category.Category;
import software.yuji.zaimuploader.genre.Genre;
import software.yuji.zaimuploader.genre.GenreService;
import software.yuji.zaimuploader.zaim.Zaim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

@Service
public class GoldPointService implements PaymentService {

    private final GoldPointRepository repository;

    private final AccountService accountService;

    private final GenreService genreService;

    private final Zaim zaim;

    public GoldPointService(GoldPointRepository repository, AccountService accountService, GenreService genreService, Zaim zaim) {
        this.repository = repository;
        this.accountService = accountService;
        this.genreService = genreService;
        this.zaim = zaim;
    }

    @Override
    public Payment[] readCSV(InputStream stream) throws IOException {
        Account goldPoint = accountService.getGoldPoint();
        Map<Genre, Category> mapping = genreService.getCategoryMapping();

        int rows = 0;
        明細種別 明細 = 明細種別.予定;

        String line;
        List<Payment> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charset.forName("Shift-JIS")));
        while ((line = reader.readLine()) != null) {
            rows++;

            if (line.isEmpty()) {
                continue;
            }

            String[] columns = line.split(",");

            // 合計行（= １列目に日付がない）なら、スキップ
            if (columns[0].isEmpty()) {
                continue;
            }

            // ヘッダーがある（= 2列目にカード番号が記載してある）なら、確定明細
            if (rows == 1 && columns.length >= 2 && columns[1].startsWith("4980-01**-****")) {
                明細 = 明細種別.確定;
                continue;
            }

            GoldPointRecord record = 明細.create(columns);
            if (repository.existsById(record.getPK())) {
                continue;
            }

            Genre genre = genreService.loadDefault(goldPoint, record.利用店名).orElse(null);

            list.add(new Payment(record, record.hashCode(), record.利用日, record.利用店名, record.利用金額, genre, mapping));
        }

        return list.toArray(new Payment[0]);
    }

    @Override
    public int send(Payment[] payments) throws OAuthException, IOException {
        Account account = accountService.getGoldPoint();

        int send = 0;
        for (Payment payment : payments) {
            if (payment.getGenre() == null) {
                continue;
            }

            Zaim.ZaimPaymentResult result = zaim.sendPayment(account, payment);

            save(account, payment, result);

            send++;
        }

        return send;
    }

    private void save(Account account, Payment payment, Zaim.ZaimPaymentResult result) {
        GoldPointRecord record = (GoldPointRecord) payment.getRecord();

        GoldPoint goldPoint = new GoldPoint(
                record.getPK(),
                result.getMoney().getId(),
                result.getMoney().getModified()
        );
        repository.save(goldPoint);

        genreService.saveDefault(account, record.利用店名, payment.getGenre());
    }


    private enum 明細種別 {
        予定 {
            @Override
            public GoldPointRecord create(String[] columns) {
                // String#split の結果、末尾の連続したカンマが消えてしまうので、ここでサイズをそろえる
                if (columns.length != 13) {
                    columns = Arrays.copyOf(columns, 13);
                }

                Function<String, Optional<String>> string =
                        (s) -> s == null || s.isEmpty() ? Optional.empty() : Optional.of(s);
                Function<String, Optional<BigDecimal>> bigDecimal =
                        (s) -> s == null || s.isEmpty() ? Optional.empty() : Optional.of(new BigDecimal(s));

                return new GoldPointRecord(
                        LocalDate.parse(columns[0], FORMATTER).atTime(0, 0),
                        columns[1],
                        string.apply(columns[2]),
                        columns[3],
                        string.apply(columns[4]),
                        string.apply(columns[5]),
                        Integer.parseInt(columns[6]),
                        Integer.parseInt(columns[7]),
                        bigDecimal.apply(columns[8]),
                        string.apply(columns[9]),
                        bigDecimal.apply(columns[10]),
                        string.apply(columns[11]),
                        string.apply(columns[12])
                );
            }
        },
        確定 {
            @Override
            public GoldPointRecord create(String[] columns) {
                // String#split の結果、末尾の連続したカンマが消えてしまうので、ここでサイズをそろえる
                if (columns.length != 7) {
                    columns = Arrays.copyOf(columns, 7);
                }

                LocalDateTime 利用日 = LocalDate.parse(columns[0], FORMATTER).atTime(0, 0);
                String 利用店名 = columns[1];
                int 利用金額 = Integer.parseInt(columns[2]);
                String 支払区分 = columns[3];
                Optional<String> 今回回数 = Optional.ofNullable(columns[4]);
                int お支払い金額 = Integer.parseInt(columns[5]);
                Optional<String> 備考 = Optional.ofNullable(columns[6]);

                return new GoldPointRecord(
                        利用日,
                        利用店名,
                        Optional.empty(),
                        支払区分,
                        今回回数,
                        Optional.empty(),
                        利用金額,
                        お支払い金額,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        備考
                );
            }
        };

        // ex. 2021年4月25日 18時09分
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu/M/d");

        public abstract GoldPointRecord create(String[] columns);
    }

    /**
     * 利用日 - 2021/12/31
     * 利用店名 - 業務スーパー
     * 請求対象 - ご本人
     * 支払区分 - 1回払い
     * 支払い月 - '21/12
     * 利用金額 - 734
     * 支払い金額 - 734
     * 現地通貨額 - 58.00
     * 略称 - USD
     * 換算レート - 115.958
     * 換算日 - 11/09
     * 備考 - 返品
     */
    private record GoldPointRecord(
            LocalDateTime 利用日, String 利用店名, Optional<String> 請求対象, String 支払区分, Optional<String> 今回回数,
            Optional<String> 支払い月, int 利用金額, int お支払い金額,
            Optional<BigDecimal> 現地通貨額, Optional<String> 略称, Optional<BigDecimal> 換算レート, Optional<String> 換算日,
            Optional<String> 備考
    ) {
        public GoldPointPK getPK() {
            return new GoldPointPK(利用日, 利用店名, 利用金額);
        }
    }
}
