package yaxis;

public class PriceScale {

    private MarkBuilder.PriceRange priceRange = null;
    private Formatter formatter;
    private MarkBuilder markBuilder = new MarkBuilder(this, 100);

    public void updateFormatter() {
        int base = 100;
//        Object _marksCache = null;
        Source mainSource = this.getMainSource();
        if (mainSource != null)
            base = mainSource.getBase();
        this.formatter = null;
        if (isPercentage()) {
            this.formatter = new BFormatter();
        } else {
            this.formatter = mainSource != null ? mainSource.formatter() : new AFormatter(100, 1); // new y(100,1)
        }
        markBuilder = new MarkBuilder(this, base);
        markBuilder.rebuildTickMarks();
    }
    // y = i(26).PriceFormatter

    public double coordinateToPrice(double y, double firstValue) {
        if (!isValid())
            throw new IllegalStateException();
        if (isEmpty()) {
            return 0;
        }
        double invertedY = invertedCoordinate(y);
        double priceSpan = priceRange.getMaxValue() - priceRange.getMinValue();
        double bottomMarginHeight = getBottomMargin() * getHeight();
        double logical = this.priceRange.getMinValue() + priceSpan * ((invertedY - bottomMarginHeight) / (internalHeight() - 1));
        double price = this.logicalToPrice(logical);
        if (isPercentage())
            price = convertPercent(price, firstValue);
        return price;
    }

    public void setPriceRange(double e, double t, double i) {
        MarkBuilder.PriceRange o;
        o = this.priceRange;
        double n, r;
    }

    private boolean isPercentage() {
        return false;
    }

    private static double convertPercent(double price, double firstValue) { // TODO rename
        if (firstValue < 0)
            price = -price;
        return price / 100 * firstValue + firstValue;
    }

    private boolean isValid() {
        if (priceRange == null) // TODO ?
            return false;
        return true; // TODO
    }

    public double getHeight() {
        return 1000; // random
    }

    public double internalHeight() {
        return getHeight() * (1 - getBottomMargin() - getTopMargin());
    }

    private double getBottomMargin() {
        return 0.05; // 5% TODO check
    }

    private double getTopMargin() {
        return 0.05; // 5% TODO check
    }

    private double invertedCoordinate(double y) {
        return getHeight() - 1 - y;
    }

    public boolean isEmpty() {
        return false;
    }

    public Source getMainSource() {
        return new Source();
    }

    public double priceToCoordinate(double currPrice, double firstValue) {
        return Double.NaN; // TODO
    }

    public String formatPrice(double currPrice, double firstValue) {
        return Double.toString(currPrice); // TODO
    }

    public boolean isLog() {
        return false;
    }

    private double logicalToPrice(double price) {
        return isLog() ? fromLog(price) : price;
    }

    private double fromLog(double price) {
        final double _logicalOffset = 4.0; // const
        final double _coordOffset = 1e-4; // const;
        double absoluteValue = Math.abs(price);
        if (absoluteValue < 1e-9) {
            return 0;
        }
        double t = Math.pow(10, absoluteValue - _logicalOffset) - _coordOffset;
        return price < 0 ? -t : t;
    }


}
