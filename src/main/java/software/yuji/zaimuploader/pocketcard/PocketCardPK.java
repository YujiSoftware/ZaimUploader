package software.yuji.zaimuploader.pocketcard;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
public class PocketCardPK implements Serializable {
    @Column(nullable = false)
    private LocalDate ご利用年月日;

    @Column(nullable = false)
    private String ご利用内容;

    @Column(nullable = false)
    private String 利用区分;

    @Column(nullable = false)
    private int ご利用金額;

    protected PocketCardPK() {
    }

    public PocketCardPK(LocalDate ご利用年月日, String ご利用内容, String 利用区分, int ご利用金額) {
        this.ご利用年月日 = ご利用年月日;
        this.ご利用内容 = ご利用内容;
        this.利用区分 = 利用区分;
        this.ご利用金額 = ご利用金額;
    }

    public LocalDate getご利用年月日() {
        return ご利用年月日;
    }

    public void setご利用年月日(LocalDate ご利用年月日) {
        this.ご利用年月日 = ご利用年月日;
    }

    public String getご利用内容() {
        return ご利用内容;
    }

    public void setご利用内容(String ご利用内容) {
        this.ご利用内容 = ご利用内容;
    }

    public String get利用区分() {
        return 利用区分;
    }

    public void set利用区分(String 利用区分) {
        this.利用区分 = 利用区分;
    }

    public int getご利用金額() {
        return ご利用金額;
    }

    public void setご利用金額(int ご利用金額) {
        this.ご利用金額 = ご利用金額;
    }
}
