package software.yuji.zaim;

import software.yuji.zaim.genre.Genre;

import java.time.LocalDateTime;

public record Payment(LocalDateTime dateTime, String message, int amount, Genre genre) {
}
