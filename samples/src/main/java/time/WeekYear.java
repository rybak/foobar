package time;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author Andrei Rybak
 */
public class WeekYear {
	public static void main(String... args) {
		DateTimeFormatter januaryFirstBased = DateTimeFormatter.ofPattern("yyyy");
		DateTimeFormatter weekBased = DateTimeFormatter.ofPattern("YYYY");
		for (int y = 2000; y <= 2025; y++) {
			for (int i = 26; i <= 31; i++) {
				LocalDate d = LocalDate.of(y, 12, i);
				String j = januaryFirstBased.format(d);
				String w = weekBased.format(d);
				if (!j.equals(w)) {
					System.out.println(d);
					System.out.println(weekBased.withLocale(Locale.US).format(d));
					System.out.println(weekBased.withLocale(Locale.UK).format(d));
					System.out.println();
				}
			}

		}
	}
}
