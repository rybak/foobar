package properties;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlNull {
	public static void main(String[] args) throws MalformedURLException {
		System.out.println(new URL(null).toString());
	}
}
