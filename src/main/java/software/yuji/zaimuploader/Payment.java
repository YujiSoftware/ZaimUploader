package software.yuji.zaimuploader;

import java.time.LocalDateTime;

public final class Payment {
    private final long id;
    private final LocalDateTime dateTime;
    private final String message;
    private final int amount;
    private Integer genreId;
    private boolean accept = true;

    public Payment(long id, LocalDateTime dateTime, String message, int amount, Integer genreId) {
        this.id = id;
        this.dateTime = dateTime;
        this.message = message;
        this.amount = amount;
        this.genreId = genreId;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getMessage() {
        return message;
    }

    public int getAmount() {
        return amount;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }
}
