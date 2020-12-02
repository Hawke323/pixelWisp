package wisp;

import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import wisp.data.GameWindow;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//核心总控类，包含截图的共享信息、机器人的共享实例，并向上开放接口
public class Nexus {
    private BufferedImage gameImage;
    private GameWindow gameWindow;
    private ExecutorService cachedThreadPool;
    //公共使用的robot，nexus本身不调用机器人的方法
    private Robot robot;
    private Adjutant adjutant;
    private Drone drone;
    private Observer observer;

    boolean refreshImageSwitch = false;
    //______________________________________配置参数
    private String windowNameSubString = "IDEA";
    private int windowXOffet = 0;
    private int windowYOffet = 0;
    private int interval = 300;
    private double defaultPercentDiff = 0.05;
    private int defaultAbsDiff = 10;
    private int moveMouseOffset = 4;
    private int clickDuration = 100;
    //______________________________________配置参数

    public Nexus(){
        try {
            robot = new Robot();
            cachedThreadPool = Executors.newCachedThreadPool();
            gameWindow = new GameWindow();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        adjutant = new Adjutant(robot);
        drone = new Drone(robot);
        observer = new Observer(robot);
        this.findWindow(windowNameSubString);
        this.startLoop();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void startLoop(){
        print("Starting Core...");
        cachedThreadPool.execute(() -> {
            while (true) {
                if (refreshImageSwitch) {
                    refreshImage();
                }
                threadWait(interval);
            }
        });
    }

    public void setGameWindowPosition(int paraX, int paraY){
        gameWindow.setLocation(paraX, paraY);
    }

    public void setGameWindowSize(int paraW, int paraH){
        gameWindow.setSize(paraW, paraH);
    }

    public void refreshImage(){
        //刷新游戏画面截图，调取ob的功能返回矩阵
        gameImage = observer.captureImage(gameWindow);
    }

    //开启或关闭自动刷新，不会重新定位窗口位置
    public void setAutoRefreshImage(boolean paraSwitch){
        this.refreshImageSwitch = paraSwitch;
    }

    public void findWindow(){
        findWindow(windowNameSubString);
    }

    //寻找游戏窗口 应该会涉及到位置转换
    public void findWindow(String paraTitleSubString){
        print("开始寻找游戏窗口，标题关键词：" + paraTitleSubString );
        Rectangle gameWindowDetected = getWindowLocation(paraTitleSubString);
        if(null == gameWindowDetected){
            print("未找到游戏窗口，设为全屏截取");
            this.setGameWindowSize(1920, 1080);
            this.setGameWindowPosition(0, 0);
            return;
        }
        setGameWindowPosition((int)gameWindowDetected.getX() + windowXOffet, (int)gameWindowDetected.getY() + windowYOffet);
        setGameWindowSize((int)gameWindowDetected.getWidth(), (int)gameWindowDetected.getHeight());
        print("游戏窗口定位完毕" + gameWindow.toString() );
    }

    //根据窗口的颜色寻找左上方点的坐标，会重置gameWindow设置
    private Rectangle getWindowLocation(String paraTitleSubString){
        Rectangle gameWindowRect = null;
        int count = 0;
        for (DesktopWindow desktopWindow : WindowUtils.getAllWindows(true)) {
            if (desktopWindow.getTitle().contains(paraTitleSubString)) {
                gameWindowRect = desktopWindow.getLocAndSize();
                count ++ ;
                print("包含" + paraTitleSubString + "的窗口的位置是" + gameWindowRect.toString());
            }
        }
        if(count > 1){
            print("【警告！】 发现多个包含" + paraTitleSubString + "的窗口");
        }
        return gameWindowRect;
    }

    //会抓取当前截图
    public void getCurrentInterfaceName(){

    }

    //不会抓取新截图
    public Color getImageColor(int paraX, int paraY){
        return Observer.getPixelColor(gameImage, paraX, paraY);
    }

    //不会抓取新截图
    public boolean compareImageColor(Color paraColor, int paraX, int paraY, double paraPercentDiff, int paraAbsDiff){
        return Observer.compareImagePixelColor(gameImage, paraX, paraY, paraColor, paraPercentDiff, paraAbsDiff);
    }

    //不会抓取新截图
    public boolean compareImageColorDefault(Color paraColor, int paraX, int paraY){
        return Observer.compareImagePixelColor(gameImage, paraX, paraY, paraColor, defaultPercentDiff, defaultAbsDiff);
    }

    //Drone的诸多操作方法
    public void MoveMouse(int paraX, int paraY){
        drone.MoveMouse(paraX, paraY, moveMouseOffset);
    }

    public void ClickScreen(int paraX, int paraY){
        drone.ClickScreen(paraX, paraY, moveMouseOffset, clickDuration);
    }

    public void pressButton(int paraKeyCode){
        drone.pressButton(paraKeyCode, clickDuration);
    }

    void captureSaveGameImage(){
        refreshImage();
        saveGameImage();
    }

    void saveGameImage(){
        Observer.saveBufferedImageDefaultPathName(gameImage);
    }

    static void threadWait(int duration){
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void print(Object paraObject){
        System.out.println(paraObject);
    }
}
