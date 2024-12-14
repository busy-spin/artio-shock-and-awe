package io.github.busy_spin.artio.initiator;

import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramIterationValue;

public class HistogramDemo {

    public static void main(String[] args) {
        Histogram histogram = new Histogram(2);

// Record some values
        histogram.recordValue(10);
        histogram.recordValue(100);
        histogram.recordValue(1000);
        histogram.recordValue(2048);

// Iterate through the histogram
        for (HistogramIterationValue value : histogram.recordedValues()) {
            System.out.println("Value Range: " + value.getValueIteratedTo());
            System.out.println("Count: " + value.getCountAtValueIteratedTo());
        }

        histogram.outputPercentileDistribution(System.out, 1.0);
    }
}
