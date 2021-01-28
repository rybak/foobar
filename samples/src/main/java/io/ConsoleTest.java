package io;

import java.io.Console;

public class ConsoleTest {
	public static void main(String[] args) {
		Console console = System.console();

		char[] chars = console.readPassword("jira password> ");
		String pswd = new String(chars);

		System.out.println("password = " + pswd);
	}
}
