package utils;

/**
 * Created by Cary on 11/19/15.
 */
public class TemperatureSingleton {

    private static TemperatureSingleton instance = null;

    public static TemperatureSingleton getInstance() {
        if (instance == null) {
            instance = new TemperatureSingleton();
        }

        return instance;
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
