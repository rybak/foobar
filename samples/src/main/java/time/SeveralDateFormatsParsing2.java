package time;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

public class SeveralDateFormatsParsing2 {
    public static void main(String[] args) {
        DateTimeFormatter f = new DateTimeFormatterBuilder()
            .appendPattern("M/dd/YYYY[ h:mm]")
            .toFormatter();
        TemporalAccessor parsed = f.parse("4/11/2020 1:40");
        Long queriedHour = parsed.query(ChronoField.HOUR_OF_AMPM::getFrom);
        System.out.println(queriedHour);

        parsed = f.parse("3/12/1995");
        queriedHour = parsed.query(ChronoField.HOUR_OF_AMPM::getFrom);
        System.out.println(queriedHour);
    }
}
