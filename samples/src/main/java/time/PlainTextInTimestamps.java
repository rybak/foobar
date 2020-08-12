package time;

import java.time.format.DateTimeFormatterBuilder;

/**
 * @author Andrei Rybak
 */
public class PlainTextInTimestamps {
	public static void main(String... args) {
		new DateTimeFormatterBuilder().appendLiteral("xxx foobar").toFormatter();
	}
}
