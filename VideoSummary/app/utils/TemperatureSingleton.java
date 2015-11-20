package utils;

/**
 * Created by Cary on 11/19/15.
 */
public class TemperatureSingleton {

    private static TemperatureSingleton self = null;

    public static TemperatureSingleton getInstance() {
        if (self == null) {
            self = new TemperatureSingleton();
        }

        return self;
    }

    private TemperatureSingleton() {

    }

    public double cTof(double c) {
        return (c * (9/5)) + 32;
    }

    public double fToc(double f) {
        return (f - 32) * (5/9);
    }
}
