import java.awt.*;

/**
 * Created by Vadim on 15.12.16.
 */
public class WindowWithPhysics extends Thread {
    private MyJFrame myJFrame;

    WindowWithPhysics() {
        myJFrame = new MyJFrame("window", 100, 100, 900, 500);
    }

    @Override
    public void run() {
        while (true) {
            Point st = myJFrame.getBall().getPosition().toPoint();
            myJFrame.update();
            //myJFrame.drawLine(myJFrame.getBall().getPosition().toPoint(), st, Color.LIGHT_GRAY);
            myJFrame.clearFrame();
            for (int i = 0; i < myJFrame.getPoints().size() - 1; ++i) {
                myJFrame.drawLine(myJFrame.getPoints().get(i), myJFrame.getPoints().get(i + 1));
            }
            myJFrame.drawRound(myJFrame.getBall().getPosition().getX(), myJFrame.getBall().getPosition().getY(), 3);
            try {
                sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
