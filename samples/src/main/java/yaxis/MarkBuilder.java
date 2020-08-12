package yaxis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MarkBuilder {
    private static final double TICK_DENSITY = 2.5; // constant


    private List<Mark> marks = new ArrayList<>();
    private PriceScale priceScale;
    private int base;
    private double currentFormatBase = 0;

    public MarkBuilder(PriceScale priceScale, int base) {
        this.priceScale = priceScale;
        this.base = base;
    }

    void rebuildTickMarks() {
        if (null == priceScale)
            throw new NullPointerException("scale is null");
        if (priceScale.isEmpty()) {
            marks = Collections.emptyList();
            return;
        }
        Source source = priceScale.getMainSource();
        double firstValue = source.getFirstValue();
        double height = priceScale.getHeight();
        double minPrice = priceScale.coordinateToPrice(height - 1, firstValue);
        double maxPrice = priceScale.coordinateToPrice(0, firstValue);
        maxPrice = Math.max(maxPrice, minPrice);
        minPrice = Math.min(maxPrice, minPrice);
        if (maxPrice != minPrice) {
            double tickSpan = this.tickSpan(minPrice, maxPrice);
            double s = maxPrice % tickSpan;
            if (s < 0) {
                s += tickSpan;
            }
            marks = new ArrayList<>();
            int direction = maxPrice >= minPrice ? 1 : -1;
            Double prevY = null;
            for (double currPrice = maxPrice - s; currPrice > minPrice; currPrice -= tickSpan) {
                double y = priceScale.priceToCoordinate(currPrice, firstValue);
                if (null == prevY || Math.abs(y - prevY) > getTickMarkHeight()) {
                    marks.add(new Mark(y, priceScale.formatPrice(currPrice, firstValue)));
                    prevY = y;
                    if (priceScale.isLog()) {
                        tickSpan = this.tickSpan(minPrice, currPrice * direction);
                    }
                }
            }
        }
    }

    private double getTickMarkHeight() {
        return Math.ceil(getFontSize() * TICK_DENSITY);
    }

    private double tickSpan(double low, double high) {
        if (high < low)
            throw new IllegalArgumentException("high < low");
        double height = priceScale.getHeight();
        double markPartPriceDelta = (high - low) * getTickMarkHeight() / height;

        A a1 = new A(base, Arrays.asList(2.0, 2.5, 2.0));
        A a2 = new A(base, Arrays.asList(2.0, 2.0, 2.5));
        A a3 = new A(base, Arrays.asList(2.5, 2.0, 2.0));

        double min = a1.tickSpan(high, low, markPartPriceDelta);
        min = Math.min(min, a2.tickSpan(high, low, markPartPriceDelta));
        min = Math.min(min, a3.tickSpan(high, low, markPartPriceDelta));
        return min;
    }

    private static boolean greaterOrEqual(double a, double b, double eps) {
        return b - a <= eps;
    }

    private static boolean equal(double a, double b, double eps) {
        return Math.abs(b - a) < eps;
    }

    private static boolean isBaseDecimal(int base) { // return whether or not `base` is a power of ten
        if (base < 0) {
            System.err.println("MathEx.isBaseDecimal: argument less zero");
            return false;
        }
        for (int t = base; t > 1; t /= 10) {
            if (t % 10 != 0)
                return false;
        }
        return true;
    }

    public static class A {
        private static final double TICK_SPAN_EPSILON = 1e-9;
        private final int base;
        private final List<Double> integralDividers;
        private final List<Double> fractionalDividers;

        A(int base, List<Double> integralDividers) {
            this.base = base;
            this.integralDividers = integralDividers;
            if (isBaseDecimal(base)) {
                this.fractionalDividers = Arrays.asList(2.0, 2.5, 2.0);
            } else {
                this.fractionalDividers = new ArrayList<>();
                for (int i = this.base; 1 != i; ) {
                    if (i % 2 == 0) {
                        this.fractionalDividers.add(2.0);
                        i /= 2;
                    } else {
                        if (i % 5 != 0)
                            throw new IllegalStateException("unexpected base");
                        fractionalDividers.add(2.0);
                        fractionalDividers.add(2.5);
                        i /= 5;
                    }
                    if (fractionalDividers.size() > 100)
                        throw new IllegalStateException("something wrong with base");
                }
            }
        }

        double tickSpan(double high, double low, double markPartPriceDelta) {
            double limit = 0 == this.base ? 0 : 1 / this.base;
            double epsilon = TICK_SPAN_EPSILON;
            double s = Math.pow(10, Math.max(0, Math.ceil(Math.log10(high - low))));
            double divider = integralDividers.get(0);
            for (int index = 0; s > limit + epsilon
                    && greaterOrEqual(s, limit, epsilon)
                    && greaterOrEqual(s, markPartPriceDelta * divider, epsilon) && greaterOrEqual(s, 1, epsilon);
                 ++index, divider = integralDividers.get(index % integralDividers.size())
                    ) {
                divider = integralDividers.get(index % integralDividers.size());
                s /= divider;
            }
            if (s <= limit + epsilon)
                s = limit;
            s = Math.max(1.0, s);
            if (this.fractionalDividers.size() > 0 && equal(s, 1.0, epsilon)) {
                divider = fractionalDividers.get(0);
                for (int index = 0;
                     s > limit + epsilon
                             && greaterOrEqual(s, markPartPriceDelta * divider, epsilon);
                     ++index, divider = fractionalDividers.get(index % this.fractionalDividers.size())
                        ) {
                    s /= divider;
                }
            }
            return s;
        }
    }

    public static class Mark {
        private final double y;
        private final String s;

        Mark(double y, String s) {
            this.y = y;
            this.s = s;
        }

    }

    public static class PriceRange {
        private final double minValue;
        private final double maxValue;

        PriceRange(double minValue, double maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public double getMinValue() {
            return minValue;
        }

        public double getMaxValue() {
            return maxValue;
        }
    }


    private static double getFontSize() {
        return 10;
    }
}
