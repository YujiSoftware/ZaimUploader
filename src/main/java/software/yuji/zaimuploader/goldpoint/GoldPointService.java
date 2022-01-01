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

        String line;
        List<Payment> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charset.forName("Shift-JIS")));
        while ((line = reader.readLine()) != null) {
            GoldPointRecord record = GoldPointRecord.create(line.split(","));
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
        return 0;
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
            LocalDateTime 利用日, String 利用店名, String 請求対象, String 支払区分, Optional<String> 今回回数, String 支払い月,
            int 利用金額, int お支払い金額,
            Optional<BigDecimal> 現地通貨額, Optional<String> 略称, Optional<BigDecimal> 換算レート, Optional<String> 換算日,
            Optional<String> 備考
    ) {
        // ex. 2021年4月25日 18時09分
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu/M/d");

        public static GoldPointRecord create(String[] columns) {
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
                    columns[2],
                    columns[3],
                    string.apply(columns[4]),
                    columns[5],
                    Integer.parseInt(columns[6]),
                    Integer.parseInt(columns[7]),
                    bigDecimal.apply(columns[8]),
                    string.apply(columns[9]),
                    bigDecimal.apply(columns[10]),
                    string.apply(columns[11]),
                    string.apply(columns[12])
            );
        }

        public GoldPointPK getPK() {
            return new GoldPointPK(利用日, 利用店名, 請求対象, 利用金額);
        }
    }
}
