package common.model;

import java.io.Serializable;

/**
 * Координаты элемента коллекции.
 */
public class Coordinates implements Serializable {

    private static final long serialVersionUID = 1L;

    private double x;
    private Float y;

    public Coordinates() {
    }

    public Coordinates(double x, Float y) {
        setX(x);
        setY(y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        if (y == null) {
            throw new IllegalArgumentException("Координата Y не может быть null.");
        }
        if (y > 968) {
            throw new IllegalArgumentException("Координата Y не может превышать 968.");
        }
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordinates { x = " + x + ", y = " + y + " }";
    }
}
