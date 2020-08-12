import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Andrei Rybak
 */
public class ToStringLoop {
	public static void main(String... args) {
		List xs = new ArrayList();
		Collection wrapped = Collections.unmodifiableCollection(xs);
		xs.add(wrapped);
		System.out.println(xs);
	}
}
