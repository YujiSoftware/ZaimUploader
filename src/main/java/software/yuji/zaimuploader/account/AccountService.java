package software.yuji.zaimuploader.account;

import oauth.signpost.exception.OAuthException;
import org.springframework.stereotype.Service;
import software.yuji.zaimuploader.zaim.Zaim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AccountService {

    private static final String PAY_PAY = "PayPay";

    private static final String GOLD_POINT = "ヨドバシゴールドポイントカード・プラス";

    private final Zaim zaim;

    private final AccountRepository repository;

    public AccountService(Zaim zaim, AccountRepository repository) {
        this.zaim = zaim;
        this.repository = repository;
    }

    public void init() throws OAuthException, IOException {
        List<Account> saveEntities = new ArrayList<>();
        List<Account> deleteEntities = new ArrayList<>();
        for (Zaim.ZaimAccount account : zaim.getAccount()) {
            Account entity = new Account(
                    account.getId(),
                    account.getName(),
                    account.getSort()
            );
            if (account.getActive() == 1) {
                saveEntities.add(entity);
            } else {
                deleteEntities.add(entity);
            }
        }

        repository.saveAll(saveEntities);
        repository.deleteAll(deleteEntities);
    }

    public Account getPayPay() {
        return Objects.requireNonNull(repository.findByName(PAY_PAY));
    }

    public Account getGoldPoint() {
        return Objects.requireNonNull(repository.findByName(GOLD_POINT));
    }
}
