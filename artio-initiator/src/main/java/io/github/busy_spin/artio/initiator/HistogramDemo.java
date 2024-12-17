package io.github.busy_spin.artio.initiator;

import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramIterationValue;

public class HistogramDemo {

    public static void main(String[] args) {
        Histogram histogram = new Histogram(1,
                200_000_000, 5);

// Record some values

        histogram.recordValue(0);
        histogram.recordValue(5000);
        histogram.recordValue(262143);
        histogram.recordValue(262142);

// Iterate through the histogram
        for (HistogramIterationValue value : histogram.recordedValues()) {
            System.out.println("Value Range: " + value.getValueIteratedTo());
            System.out.println("Count: " + value.getCountAtValueIteratedTo());
        }

        histogram.outputPercentileDistribution(System.out, 1.0);
    }
}
