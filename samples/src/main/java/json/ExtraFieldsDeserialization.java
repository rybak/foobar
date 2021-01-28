package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

public class ExtraFieldsDeserialization {
	public static void main(String... args) {
		Gson gson = new GsonBuilder().create();
		String s = gson.toJson(new Test(10, "10"));
		System.out.println(s);
		String withExtra = "{" +
			"\"n\":6789," +
			"\"notS\":\"Abacabadabacaba\"," +
			"\"foobar\":\"fghjk\"" +
			"}";
		Test res = gson.fromJson(withExtra, Test.class);
		System.out.println(res.getN());
		System.out.println(res.getS());
	}

	public static class Test {
		private final int n;
		private final String s;

		public Test(int n, String s) {
			this.n = n;
			this.s = Objects.requireNonNull(s);
		}

		public int getN() {
			return n;
		}

		public String getS() {
			return s;
		}
	}
}
