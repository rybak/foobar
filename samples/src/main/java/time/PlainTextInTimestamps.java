package time;

import java.time.format.DateTimeFormatterBuilder;

public class PlainTextInTimestamps {
	public static void main(String... args) {
		new DateTimeFormatterBuilder().appendLiteral("xxx foobar").toFormatter();
	}
}
