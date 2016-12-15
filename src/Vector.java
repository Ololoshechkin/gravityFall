import java.awt.*;

/**
 * Created by Vadim on 15.12.16.
 */
public class Vector {

    private double X, Y;
    private double eps = 1e-10;

    Vector(double x, double y) {
        X = x;
        Y = y;
    }

    Vector(Point p) {
        X = p.getX();
        Y = p.getY();
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public double getEps() {return eps;}

    public void add(Vector v) {
        X += v.getX();
        Y += v.getY();
    }

    public Vector getAdded(Vector v) {
        return new Vector(X + v.getX(), Y + v.getY());
    }

    public void subtract(Vector v) {
        X -= v.getX();
        Y -= v.getY();
    }

    public Vector getSubtracted(Vector v) {
        return new Vector(X - v.getX(), Y - v.getY());
    }

    public double len() {
        return Math.sqrt(X * X + Y * Y);
    }

    public void normalize() {
        double L = len();
        if (L < eps) {
            X = 0.0;
            Y = 0.0;
        }
        else {
            X /= L;
            Y /= L;
        }
    }

    public void multyply(double alpha) {
        X *= alpha;
        Y *= alpha;
    }

    public double scalarlyMultiply(Vector v) {
        return X * v.getX() + Y * v.getY();
    }

    public Vector getMultyplied(double alpha) {
        return new Vector(X * alpha, Y * alpha);
    }

    public double getProjectionTo(Vector v) {
        Vector v0 = new Vector(v.getX(), v.getY());
        v0.normalize();
        return scalarlyMultiply(v0);
    }

    public void rotate() {
        double x = - Y, y = X;
        X = x;
        Y = y;
    }

    public double crossProduct(Vector v) {
        return X * v.getY() - Y * v.getX();
    }

    public Point toPoint() {
        return new Point((int)X, (int)Y);
    }

    public void PrintCoordinates() {
        System.out.println("X = " + X + " , Y = " + Y);
    }

    public Vector getNormalized() {
        double L = len();
        if (L < eps) {
            return new Vector(0.0, 0.0);
        }
        else {
            return new Vector(X / L, Y / L);
        }
    }

}
