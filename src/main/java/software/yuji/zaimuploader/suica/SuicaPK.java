package software.yuji.zaimuploader.suica;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.MonthDay;

public class SuicaPK implements Serializable {
    @Column(nullable = false)
    private MonthDay 月日;

    @Column(nullable = false)
    private String in種別;

    @Column
    private String in利用場所;

    @Column
    private String out種別;

    @Column
    private String out利用場所;

    @Column(nullable = false)
    private int 残高;

    protected SuicaPK() {
    }

    public SuicaPK(MonthDay 月日, String in種別, String in利用場所, String out種別, String out利用場所, int 残高) {
        this.月日 = 月日;
        this.in種別 = in種別;
        this.in利用場所 = in利用場所;
        this.out種別 = out種別;
        this.out利用場所 = out利用場所;
        this.残高 = 残高;
    }

    public MonthDay get月日() {
        return 月日;
    }

    public void set月日(MonthDay 月日) {
        this.月日 = 月日;
    }

    public String getIn種別() {
        return in種別;
    }

    public void setIn種別(String in種別) {
        this.in種別 = in種別;
    }

    public String getIn利用場所() {
        return in利用場所;
    }

    public void setIn利用場所(String in利用場所) {
        this.in利用場所 = in利用場所;
    }

    public String getOut種別() {
        return out種別;
    }

    public void setOut種別(String out種別) {
        this.out種別 = out種別;
    }

    public String getOut利用場所() {
        return out利用場所;
    }

    public void setOut利用場所(String out利用場所) {
        this.out利用場所 = out利用場所;
    }

    public int get残高() {
        return 残高;
    }

    public void set残高(int 残高) {
        this.残高 = 残高;
    }
}
