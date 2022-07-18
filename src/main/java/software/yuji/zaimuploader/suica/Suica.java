package software.yuji.zaimuploader.suica;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class Suica {
    @EmbeddedId
    private SuicaPK suicaPK;

    @Column(unique = true, nullable = false)
    private Long zaimId;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Suica() {
    }

    public Suica(SuicaPK suicaPK, Long zaimId, LocalDateTime createdAt) {
        this.suicaPK = suicaPK;
        this.zaimId = zaimId;
        this.createdAt = createdAt;
    }

    public SuicaPK getSuicaPK() {
        return suicaPK;
    }

    public void setSuicaPK(SuicaPK suicaPK) {
        this.suicaPK = suicaPK;
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
