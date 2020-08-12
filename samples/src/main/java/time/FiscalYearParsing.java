package time;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class FiscalYearParsing {
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
        .appendPattern("M/d/yyyy[H:mm]")
        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
        .toFormatter();

    public static void main(String[] args) {
        String s = "2/27/2012 10:50";
        System.out.println(FORMATTER.parse(s));
        LocalDateTime localDateTime = FORMATTER.parse(s, LocalDateTime::from);
        System.out.println(localDateTime);
        System.out.println(localDateTime.atZone(ZoneId.of("EST", ZoneId.SHORT_IDS)));
    }
}
