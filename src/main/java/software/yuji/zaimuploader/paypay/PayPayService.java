package software.yuji.zaimuploader.paypay;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public final class PayPayService implements PaymentService {

    private final PayPayRepository payPayRepository;

    private final AccountService accountService;

    private final GenreService genreService;

    private final Zaim zaim;

    public PayPayService(PayPayRepository payPayRepository, AccountService accountService, GenreService genreService, Zaim zaim) {
        this.payPayRepository = payPayRepository;
        this.accountService = accountService;
        this.genreService = genreService;
        this.zaim = zaim;
    }

    @Override
    public Payment[] readCSV(InputStream stream) throws IOException {
        Account payPay = accountService.getPayPay();
        Map<Genre, Category> mapping = genreService.getCategoryMapping();

        String line;
        List<Payment> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        while ((line = reader.readLine()) != null) {
            PayPayRecord record = PayPayRecord.create(line.split(","));
            if (!record.status.equals("支払い完了")) {
                continue;
            }
            if (payPayRepository.existsById(record.id)) {
                continue;
            }

            // 例:
            // record.message = 吉野家に支払い
            // record.merchant = 秋葉原店
            String message = record.message.replace("に支払い", "");
            if (!record.merchant.isEmpty() && !message.equals(record.merchant)) {
                message += " (" + record.merchant + ")";
            }
            Genre genre = genreService.loadDefault(payPay, record.message).orElse(null);

            list.add(new Payment(record.id, record.dateTime, message, record.amount, genre, mapping));
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

            PayPay payPay = new PayPay(
                    payment.getId(),
                    result.getMoney().getId(),
                    result.getMoney().getModified()
            );
            payPayRepository.save(payPay);

            send++;
        }

        return send;
    }

    public record PayPayRecord(
            long id, String message, String merchant, String status, LocalDateTime dateTime, int amount
    ) {
        // ex. 2021年4月25日 18時09分
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu年M月d日 k時m分");

        public static PayPayRecord create(String[] line) {
            if (line.length != 6) {
                // TODO: Exception を適切なものに変更
                throw new java.lang.RuntimeException(String.format("Invalid line length. [length = %d]", line.length));
            }

            return new PayPayRecord(
                    Long.parseLong(line[0]),
                    line[1],
                    line[2],
                    line[3],
                    LocalDateTime.parse(line[4], FORMATTER),
                    Integer.parseInt(line[5])
            );
        }
    }
}
