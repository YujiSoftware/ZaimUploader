package software.yuji.zaimuploader.pocketcard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.yuji.zaimuploader.Payment;
import software.yuji.zaimuploader.account.Account;
import software.yuji.zaimuploader.account.AccountService;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class PocketCardServiceTest {

    private static final Account ポケットカード = new Account(16676356, "ポケットカード", 16);

    @MockBean(AccountService.class)
    private AccountService accountService;

    @Autowired
    private PocketCardService service;

    @BeforeEach
    void beforeEach() {
        given(this.accountService.getPocketCard()).willReturn(ポケットカード);
    }

    @Test
    void readCSV() {
        String csv = """
                カード名,今回ご請求金額,お支払日,お支払いコース
                ファミマＴカード（クレジット）,"3,000",2023年02月01日,口座引落しコース（ずっと全額支払い選択）
                        
                        
                ご利用年月日,ご利用内容/店名,利用区分,ご利用金額（円）,備考　<海外ご利用分は現地通貨額／換算レート(円)>
                2022/12/28,ＦａｍｉＰａｙチャージ,SP,"3,000",
                ,,,,
                        
                """;

        ByteArrayInputStream is = new ByteArrayInputStream(csv.getBytes(Charset.forName("MS932")));
        Payment[] payments = service.readCSV(is);

        assertEquals(1, payments.length);
        Payment payment = payments[0];
        assertEquals(LocalDateTime.of(2022, 12, 28, 0, 0), payment.getDateTime());
        assertEquals("ＦａｍｉＰａｙチャージ", payment.getPlace());
        assertEquals(3000, payment.getAmount());
        assertNull(payment.getGenre());
    }
}