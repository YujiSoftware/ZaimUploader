package software.yuji.zaimuploader;

import software.yuji.zaimuploader.category.Category;
import software.yuji.zaimuploader.genre.Genre;

import java.time.LocalDateTime;
import java.util.Map;

public final class Payment {
    private final Object record;
    private final Map<Genre, Category> mapping;
    private final long id;
    private final LocalDateTime dateTime;
    private final String place;
    private final int amount;

    private Genre genre;

    public Payment(Object record, long id, LocalDateTime dateTime, String place, int amount, Genre genre, Map<Genre, Category> mapping) {
        this.record = record;
        this.mapping = mapping;

        this.id = id;
        this.dateTime = dateTime;
        this.place = place;
        this.amount = amount;
        this.genre = genre;
    }

    public Object getRecord() {
        return record;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getPlace() {
        return place;
    }

    public int getAmount() {
        return amount;
    }

    public Category getCategory() {
        return mapping.get(genre);
    }

    public Genre getGenre() {
        return genre;
    }

    public Integer getGenreId() {
        if (genre == null) {
            return null;
        }

        return genre.getId();
    }

    public void setGenreId(Integer genreId) {
        if (genreId == null) {
            genre = null;
            return;
        }

        // TODO: 線形探索をやめる
        int id = genreId;
        for (Genre genre : mapping.keySet()) {
            if (genre.getId() == id) {
                this.genre = genre;
                break;
            }
        }
    }
}
