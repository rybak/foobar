package market;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Andrei Rybak
 */
public class VolatilityCalcExperiment {
    private static final int SYMBOL_COUNT = 3;
    private static final int RECORDS_COUNT = 1000;
    private static final double[] W = {1.0, -0.5, -0.1};
    private static final double MAX_VOLATILITY = 100.0;


    public static void main(String[] args) {
        List<List<Double>> volBySymbol = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < SYMBOL_COUNT; i++) {
            ArrayList<Double> volatilities = new ArrayList<>();
            volBySymbol.add(volatilities);
            for (int j = 0; j < RECORDS_COUNT; j++) {
                volatilities.add(random.nextDouble() * MAX_VOLATILITY);
            }
        }

        System.out.println(volBySymbol.stream()
            .map(Object::toString)
            .collect(Collectors.joining("\n")));

        final double totatWeight = Arrays.stream(W).map(Math::abs).sum();
        for (int j = 0; j < RECORDS_COUNT; j++) {
            double vol = 0;
            for (int i = 0; i < SYMBOL_COUNT; i++) {
                vol += volBySymbol.get(i).get(j) * Math.abs(W[i]);
            }
            vol /= totatWeight;
            if (vol < 0 || vol > MAX_VOLATILITY) {
                System.out.println("FAILED");
                System.out.println(vol);
                System.exit(1);
            }
        }

    }
}
