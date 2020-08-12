package concurrency;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author Andrei Rybak
 */
public class ConcurrentMapTest {
	public static void main(String... args) throws InterruptedException, ExecutionException {
		Random r = new Random(System.currentTimeMillis());

		ConcurrentMap<Long, String> m = new ConcurrentHashMap<>();
		ExecutorService executorService = Executors.newFixedThreadPool(16);

		List<Callable<String>> tasks = new ArrayList<>();
		for (int i = 0; i < 1024; i++) {
//			 long delay = r.nextInt(100);
			final long tmp = i;
			tasks.add(() -> m.computeIfAbsent(tmp, k -> Long.toString(k)));
			tasks.add(() -> m.computeIfAbsent(tmp, k -> Long.toString(k)));
			tasks.add(() -> m.computeIfAbsent(tmp, k -> Long.toString(k)));
			tasks.add(() -> m.computeIfAbsent(tmp, k -> Long.toString(k)));
			tasks.add(() -> m.computeIfAbsent(tmp, k -> Long.toString(k)));
			tasks.add(() -> m.computeIfAbsent(tmp, k -> Long.toString(k)));
			tasks.add(() -> m.computeIfAbsent(tmp, k -> Long.toString(k)));
		}
		Collections.shuffle(tasks);
		System.out.println("Running " + tasks.size() + " tasks");

		List<Future<String>> futures = executorService.invokeAll(tasks);
		System.out.println("Waiting...");
		Thread.sleep(5000);
		executorService.shutdown();
		executorService.awaitTermination(2, TimeUnit.SECONDS);
		System.out.println("Getting...");
		for (Future<String> future : futures) future.get();
		System.out.println("Asserting...");
		for (Map.Entry<Long, String> longs : m.entrySet()) {
			long x = longs.getKey();
			String s = longs.getValue();
			if (!Long.toString(x).equals(s)) {
				throw new AssertionError("Not equal: " + s + " =/= " + x);
			} else {
				System.out.print(x);
				System.out.print(' ');
			}
		}
	}
}
