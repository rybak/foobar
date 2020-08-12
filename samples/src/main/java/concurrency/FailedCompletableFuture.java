package concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Andrei Rybak
 */
public class FailedCompletableFuture {
	public static void main(String... args) {
		CompletableFuture<String> test = CompletableFuture.supplyAsync(() -> {
			throw new IllegalArgumentException("test");
		});
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Done:          " + test.isDone());
		System.out.println("Exceptionally: " + test.isCompletedExceptionally());
		try {
			System.out.println(test.get());
		} catch (InterruptedException | ExecutionException e) {
			System.out.println(e.getMessage());
			System.out.flush();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
}
