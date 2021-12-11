package software.yuji.zaimuploader.genre;

import software.yuji.zaimuploader.account.Account;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DefaultGenrePk implements Serializable {
    @ManyToOne
    private Account account;

    private String message;

    protected DefaultGenrePk() {
    }

    public DefaultGenrePk(Account account, String message) {
        this.account = account;
        this.message = message;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultGenrePk that = (DefaultGenrePk) o;
        return account == that.account && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, message);
    }
}
