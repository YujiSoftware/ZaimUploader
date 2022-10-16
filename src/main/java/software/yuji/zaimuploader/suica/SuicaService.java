package software.yuji.zaimuploader.suica;

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
import java.nio.charset.StandardCharsets;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public final class SuicaService implements PaymentService {

    private final SuicaRepository repository;

    private final AccountService accountService;

    private final GenreService genreService;

    private final Zaim zaim;

    public SuicaService(SuicaRepository repository, AccountService accountService, GenreService genreService, Zaim zaim) {
        this.repository = repository;
        this.accountService = accountService;
        this.genreService = genreService;
        this.zaim = zaim;
    }

    @Override
    public Payment[] readCSV(InputStream stream) throws IOException {
        Map<Genre, Category> mapping = genreService.getCategoryMapping();

        Genre 電車 = genreService.load("電車");
        Genre バス = genreService.load("バス");

        String line;
        List<Payment> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("月日\t")) {
                continue;
            }
            if (line.isBlank()) {
                continue;
            }

            SuicaRecord record = SuicaRecord.create(line.split("\t"));
            if (record.入金_利用額 >= 0) {
                continue;
            }
            if (repository.existsById(record.getPK())) {
                continue;
            }

            String place;
            Genre genre;
            if (record.in種別.endsWith("入") && record.out種別.endsWith("出")) {
                place = record.in利用場所 + " ～ " + record.out利用場所;
                genre = 電車;
            } else if (record.in種別.equals("ﾊﾞｽ等")) {
                place = record.in利用場所();
                genre = バス;
            } else {
                place = record.in種別();
                genre = null;
            }

            list.add(new Payment(record, record.hashCode(), Year.now().atMonthDay(record.月日).atStartOfDay(), place, -record.入金_利用額, genre, mapping));
        }

        return list.toArray(new Payment[0]);
    }

    @Override
    public int send(Payment[] payments) throws OAuthException, IOException {
        Account account = accountService.getPayPay();

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
        SuicaRecord record = (SuicaRecord) payment.getRecord();

        Suica suica = new Suica(
                record.getPK(),
                result.getMoney().getId(),
                result.getMoney().getModified()
        );
        repository.save(suica);
    }

    private record SuicaRecord(
            MonthDay 月日, String in種別, String in利用場所, String out種別, String out利用場所, int 残高, int 入金_利用額
    ) {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd");

        public static SuicaRecord create(String[] line) {
            if (line.length != 7 && line.length != 6) {
                // TODO: Exception を適切なものに変更
                throw new java.lang.RuntimeException(String.format("Invalid line length. [length = %d]", line.length));
            }

            var 月日 = MonthDay.parse(line[0], FORMATTER);
            var in種別 = line[1];
            var in利用場所 = line[2].stripTrailing();
            var out種別 = line[3];
            var out利用場所 = line[4].stripTrailing();
            var 残高 = Integer.parseInt(line[5].replace(",", "").replace("\\", ""));
            var 入金_利用額 = 0;
            if (line.length >= 7) {
                入金_利用額 = Integer.parseInt(line[6].replace(",", ""));
            }

            return new SuicaRecord(月日, in種別, in利用場所, out種別, out利用場所, 残高, 入金_利用額);
        }

        public SuicaPK getPK() {
            return new SuicaPK(月日, in種別, in利用場所, out種別, out利用場所, 残高);
        }
    }
}
