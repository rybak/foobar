package ramdom;

import java.util.*;

public class PercentDistributionSimulator {
	private final Random rnd;
	private final List<Integer> accumulatedPercents = new ArrayList<>();

	public PercentDistributionSimulator(List<Integer> percents) {
		this(percents, System.currentTimeMillis());
	}

	public PercentDistributionSimulator(List<Integer> percents, long seed) {
		if (percents.isEmpty())
			throw new IllegalArgumentException();
		if (percents.stream().mapToInt(i -> i).sum() != 100)
			throw new IllegalArgumentException();
		if (percents.stream().anyMatch(i -> i <= 0))
			throw new IllegalArgumentException();
		int s = 0;
		for (Integer prob : percents) {
			s += prob;
			accumulatedPercents.add(s);
		}
		assert percents.size() == accumulatedPercents.size();

		rnd = new Random(seed);
	}

	public int getRandomIndex() {
		int x = rnd.nextInt(100);
		for (int i = 0; i < accumulatedPercents.size(); i++) {
			if (x < accumulatedPercents.get(i))
				return i;
		}
		throw new AssertionError();
	}

	public static void main(String... args) {
		checkPercentDistribution(Arrays.asList(50, 50), 10_000);
		checkPercentDistribution(Arrays.asList(33, 67), 10_000);
		checkPercentDistribution(Arrays.asList(1, 99), 500_000);
		checkPercentDistribution(Arrays.asList(20, 60, 20), 200_000);
		checkPercentDistribution(Collections.nCopies(100, 1), 1000_000);
	}

	private static void checkPercentDistribution(List<Integer> percents, int numberOfExperiments) {
		System.out.println("Distribution: " + percents);
		PercentDistributionSimulator rnd = new PercentDistributionSimulator(percents);
		int[] counts = new int[percents.size()];
		for (int i = 0; i < numberOfExperiments; i++) {
			int x = rnd.getRandomIndex();
			counts[x]++;
		}
		System.out.println("Results:");
		System.out.println("\t" + Arrays.toString(counts));
		System.out.println("Results (percents):");
		for (int count : counts)
			System.out.println("\t" + ((1.0 * count) / numberOfExperiments) * 100.0);
	}
}
