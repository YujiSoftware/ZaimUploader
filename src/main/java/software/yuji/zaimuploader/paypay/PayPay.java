package software.yuji.zaimuploader.paypay;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PayPay {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long zaimId;

    @CreationTimestamp
    @ColumnDefault("now()")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected PayPay() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getZaimId() {
        return zaimId;
    }

    public void setZaimId(Long zaimId) {
        this.zaimId = zaimId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
