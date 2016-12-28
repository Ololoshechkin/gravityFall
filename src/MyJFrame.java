import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Vadim on 15.12.16.?|
 */
public class MyJFrame extends JFrame {
    private Point lastPoint;
    private ArrayList<ArrayList<Point>> points;
    private Ball ball = new Ball(new Point(0, 0));
    private boolean ballCreated = false;


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
        points = new ArrayList<ArrayList<Point>>();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.isMetaDown()) {
                    if (true || !ballCreated) {
                        ball = new Ball(e.getPoint());
                        ballCreated = true;
                    }
                } else {
                    points.add(new ArrayList<Point>());
                    ArrayList<Point> lastList = points.get(points.size() - 1);
                    lastList.add(e.getPoint());
                    points.set(points.size() - 1, lastList);
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(e.isMetaDown()) {
                    //System.out.println("ПКМ");
                } else {
                    Vector curPoint = new Vector(e.getPoint());
                    Vector lastPoint = new Vector(points.get(points.size() - 1).get(points.get(points.size() - 1).size() - 1));
                    if (curPoint.distTo(lastPoint) > 0.1) {
                        ArrayList<Point> last = points.get(points.size() - 1);
                        last.add(curPoint.toPoint());
                        points.set(points.size() - 1, last);
                    }
                }
            }
        });
        setVisible(true);
    }

    private double getDirivative(int i, int j) {
        double middleXbefore = 0.0;
        double middleXafter = 0.0;
        double middleYbefore = 0.0;
        double middleYafter = 0.0;
        for (int d = 0; d <= 2; ++d) {
            if (j - d >= 0) middleXbefore += points.get(i).get(j - d).getX();
            if (j - d >= 0) middleYbefore += points.get(i).get(j - d).getY();
        }
        for (int d = 0; d <= 2; ++d) {
            if (j + d < points.get(i).size()) middleXafter += points.get(i).get(j + d).getX();
            if (j + d < points.get(i).size()) middleYafter += points.get(i).get(j + d).getY();
        }
        double dy = (middleYafter - middleYbefore);
        double dx = (middleXafter - middleXbefore);
        if (dx == 0.0) return 1000.0;
        else return dy / dx;
    }

    private Vector getNormalVector(int i, int j) {
        System.out.println("dirivative = " + getDirivative(i, j));
        Vector n = new Vector(1.0, getDirivative(i, j));
        n.rotate();
        n.normalize();
        if (n.getY() > 0) n.multyply(-1.0);
        System.out.print("n : ");
        n.PrintCoordinates();
        System.out.println();
        return n;
    }

    private ArrayList<Pair<Integer, Integer>> predictIntersections() {
        ArrayList ans = new ArrayList();
        Vector p1 = ball.getPosition();
        Vector p2 = ball.nextMovement();
        for (int i = 0; i < points.size(); ++i) {
            for (int j = 0; j < points.get(i).size() - 1; ++j) {
                Vector l1 = new Vector(points.get(i).get(j));
                Vector l2 = new Vector(points.get(i).get(j + 1));
                if ((l2.getSubtracted(l1)).crossProduct(p2.getSubtracted(l1)) * l2.getSubtracted(l1).crossProduct(p1.getSubtracted(l1)) >= 0)
                    continue;
                if ((p2.getSubtracted(p1)).crossProduct(l2.getSubtracted(p1)) * p2.getSubtracted(p1).crossProduct(l1.getSubtracted(p1)) >= 0)
                    continue;
                ans.add(new Pair<Integer, Integer>(i, j));
            }
            for (int j = 0; j < points.get(i).size() - 2; ++j) {
                Vector l1 = new Vector(points.get(i).get(j));
                Vector l2 = new Vector(points.get(i).get(j + 2));
                if ((l2.getSubtracted(l1)).crossProduct(p2.getSubtracted(l1)) * l2.getSubtracted(l1).crossProduct(p1.getSubtracted(l1)) >= 0)
                    continue;
                if ((p2.getSubtracted(p1)).crossProduct(l2.getSubtracted(p1)) * p2.getSubtracted(p1).crossProduct(l1.getSubtracted(p1)) >= 0)
                    continue;
                ans.add(new Pair<Integer, Integer>(i, j));
            }
        }
        return ans;
    }

    private  Vector predictableIntersectionPosition(int i, int j) {
        Vector p1 = ball.getPosition();
        Vector p2 = ball.nextMovement();
        Vector l1 = new Vector(points.get(i).get(j));
        Vector l2 = new Vector(points.get(i).get(j + 1));
        Vector vp = p2.getSubtracted(p1);
        Vector vl = l2.getSubtracted(l1);
        double alphaP = (l1.crossProduct(vl) - p1.crossProduct(vl)) / (vp.crossProduct(vl));
        Vector intersection = p1.getAdded(vp.getMultyplied(alphaP));
        return intersection;
    }

    Vector predictableSpeedAfterColisionIn(Pair<Integer, Integer> colisionIndexes) throws InterruptedException {
        Vector n = getNormalVector(colisionIndexes.getKey(), colisionIndexes.getValue());
        double L = ball.getSpeed().getProjectionTo(n);
        n.multyply(-L);
        Vector v = ball.getSpeed().getAdded(n.getMultyplied(2.0));
        return v;
    }

    public void update() throws InterruptedException {
        if (ballCreated) {
            ArrayList<Pair<Integer, Integer>> intersections = predictIntersections();
            if (!intersections.isEmpty() && !ball.ExpectingColision()) {
                Vector intersection = predictableIntersectionPosition(
                        intersections.get(0).getKey(),
                        intersections.get(0).getValue()
                );
                Vector displacement = intersection.getSubtracted(ball.getPosition());
                Pair<Integer, Integer> indexes = intersections.get(0);
                for (int i = 1; i < intersections.size(); ++i) {
                    Vector currentIntersection = predictableIntersectionPosition(
                            intersections.get(i).getKey(),
                            intersections.get(i).getValue()
                    );
                    Vector currentDisplacement = intersection.getSubtracted(ball.getPosition());
                    if (currentDisplacement.len() < displacement.len()) {
                        displacement = currentDisplacement;
                        intersection = currentIntersection;
                        indexes = intersections.get(i);
                    }
                }
                Vector newSpeed = predictableSpeedAfterColisionIn(indexes);
                ball.expectColision(intersection, newSpeed);
                drawRound(intersection.getX(), intersection.getY(), 10);
                drawLine(
                        intersection.toPoint(),
                        intersection.getAdded(
                                getNormalVector(
                                        indexes.getKey(),
                                        indexes.getValue()
                                ).getMultyplied(100)).toPoint(),
                        Color.BLUE
                );
            }
            ball.move();
        }
    }

    public void clearFrame() {
        Graphics g = getGraphics();
        g.clearRect(0, 0, 1000, 600);
    }

    public void clearRect() {
        Graphics g = getGraphics();
        g.clearRect(0, 0, 1000, 600);
    }

    public void drawRound(double x0, double y0, int r) {
        Graphics g = getGraphics();
        g.drawRoundRect((int)x0, (int)y0, r, r, r, r);
    }

    public ArrayList<ArrayList<Point>> getPoints() {return points;}

    public Ball getBall() {return ball;}
}
