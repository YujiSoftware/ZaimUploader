package software.yuji.zaimuploader.pocketcard;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class PocketCard {
    @EmbeddedId
    private PocketCardPK pocketCardPK;

    @Column(unique = true, nullable = false)
    private Long zaimId;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected PocketCard() {
    }

    public PocketCard(PocketCardPK pocketCardPK, Long zaimId, LocalDateTime createdAt) {
        this.pocketCardPK = pocketCardPK;
        this.zaimId = zaimId;
        this.createdAt = createdAt;
    }

    public PocketCardPK getPocketCardPK() {
        return pocketCardPK;
    }

    public void setPocketCardPK(PocketCardPK pocketCardPK) {
        this.pocketCardPK = pocketCardPK;
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
