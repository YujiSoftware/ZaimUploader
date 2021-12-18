package software.yuji.zaimuploader.goldpoint;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class GoldPointPK implements Serializable {
    @Column(nullable = false)
    private LocalDateTime 利用日;

    @Column(nullable = false)
    private String 利用店名;

    @Column(nullable = false)
    private String 請求対象;

    @Column(nullable = false)
    private int 利用金額;

    protected GoldPointPK() {
    }

    public GoldPointPK(LocalDateTime 利用日, String 利用店名, String 請求対象, int 利用金額) {
        this.利用日 = 利用日;
        this.利用店名 = 利用店名;
        this.請求対象 = 請求対象;
        this.利用金額 = 利用金額;
    }

    public LocalDateTime get利用日() {
        return 利用日;
    }

    public void set利用日(LocalDateTime 利用日) {
        this.利用日 = 利用日;
    }

    public String get利用店名() {
        return 利用店名;
    }

    public void set利用店名(String 利用店名) {
        this.利用店名 = 利用店名;
    }

    public String get請求対象() {
        return 請求対象;
    }

    public void set請求対象(String 請求対象) {
        this.請求対象 = 請求対象;
    }

    public int get利用金額() {
        return 利用金額;
    }

    public void set利用金額(int 利用金額) {
        this.利用金額 = 利用金額;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoldPointPK that = (GoldPointPK) o;
        return 利用金額 == that.利用金額 && Objects.equals(利用日, that.利用日) && Objects.equals(利用店名, that.利用店名) && Objects.equals(請求対象, that.請求対象);
    }

    @Override
    public int hashCode() {
        return Objects.hash(利用日, 利用店名, 請求対象, 利用金額);
    }
}
