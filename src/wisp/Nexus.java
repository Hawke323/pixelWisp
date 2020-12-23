package wisp;

import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import wisp.data.GameWindow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;

//核心总控类，包含截图的共享信息、机器人的共享实例，并向上开放接口
public class Nexus {
    private boolean publicWispsLock = true;

    public BufferedImage getGameImage() {
        return gameImage;
    }

    private BufferedImage gameImage;
    private GameWindow gameWindow = new GameWindow();
    private ExecutorService cachedThreadPool;
    //公共使用的robot，nexus本身不调用机器人的方法
    private Robot robot;
    private Adjutant adjutant;
    private Drone drone;
    private Observer observer;

    boolean refreshImageSwitch = false;
    //______________________________________配置参数1
    private String windowNameSubString;
    private int windowXOffet = 0;
    private int windowYOffet = 0;
    private int loopInterval = 300;
    private int waitUILoopInterval = 1000;
    private double defaultComparePercentDiff = 0.05;
    private int defaultCompareAbsDiff = 10;
    private int moveMouseOffset = 4;
    private int clickDuration = 100;
    //______________________________________配置参数2
    private String sourceFilePath = "D:\\test";
    private double compareUIThreshold = 0.8;

    //从左到右的颜色
    private List<Color> signatureColors = new ArrayList<>();
    {
        signatureColors.add(new Color(255,255,255));
        signatureColors.add(new Color(0,0,0));
        signatureColors.add(new Color(237,28,36));
    }
    //______________________________________配置结束

    public Nexus(){}

    //启动核心
    public boolean startNexus(String paraWindowNameSubString){
        windowNameSubString = paraWindowNameSubString;
        if(!this.findWindow(windowNameSubString)){
            return false;
        }
        try {
            robot = new Robot();
            cachedThreadPool = Executors.newCachedThreadPool();
        } catch (AWTException e) {
            print("nexus启动robot失败" + e);
        }
        adjutant = new Adjutant();
        drone = new Drone(robot);
        observer = new Observer(robot);
        this.startLoop();
        return true;
    }

    //从所有PNG图新建wisp文件，并预读所有新建或者存在的wisp文件
    public void generateReloadWisp(){
        Adjutant.generateWisps(sourceFilePath,signatureColors);
        adjutant.preloadWisps(sourceFilePath);
    }

    //核心的循环方法，用于刷新游戏截图
    @SuppressWarnings("InfiniteLoopStatement")
    private void startLoop(){
        print("Starting Core...");
        cachedThreadPool.execute(() -> {
            while (true) {
                if (refreshImageSwitch) {
                    refreshImage();
                }
                threadWait(loopInterval);
            }
        });
    }

    public ExecutorService getCachedThreadPool(){
        return cachedThreadPool;
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

    public void loadImage(String paraPathName){
        print("直接读取图片作为游戏图片使用:" + paraPathName);
        //刷新游戏画面截图，调取ob的功能返回矩阵
        try {
            gameImage = ImageIO.read(new File(paraPathName));
        } catch (Exception e) {
           print("读取图片失败" + e);
        }
    }

    //开启或关闭自动刷新，不会重新定位窗口位置
    public void setAutoRefreshImage(boolean paraSwitch){
        this.refreshImageSwitch = paraSwitch;
    }

    public void findWindow(){
        findWindow(windowNameSubString);
    }

    //从预载的Wisp中寻找当前UI名称
    public String getCurrentUINamePreloaded(){
        return adjutant.compareWithPreloadedWisps(gameImage, compareUIThreshold);
    }

    //从预载的Wisp中寻找当前UI名称
    public String getRefreshUINamePreloaded(){
        this.refreshImage();
        return adjutant.compareWithPreloadedWisps(gameImage, compareUIThreshold);
    }

    public boolean compareRefreshUINamePreloaded(String paraUIName){
        return paraUIName.equals(getRefreshUINamePreloaded());
    }


    //等待界面，超时则返回false
    public boolean waitUtilUIPreloaded(String paraUIName, int paraMaxWait){
        int msWaited = 0;
        print("开始等待界面" + paraUIName);
        while (!compareRefreshUINamePreloaded(paraUIName)) {
            print("依然在等待界面" + paraUIName);
            threadWait(waitUILoopInterval);

            msWaited += waitUILoopInterval;
            if(msWaited >= paraMaxWait){
                print("等待界面" + paraUIName + "超时");
                return false;
            }
        }
        return true;
    }

    //等待某个界面然后点击按钮
    public boolean waitUtilUIThenClickPoint(String paraUIName, int paraMaxWait, Point paraTargetPoint, int waitDuration){
        if(!waitUtilUIPreloaded(paraUIName, paraMaxWait)){
            print("等待界面" + paraUIName + "超时");
            return false;
        }
        threadWait(waitDuration,waitDuration/6);
        clickScreen(paraTargetPoint);
        return true;
    }

    //获得当前游戏UI，涉及文件读取，不推荐使用
    public String getCurrentUIName(){
        return Adjutant.compareFolderWisps(gameImage, sourceFilePath, compareUIThreshold);
    }

    //寻找游戏窗口 应该会涉及到位置转换
    public boolean findWindow(String paraTitleSubString){
        print("开始寻找游戏窗口，标题关键词：" + paraTitleSubString );
        Rectangle gameWindowDetected = getWindowLocation(paraTitleSubString);
        if(null == gameWindowDetected){
            print("未找到游戏窗口，设为全屏截取");
            this.setGameWindowSize(1920, 1080);
            this.setGameWindowPosition(0, 0);
            return false;
        }
        setGameWindowPosition((int)gameWindowDetected.getX() + windowXOffet, (int)gameWindowDetected.getY() + windowYOffet);
        setGameWindowSize((int)gameWindowDetected.getWidth(), (int)gameWindowDetected.getHeight());
        print("游戏窗口定位完毕" + gameWindow.toString() );
        return true;
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

    public static Point xSwift(Point paraLeftestPoint, int paraXIndex, int paraXGap){
        return new Point((int)paraLeftestPoint.getX() + (paraXIndex - 1) * paraXGap, (int)paraLeftestPoint.getY());
    }

    public static Point ySwift(Point paraTopPoint, int paraYIndex, int paraYGap){
        return new Point((int)paraTopPoint.getX(), (int)paraTopPoint.getY() + (paraYIndex - 1) * paraYGap);
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
        return Observer.compareImagePixelColor(gameImage, paraX, paraY, paraColor, defaultComparePercentDiff, defaultCompareAbsDiff);
    }

    public boolean compareImageColorDefault(Color paraColor, Point paraPointChecked){
        return Observer.compareImagePixelColor(gameImage, (int)paraPointChecked.getX(), (int)paraPointChecked.getY(), paraColor, defaultComparePercentDiff, defaultCompareAbsDiff);
    }

    //Drone的诸多操作方法，涉及坐标的都要进行转换
    public void moveMouse(int paraX, int paraY){
        //print("移动点到" + paraX + "," + paraY);
        drone.moveMouse(realX(paraX), realY(paraY), moveMouseOffset);
    }
    public void moveMouse(double paraX, double paraY){
        this.moveMouse((int)paraX, (int)paraY);
    }
    public void moveMouse(Point paraPoint){
        this.moveMouse(paraPoint.getX(), paraPoint.getY());
    }

    public void clickScreen(int paraX, int paraY){
        //print("点击位置" + paraX + "," + paraY);
        drone.clickScreen(realX(paraX), realY(paraY), moveMouseOffset, clickDuration);
    }
    public void clickScreen(double paraX, double paraY){
        this.clickScreen((int)paraX, (int)paraY);
    }
    public void clickScreen(Point paraPoint){
        this.clickScreen(paraPoint.getX(), paraPoint.getY());
    }

    public void clickRightScreen(int paraX, int paraY){
        //print("点击位置" + paraX + "," + paraY);
        drone.clickRightScreen(realX(paraX), realY(paraY), moveMouseOffset, clickDuration);
    }
    public void clickRightScreen(double paraX, double paraY){
        this.clickRightScreen((int)paraX, (int)paraY);
    }
    public void clickRightScreen(Point paraPoint){
        this.clickRightScreen(paraPoint.getX(), paraPoint.getY());
    }

    private int realX(int paraX){
        return (int)(paraX + gameWindow.getX());
    }

    private int realY(int paraY){
        return (int)(paraY + gameWindow.getY());
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

    public static void threadWait(int duration){
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void threadWait(int duration, double randomGap){
        Random random = new Random();
        try {
            Thread.sleep((int)(duration + randomGap * random.nextDouble()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void print(Object paraObject){
        System.out.println(paraObject);
    }


    //______________________________________配置项Setters，不使用默认值的话，使用这些进行赋值
    public void setWindowNameSubString(String windowNameSubString) {
        this.windowNameSubString = windowNameSubString;
    }

    public void setWindowXOffet(int windowXOffet) {
        this.windowXOffet = windowXOffet;
    }

    public void setWindowYOffet(int windowYOffet) {
        this.windowYOffet = windowYOffet;
    }

    public void setLoopInterval(int loopInterval) {
        this.loopInterval = loopInterval;
    }

    public void setDefaultComparePercentDiff(double defaultComparePercentDiff) {
        this.defaultComparePercentDiff = defaultComparePercentDiff;
    }

    public void setDefaultCompareAbsDiff(int defaultCompareAbsDiff) {
        this.defaultCompareAbsDiff = defaultCompareAbsDiff;
    }

    public void setMoveMouseOffset(int moveMouseOffset) {
        this.moveMouseOffset = moveMouseOffset;
    }

    public void setClickDuration(int clickDuration) {
        this.clickDuration = clickDuration;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public void setCompareUIThreshold(double compareUIThreshold) {
        this.compareUIThreshold = compareUIThreshold;
    }

    public void setSignatureColors(List<Color> signatureColors) {
        this.signatureColors = signatureColors;
    }

    public boolean isPublicWispsLock() {
        return publicWispsLock;
    }

    public void setPublicWispsLock(boolean publicWispsLock) {
        this.publicWispsLock = publicWispsLock;
    }

    public boolean tryClaimLockifNotOccupied(){
        if(!this.publicWispsLock){
            this.publicWispsLock = true;
            return true;
        }
        return false;
    }

    public void releaseLock(){
        this.publicWispsLock = false;
    }

}
