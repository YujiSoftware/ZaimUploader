package software.yuji.zaimuploader.zaim;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class NameValuePair implements org.apache.http.NameValuePair {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd");

    private final String name;
    private final String value;

    public NameValuePair(String name, String value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");

        this.name = name;
        this.value = value;
    }

    public NameValuePair(String name, int value) {
        Objects.requireNonNull(name, "name");

        this.name = name;
        this.value = Integer.toString(value);
    }

    public NameValuePair(String name, LocalDateTime value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");

        this.name = name;
        this.value = FORMATTER.format(value);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }
}
