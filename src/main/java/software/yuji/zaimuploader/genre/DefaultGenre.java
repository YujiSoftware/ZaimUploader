package software.yuji.zaimuploader.genre;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class DefaultGenre {
    @Id
    private DefaultGenrePk pk;

    @OneToOne
    private Genre genre;

    protected DefaultGenre() {
    }

    public DefaultGenre(DefaultGenrePk pk, Genre genre) {
        this.pk = pk;
        this.genre = genre;
    }

    public DefaultGenrePk getPk() {
        return pk;
    }

    public void setPk(DefaultGenrePk pk) {
        this.pk = pk;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

}
