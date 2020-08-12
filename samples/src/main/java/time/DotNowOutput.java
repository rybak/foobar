package time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author Andrei Rybak
 */
public class DotNowOutput {
	public static void main(String[] args) {
		System.out.println(LocalDate.now());
		System.out.println(LocalDateTime.now());
		System.out.println(Instant.now());
		System.out.println(ZonedDateTime.now());
	}
}
