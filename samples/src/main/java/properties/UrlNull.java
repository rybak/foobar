package properties;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Andrei Rybak
 */
public class UrlNull {
	public static void main(String[] args) throws MalformedURLException {
		System.out.println(new URL(null).toString());
	}
}
