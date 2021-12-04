package software.yuji.zaim;

import software.yuji.zaim.genre.Genre;

import java.time.LocalDateTime;

public final class Payment {
    private final long id;
    private final LocalDateTime dateTime;
    private final String message;
    private final int amount;
    private final Genre genre;

    public Payment(long id, LocalDateTime dateTime, String message, int amount, Genre genre) {
        this.id = id;
        this.dateTime = dateTime;
        this.message = message;
        this.amount = amount;
        this.genre = genre;
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

    public Genre getGenre() {
        return genre;
    }
}
