package time;

import java.time.Duration;
import java.time.LocalDate;

public class DurationBetweenLocalDates {
	public static void main(String... args) {
		LocalDate a = LocalDate.of(2020, 5, 1);
		LocalDate b = LocalDate.of(2020, 5, 10);
		System.out.println(Duration.between(b, a).toDays());

	}
}
