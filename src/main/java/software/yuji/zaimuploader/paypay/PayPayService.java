package software.yuji.zaimuploader.paypay;

import org.springframework.stereotype.Service;
import software.yuji.zaimuploader.Payment;
import software.yuji.zaimuploader.PaymentService;
import software.yuji.zaimuploader.genre.Genre;
import software.yuji.zaimuploader.genre.GenreRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public final class PayPayService implements PaymentService {

    private final PayPayRepository payPayRepository;

    private final GenreRepository genreRepository;

    public PayPayService(PayPayRepository payPayRepository, GenreRepository genreRepository) {
        this.payPayRepository = payPayRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public Payment[] readCSV(InputStream stream) throws IOException {
        Map<Integer, Genre> map = new HashMap<>();
        for (Genre genre : genreRepository.findAll()) {
            map.put(genre.getId(), genre);
        }

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

            list.add(record.toPayment(map.get(10104)));
        }

        return list.toArray(new Payment[0]);
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

        public Payment toPayment(Genre genre) {
            return new Payment(id, dateTime, message, amount, genre);
        }
    }
}
