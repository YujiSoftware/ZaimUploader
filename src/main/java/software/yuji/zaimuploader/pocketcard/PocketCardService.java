package software.yuji.zaimuploader.pocketcard;

import com.opencsv.CSVReader;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PocketCardService implements PaymentService {

    private final PocketCardRepository repository;

    private final AccountService accountService;

    private final GenreService genreService;

    private final Zaim zaim;

    public PocketCardService(PocketCardRepository repository, AccountService accountService, GenreService genreService, Zaim zaim) {
        this.repository = repository;
        this.accountService = accountService;
        this.genreService = genreService;
        this.zaim = zaim;
    }

    @Override
    public Payment[] readCSV(InputStream stream) {
        Account pocketCard = accountService.getPocketCard();
        Map<Genre, Category> mapping = genreService.getCategoryMapping();

        boolean skip = true;
        List<Payment> list = new ArrayList<>();
        InputStreamReader reader = new InputStreamReader(stream, Charset.forName("MS932"));
        CSVReader csv = new CSVReader(reader);
        for (String[] line : csv) {
            if (line.length == 0 || line[0].isEmpty()) {
                continue;
            }
            if (line[0].equals("ご利用年月日")) {
                skip = false;
                continue;
            }
            if (skip) {
                continue;
            }

            PocketCardRecord record = PocketCardRecord.create(line);
            if (repository.existsById(record.getPK())) {
                continue;
            }

            Genre genre = genreService.loadDefault(pocketCard, record.ご利用内容).orElse(null);

            list.add(new Payment(record, record.hashCode(), record.ご利用年月日.atStartOfDay(), record.ご利用内容, record.ご利用金額, genre, mapping));
        }

        return list.toArray(new Payment[0]);
    }

    @Override
    public int send(Payment[] payments) throws OAuthException, IOException {
        Account account = accountService.getPocketCard();

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
        PocketCardRecord record = (PocketCardRecord) payment.getRecord();

        PocketCard pocketCard = new PocketCard(
                record.getPK(),
                result.getMoney().getId(),
                result.getMoney().getModified()
        );
        repository.save(pocketCard);

        genreService.saveDefault(account, record.ご利用内容, payment.getGenre());
    }

    private record PocketCardRecord(
            LocalDate ご利用年月日, String ご利用内容, String 利用区分, int ご利用金額
    ) {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu/MM/dd");

        public static PocketCardRecord create(String[] line) {
            if (line.length != 5) {
                // TODO: Exception を適切なものに変更
                throw new java.lang.RuntimeException(String.format("Invalid line length. [length = %d]", line.length));
            }

            var ご利用年月日 = LocalDate.parse(line[0], FORMATTER);
            var ご利用内容 = line[1];
            var 利用区分 = line[2];
            var ご利用金額 = Integer.parseInt(line[3].replace(",", ""));

            return new PocketCardRecord(ご利用年月日, ご利用内容, 利用区分, ご利用金額);
        }

        public PocketCardPK getPK() {
            return new PocketCardPK(ご利用年月日, ご利用内容, 利用区分, ご利用金額);
        }
    }
}
