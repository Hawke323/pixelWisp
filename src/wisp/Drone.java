package wisp;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static wisp.Nexus.threadWait;

//整合各种操作方法
public class Drone {

    private Robot robot;
    private ExecutorService cachedThreadPool;

    Drone(Robot paraRobot){
        this.robot = paraRobot;
        cachedThreadPool = Executors.newCachedThreadPool();
    }

    //按下并弹起左键，添加一个随机的小偏移和持续时间
    public void clickScreen(int paraX, int paraY, int offset, int gap){
        cachedThreadPool.execute(() -> {
            this.moveMouse(paraX, paraY, offset);
            this.clickMouse(InputEvent.BUTTON1_MASK, gap);
        });
    }

    public void clickRightScreen(int paraX, int paraY, int offset, int gap){
        cachedThreadPool.execute(() -> {
            this.moveMouse(paraX, paraY, offset);
            this.clickMouse(InputEvent.BUTTON2_MASK, gap);
        });
    }

    private void clickMouse(int mouseButton, int gap){
        Random random = new Random();
        int actualDuration = 100 + (int)((double)gap * random.nextDouble());
        robot.mousePress(mouseButton);
        threadWait(actualDuration);
        robot.mouseRelease(mouseButton);
    }

    public void moveMouse(int paraX, int paraY, int offset){
        Random random = new Random();
        int actualX = paraX + (int)((double)offset * random.nextDouble());
        int actualY = paraY + (int)((double)offset * random.nextDouble());
        robot.mouseMove(actualX, actualY);
    }

    public void pressButton(int paraKeyCode, int gap){
        Random random = new Random();
        int actualDuration = 100 + (int)((double)gap * random.nextDouble());
        cachedThreadPool.execute(() -> {
            robot.keyPress(paraKeyCode);
            threadWait(actualDuration);
            robot.keyRelease(paraKeyCode);
        });
    }


}
