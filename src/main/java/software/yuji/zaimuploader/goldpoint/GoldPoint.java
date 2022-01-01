package software.yuji.zaimuploader.goldpoint;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class GoldPoint {

    @EmbeddedId()
    private GoldPointPK goldPointPK;

    @Column(unique = true, nullable = false)
    private Long zaimId;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected GoldPoint(){
    }

    public GoldPoint(GoldPointPK goldPointPK, Long zaimId, LocalDateTime createdAt) {
        this.goldPointPK = goldPointPK;
        this.zaimId = zaimId;
        this.createdAt = createdAt;
    }

    public GoldPointPK getGoldPointPK() {
        return goldPointPK;
    }

    public void setGoldPointPK(GoldPointPK goldPointPK) {
        this.goldPointPK = goldPointPK;
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
