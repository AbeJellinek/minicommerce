package me.abje.minicommerce.db;

import javax.persistence.Entity;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;

@Entity
public class Message extends ModelBase {
    private Instant time;
    private Level level;
    private String value;

    public Message(Level level, String value) {
        this.time = Instant.now();
        this.level = level;
        this.value = value;
    }

    protected Message() {
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getPrettyTime() {
        return DateTimeFormatter.
                ofLocalizedDateTime(FormatStyle.SHORT).
                withZone(ZoneId.systemDefault()).
                format(time);
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Message message = (Message) o;
        return Objects.equals(time, message.time) &&
                Objects.equals(level, message.level) &&
                Objects.equals(value, message.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), time, level, value);
    }

    @Override
    public String toString() {
        return "Message{" +
                "time=" + time +
                ", level=" + level +
                ", value='" + value + '\'' +
                "} " + super.toString();
    }

    public enum Level {
        INFO("none"),
        SUCCESS("success"),
        WARNING("warning"),
        ERROR("danger");

        private final String color;

        Level(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }
}
