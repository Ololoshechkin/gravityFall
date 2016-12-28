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
            for (int i = 0; i < 100; ++i) {
                try {
                    myJFrame.update();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            myJFrame.clearFrame();
            for (int i = 0; i < myJFrame.getPoints().size(); ++i) {
                for (int j = 0; j < myJFrame.getPoints().get(i).size() - 1; ++j) {
                    myJFrame.drawLine(myJFrame.getPoints().get(i).get(j), myJFrame.getPoints().get(i).get(j + 1));
                }
            }
            myJFrame.drawRound(myJFrame.getBall().getPosition().getX(), myJFrame.getBall().getPosition().getY(), 4);
            myJFrame.drawLine(
                    myJFrame.getBall().getPosition().toPoint(),
                    myJFrame.getBall().getPosition().getAdded(myJFrame.getBall().getSpeed()).toPoint(),
                    Color.cyan
            );
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
