import java.awt.*;

/**
 * Created by Vadim on 15.12.16.
 */
public class Ball {

    private Vector position;
    private Vector speed;
    private Vector acceleration;
    private boolean isExpectingColision = false;
    private Vector expectedColisionPosition, nextSpeed;
    private double motion = 0.001;

    Ball(Point pos) {
        position = new Vector(pos);
        speed = new Vector(0, 0);
        acceleration = new Vector(0, 9.81);
        expectedColisionPosition = new Vector(100, 100);
        nextSpeed = new Vector(10, 10);
    }

    public void move() {
        if (isExpectingColision) {
            position = expectedColisionPosition;
            speed.PrintCoordinates();
            speed = nextSpeed;
            isExpectingColision = false;
            position.PrintCoordinates();
            speed.PrintCoordinates();
            System.out.println();
            return;
        }
        position.add(speed.getMultyplied(motion));
        speed.add(acceleration.getMultyplied(motion));
    }

    public Vector nextMovement() {
        if (isExpectingColision) return expectedColisionPosition;
        return position.getAdded(speed.getMultyplied(motion));
    }

    public void setAcceleration(Vector a) {
        acceleration = a;
    }

    public Vector getPosition() {return position;}

    public void expectColision(Vector pnt, Vector nextspeed) {
        isExpectingColision = true;
        expectedColisionPosition = pnt;
        nextSpeed = nextspeed;
    }

    public boolean ExpectingColision() {return isExpectingColision;}

    public Vector getSpeed() {return speed;}

}
