package software.yuji.zaimuploader.suica;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.yuji.zaimuploader.Payment;
import software.yuji.zaimuploader.category.Category;
import software.yuji.zaimuploader.category.CategoryMode;
import software.yuji.zaimuploader.genre.Genre;
import software.yuji.zaimuploader.genre.GenreService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class SuicaServiceTest {
    private static final String HEADER = "月日\t種別\t利用場所\t種別\t利用場所\t残高\t入金・利用額\n";

    private static final Category 交通 = new Category(103, CategoryMode.PAYMENT, "交通", 3);
    private static final Genre 電車 = new Genre(10301, "電車", 1, 交通);
    private static final Genre バス = new Genre(10301, "電車", 1, 交通);

    @MockBean(GenreService.class)
    private GenreService genreService;

    @Autowired
    private SuicaService service;

    @BeforeEach
    void beforeEach() {
        given(this.genreService.load("電車")).willReturn(電車);
        given(this.genreService.load("バス")).willReturn(バス);
    }

    @Test
    void readCSV_電車() throws IOException {
        String csv = HEADER + "07/17\t入\t生麦　　\t出\t京急品川　\t\\\\3,048\t-283";
        ByteArrayInputStream is = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        Payment[] payments = service.readCSV(is);

        assertEquals(1, payments.length);
        Payment payment = payments[0];
        assertEquals(LocalDate.now().withMonth(7).withDayOfMonth(17).atStartOfDay(), payment.getDateTime());
        assertEquals("生麦 ～ 京急品川", payment.getPlace());
        assertEquals(283, payment.getAmount());
        assertEquals(電車.getId(), payment.getGenreId());
    }

    @Test
    void readCSV_電車_スペース() throws IOException {
        String csv = HEADER + "05/27\t入\t永田町　\t出\t地　王子\t\\2,739\t-199";
        ByteArrayInputStream is = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        Payment[] payments = service.readCSV(is);

        assertEquals(1, payments.length);
        Payment payment = payments[0];
        assertEquals(LocalDate.now().withMonth(5).withDayOfMonth(27).atStartOfDay(), payment.getDateTime());
        assertEquals("永田町 ～ 地　王子", payment.getPlace());
        assertEquals(199, payment.getAmount());
        assertEquals(電車.getId(), payment.getGenreId());
    }

    @Test
    void readCSV_バス() throws IOException {
        String csv = HEADER + "04/03\tﾊﾞｽ等\t都電都Ｂ\t　\t\t\\2,181\t-210";
        ByteArrayInputStream is = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        Payment[] payments = service.readCSV(is);

        assertEquals(1, payments.length);
        Payment payment = payments[0];
        assertEquals(LocalDate.now().withMonth(4).withDayOfMonth(3).atStartOfDay(), payment.getDateTime());
        assertEquals("都電都Ｂ", payment.getPlace());
        assertEquals(210, payment.getAmount());
        assertEquals(バス.getId(), payment.getGenreId());
    }

    @Test
    void readCSV_物販() throws IOException {
        String csv = HEADER + "05/01\t物販\t\t　\t\t\\1,087\t-490";
        ByteArrayInputStream is = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        Payment[] payments = service.readCSV(is);

        assertEquals(1, payments.length);
        Payment payment = payments[0];
        assertEquals(LocalDate.now().withMonth(5).withDayOfMonth(1).atStartOfDay(), payment.getDateTime());
        assertEquals("物販", payment.getPlace());
        assertEquals(490, payment.getAmount());
        assertNull(payment.getGenreId());
    }

    @Test
    void readCSV_繰り越し() throws IOException {
        String csv = HEADER + "01/29\t繰\t\t　\t\t\\3,277\t\n";
        ByteArrayInputStream is = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        Payment[] payments = service.readCSV(is);

        assertEquals(0, payments.length);
    }
}