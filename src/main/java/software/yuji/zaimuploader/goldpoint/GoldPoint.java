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

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public GoldPointPK getGoldPointPK() {
        return goldPointPK;
    }

    public void setGoldPointPK(GoldPointPK goldPointPK) {
        this.goldPointPK = goldPointPK;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
