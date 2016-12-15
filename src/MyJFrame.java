import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Vadim on 15.12.16.
 */
public class MyJFrame extends JFrame {
    private Point lastPoint;
    private double[] YPos = new double[900];
    private ArrayList<Point> points;
    private Ball ball = new Ball(new Point(0, 0));
    private boolean ballCreated = false;

    public double getYpos(int x) {
        return YPos[x];
    }

    private double getYByXOnLine(int x, double x1, double y1, double x2, double y2) {
        // A = (x2 - x1) / (y2 - y1) = (x - x1) / (y - y1), dx = x - x1
        // y = y1 + dx / A
        double A = (x2 - x1) / (y2 - y1), dx = (x - x1);
        return y1 + dx / A;
    }

    public void paint(Graphics g) {

    }

    public void drawLine(Point startPoint, Point destinationPoint) {
        Graphics g = getGraphics();
        g.setColor(Color.RED);
        g.drawLine((int)startPoint.getX(), (int)startPoint.getY(), (int)destinationPoint.getX(), (int)destinationPoint.getY());
    }

    public void drawLine(Point startPoint, Point destinationPoint, Color color) {
        Graphics g = getGraphics();
        g.setColor(color);
        g.drawLine((int)startPoint.getX(), (int)startPoint.getY(), (int)destinationPoint.getX(), (int)destinationPoint.getY());
    }

    MyJFrame(String s, int x0, int y0, int W, int H) {
        super(s);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(x0, y0, W, H);
        YPos = new double[W];
        points = new ArrayList<Point>();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.isMetaDown()) {
                    /*
                        Vector n = getNormalVector(e.getX());
                        n.multyply(100.0);
                        if (n.getY() > 0.0) n.multyply(-1.0);
                        Vector inGR = new Vector(e.getX(), YPos[(int)e.getX()]);
                        Vector p = inGR.getAdded(n);
                        drawLine(inGR.toPoint(), p.toPoint(), Color.CYAN);
                    */
                    if (!ballCreated) {
                        ball = new Ball(e.getPoint());
                        ballCreated = true;
                    }
                    System.out.println("ПКМ1");
                } else {
                    lastPoint = e.getPoint();
                    points.add(e.getPoint());
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(e.isMetaDown()) {
                    //System.out.println("ПКМ");
                } else {
                    drawLine(lastPoint, e.getPoint());
                    Point startPoint, destinationPoint;
                    if (lastPoint.getX() < e.getPoint().getX()) {
                        startPoint = lastPoint;
                        destinationPoint = e.getPoint();
                    } else {
                        destinationPoint = lastPoint;
                        startPoint = e.getPoint();
                    }
                    for (int x = (int) startPoint.getX(); x <= (int) destinationPoint.getX(); ++x) {
                        YPos[x] = getYByXOnLine(x, startPoint.getX(), startPoint.getY(), destinationPoint.getX(), destinationPoint.getY());
                        points.add(e.getPoint());
                    }
                    lastPoint = e.getPoint();
                }
            }
        });
        setVisible(true);
    }

    public double getDerivative(double x) {
        return (YPos[(int) x + 2] - YPos[(int) x]) / 2.0;
    }

    public Vector getNormalVector(double x) {
        Vector n = new Vector(1.0, getDerivative(x));
        n.rotate();
        if (n.getY() > 0) n.multyply(-1.0);
        n.normalize();
        return n;
    }

    int predictIntersection() {
        Vector p1 = ball.getPosition();
        Vector p2 = ball.nextMovement();
        for (int i = 0; i < points.size() - 3; ++i) {
            Vector l1 = new Vector(points.get(i));
            Vector l2 = new Vector(points.get(i + 3));
            /*System.out.println("i = " + i);
            l1.PrintCoordinates();
            l2.PrintCoordinates();
            System.out.println();*/
            if ((l2.getSubtracted(l1)).crossProduct(p2.getSubtracted(l1)) * l2.getSubtracted(l1).crossProduct(p1.getSubtracted(l1)) >= -l2.getEps()) continue;
            if ((p2.getSubtracted(p1)).crossProduct(l2.getSubtracted(p1)) * p2.getSubtracted(p1).crossProduct(l1.getSubtracted(p1)) >= -p2.getEps()) continue;
            return i;
        }
        return -1;
    }

    Vector predictableIntersectionPosition(int i) {
        Vector p1 = ball.getPosition();
        Vector p2 = ball.nextMovement();
        Vector l1 = new Vector(points.get(i));
        Vector l2 = new Vector(points.get(i + 3));
        Vector vp = p2.getSubtracted(p1);
        Vector vl = l2.getSubtracted(l1);
        double alphaP = (l1.crossProduct(vl) - p1.crossProduct(vl)) / (vp.crossProduct(vl));
        Vector intersection = p1.getAdded(vp.getMultyplied(alphaP));

        Vector displacement = intersection.getSubtracted(ball.getPosition());
        double len = displacement.len();
        intersection = ball.getPosition().getAdded(displacement.getMultyplied(len / displacement.len()));

        return intersection;
    }

    Vector predictableSpeedAfterColisionIn(Vector colisionPosition) {
        Vector n = getNormalVector(colisionPosition.getX());
        double L = ball.getSpeed().getProjectionTo(n);
        n.multyply(-L);
        Vector v = ball.getSpeed().getAdded(n.getMultyplied(2.0));
        return v;
    }

    public void update() {
        if (ballCreated) {
            int intersectionIndex = predictIntersection();
            if (intersectionIndex != -1 && !ball.ExpectingColision()) {
                Graphics g = getGraphics();
                g.drawRoundRect((int) points.get(intersectionIndex).getX(), (int) points.get(intersectionIndex).getY(), 10, 10, 5, 5);
                Vector colisionPosition = predictableIntersectionPosition(intersectionIndex);
                ball.expectColision(colisionPosition, predictableSpeedAfterColisionIn(colisionPosition));
            }
            ball.move();
        }
    }

    public void clearFrame() {
        Graphics g = getGraphics();
        //g.setColor(Color.white);
        //g.drawRect(0, 0, 1000, 1000);
        g.clearRect(0, 0, 1000, 600);
    }

    public void drawRound(double x0, double y0, int r) {
        Graphics g = getGraphics();
        g.drawRoundRect((int)x0, (int)y0, r, r, r, r);
    }

    public ArrayList<Point> getPoints() {return points;}

    public Ball getBall() {return ball;}
}
