package time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class SimpleDateFormatUsage {

    public static void main(String[] args) {
	// write your code here

        SimpleDateFormat dayParser = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d = dayParser.parse("2016-01-01T12:34");
            System.out.println(Instant.ofEpochMilli(d.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
