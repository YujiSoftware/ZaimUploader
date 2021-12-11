package software.yuji.zaimuploader.paypay;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class PayPay {

    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private Long zaimId;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected PayPay() {
    }

    public PayPay(Long id, Long zaimId, LocalDateTime createdAt) {
        this.id = id;
        this.zaimId = zaimId;
        this.createdAt = createdAt;
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
