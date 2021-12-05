package software.yuji.zaimuploader.genre;

import software.yuji.zaimuploader.PaymentServiceId;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DefaultGenrePk implements Serializable {
    @Enumerated(EnumType.ORDINAL)
    private PaymentServiceId id;

    private String message;

    protected DefaultGenrePk() {
    }

    public DefaultGenrePk(PaymentServiceId id, String message) {
        this.id = id;
        this.message = message;
    }

    public PaymentServiceId getId() {
        return id;
    }

    public void setId(PaymentServiceId id) {
        this.id = id;
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
        return id == that.id && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message);
    }
}
