package java_samples.time;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
public class SeveralDateFormatsParsing {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
        .appendPattern("M/d/yyyy[ h:mm]")
        .toFormatter();

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
        .appendPattern("M/d/yyyy[ H:mm]")
        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
        .toFormatter();

    private static long parseTime(String s) {
        try {
            LocalDateTime parsed = FORMATTER.parse(s, LocalDateTime::from);
            return parsed.atZone(ZoneId.of("CST")).toInstant().toEpochMilli();
        } catch (DateTimeParseException e) {
            System.err.println("Could not parse");
            System.err.println(e);
        }
        return 0;
    }

    public static void main(String[] args) {


        DateTimeFormatter manualOptional = new DateTimeFormatterBuilder()
            .appendPattern("M/d/yyyy")
            .appendOptional(
                new DateTimeFormatterBuilder()
                    .appendPattern(" h:mm")
                    .toFormatter()
            )
            .toFormatter();
        DateTimeFormatter patternWithOptional = new DateTimeFormatterBuilder()
            .appendPattern("M/d/yyyy[ h:mm]")
            .toFormatter();
        DateTimeFormatter defaulting = new DateTimeFormatterBuilder()
            .appendPattern("M/d/yyyy[ H:mm]") // fixed the HOUR parsing
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .toFormatter();
        // ------------------------
        String[] values = {
            "3/20/1995",
            "4/11/2020 1:20"
        };
        // ------------------------
        if (false) {
            LocalDateTime ldtWithoutTime = patternWithOptional.parse("4/11/1993", LocalDateTime::from);
            System.out.println(ldtWithoutTime);
            System.exit(0);
            TemporalAccessor tmp = patternWithOptional.parseBest("4/11/2020 1:20", LocalDateTime::from, LocalDate::from);
            System.out.println("Not working " + tmp);
            System.out.println(tmp.getClass().getSimpleName());
            System.out.println(patternWithOptional.parse("4/11/2020 1:20"));
            System.exit(0);
        }
        List<DateTimeFormatter> formatters = Arrays.asList(manualOptional, patternWithOptional, defaulting);
        for (DateTimeFormatter f : formatters) {
            for (String s : values)
                test(f, s);
        }
        if (false) {
            System.out.println(parse("3/20/1995"));
            System.out.println(parse("4/11/2020 1:20"));
        }
    }

    private static LocalDateTime parse(String s) {
        TemporalAccessor parsed = DATE_TIME_FORMATTER.parse(s);
        return LocalDateTime.of(
            LocalDate.from(parsed),
            LocalTime.of(
                parsed.isSupported(ChronoField.HOUR_OF_AMPM)
                    ? (int) ChronoField.HOUR_OF_AMPM.getFrom(parsed)
                    : 0,
                parsed.isSupported(ChronoField.MINUTE_OF_HOUR)
                    ? (int) ChronoField.MINUTE_OF_HOUR.getFrom(parsed)
                    : 0
            )
        );
    }

    private static void test(DateTimeFormatter formatter, String s) {
        try {
            LocalDateTime result = formatter.parse(s, LocalDateTime::from);
            System.out.println(result);
        } catch (DateTimeParseException e) {
            System.out.println("Could not parse: " + e.getMessage());
        }
    }

}
