package org.jboss.as.console.client.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/23/11
 */
public class Stats {

    public static double[] Quartiles(List<Double> values)
    {
        if (values.size() < 3) return new double[] {0,0};

        double median = Median(values);

        List<Double> lowerHalf = GetValuesLessThan(values, median, true);
        List<Double> upperHalf = GetValuesGreaterThan(values, median, true);

        return new double[] {Median(lowerHalf), median, Median(upperHalf)};
    }

    public static List<Double> GetValuesGreaterThan(List<Double> values, double limit, boolean orEqualTo)
    {
        ArrayList<Double> modValues = new ArrayList<Double>();

        for (double value : values)
            if (value > limit || (value == limit && orEqualTo))
                modValues.add(value);

        return modValues;
    }

    public static List<Double> GetValuesLessThan(List<Double> values, double limit, boolean orEqualTo)
    {
        ArrayList<Double> modValues = new ArrayList<Double>();

        for (double value : values)
            if (value < limit || (value == limit && orEqualTo))
                modValues.add(value);

        return modValues;
    }

    public static double Median(List<Double> values)
    {
        Collections.sort(values);

        if (values.size() % 2 == 1)
            return values.get((values.size()+1)/2-1);
        else
        {
            double lower = values.get(values.size()/2-1);
            double upper = values.get(values.size()/2);

            return (lower + upper) / 2.0;
        }
    }
}
