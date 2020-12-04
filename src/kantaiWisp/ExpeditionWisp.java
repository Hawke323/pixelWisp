package kantaiWisp;

import wisp.Nexus;

import java.awt.*;

import static wisp.Nexus.print;
import static wisp.Nexus.threadWait;

public class ExpeditionWisp {
    private Nexus nexus;

    ExpeditionWisp(Nexus paraNexus){
        this.nexus = paraNexus;
    }

    int[] fleetExpePage = new int[4];
    int[] fleetExpeIndex = new int[4];

    //___________________________配置参数，界面相关___________________________//
    //界面位置相关（固定）
        //Poi远征状态位置
    Point expeOneStatusPoint = new Point(660, 875); //1-875, 3-925. gap = 25
    int expeStatusGap = 25;

        //Poi远征状态颜色
    Color expeUnderwayColor = new Color(100,100,100); //TODO
    Color expeCompleteColor = new Color(34,76,70);
    Color expeIdleColor = new Color(39,52,62);

        //游戏远征界面远征分页和科目位置点
    Point expeTabOnePoint = new Point(212, 704); //1-212, 7-573, gap = 60
    int expeTabGap = 60;
    Point expeCouseOnePoint = new Point(400, 290); //1 - 290, 8 - 600, gap = 44
    int expeCouseGap = 44;

         //母港收远征点击位置，最好选用没有按钮的位置
    Point harborClickPoint = new Point(625, 430);

        //游戏远征界面远征舰队按钮
    Point expeFleetOnePoint = new Point(590, 200); // 1-590, 3-685, gap = 47
    int expeFleetGap = 47;

        //游戏远征界面补给按钮和开始远征按钮
    Point expeSupplyPoint = new Point(750, 690);
    Point expeLaunchPoint = new Point(915, 690);

    //游戏主菜单界面远征按钮位置和任意界面母港按钮位置
    Point menuBattlePoint = new Point(300, 400);
    Point battleExpePoint = new Point(1000, 370);
    Point subInterfaceMainMenuPoint = new Point(115, 410);
    //___________________________配置参数，远征分配___________________________//
    {
        //远征1
        fleetExpePage[1] = 1;
        fleetExpeIndex[1] = 2;
        //远征2
        fleetExpePage[2] = 2;
        fleetExpeIndex[2] = 3;
        //远征3
        fleetExpePage[3] = 4;
        fleetExpeIndex[3] = 3;
    }

    enum ExpeditionStatus{
        UNDERWAY, COMPLETED, IDLE, OTHERS
    }

    public void expeCheckTest(){
        print("测试远征模块的方法");
        //测试远征舰队状态
        /*nexus.loadImage("D:\\test\\kantai7.png");
        print("读取图片完成");
        for(int fleetIndex = 1; fleetIndex <= 3; fleetIndex ++){
            print("舰队"+fleetIndex+"的是否已经完成：" + checkFleetCompleted(fleetIndex));
        }*/

        //测试按钮点击
        /*this.rendezvousBattle();
        threadWait(2000,2000);
        //进入母港界面
        this.rendezvousBackHarbor();*/

        //发布远征测试
        for(int fleetIndex = 1; fleetIndex <= 3; fleetIndex ++){
            selectFleetExpe(fleetIndex);
            threadWait(1600,400);
            selectFleet(fleetIndex);
            threadWait(1000,400);
            resupplyAndLaunch();
            threadWait(1000,400);
        }

    }

    //检测完成的远征，并根据配置继续启动远征，为阻塞方法以避免冲突
    public void expeditionsCheck(){
        /*如果存在完成状态的远征，则进入远征界面，并回到母港，然后点击到不再存在为止
        （如果存在闲置状态的舰队——上一步后一定存在），进入远征界面
        根据每个闲置状态的舰队，选择相应的远征-选择舰队-补给-发送远征，结束后回到母港*/

        //如果存在完成状态的远征
        nexus.refreshImage();
        if(!(checkFleetCompleted(1) || checkFleetCompleted(2) || checkFleetCompleted(3))){
            return;
        }
        //进入出击界面
        this.rendezvousBattle();
        threadWait(2000,2000);
        //进入母港界面
        this.rendezvousBackHarbor();
        //在还有完成状态的情况下，点击空白
        nexus.refreshImage();
        while((checkFleetCompleted(1) || checkFleetCompleted(2) || checkFleetCompleted(3))){
            this.clickBlankHarbor();
            threadWait(300,1000);
            nexus.refreshImage();
        }
        //之后再点击几次空白，确保完成远征收取
        for(int i = 0; i < 10; i ++){
            this.clickBlankHarbor();
            threadWait(1600,400);
        }
        //点击进入出击界面
        this.rendezvousBattle();
        threadWait(2000,2000);
        //点击进入远征界面
        this.rendezvousExpe();
        threadWait(2000,2000);
        //针对每个闲置状态的舰队，发送远征
        for(int fleetIndex = 1; fleetIndex <= 3; fleetIndex ++){
            nexus.refreshImage();
            if(checkFleetIdle(fleetIndex)){
                selectFleetExpe(fleetIndex);
                threadWait(1600,400);
                selectFleet(fleetIndex);
                threadWait(1000,400);
                resupplyAndLaunch();
                threadWait(1000,400);
            }
        }
    }

    private boolean checkFleetIdle(int fleetIndex){
        return (checkFleetStatus(fleetIndex, ExpeditionStatus.IDLE));
    }

    private boolean checkFleetCompleted(int fleetIndex){
        return (checkFleetStatus(fleetIndex, ExpeditionStatus.COMPLETED));
    }

    private boolean checkFleetStatus(int fleetIndex, ExpeditionStatus status){
        return (getExpeditionStatus(fleetIndex) == status);
    }

    private ExpeditionStatus getExpeditionStatus(int paraFleetIndex){
        if(compareExpeditionColor(expeUnderwayColor, paraFleetIndex)){
            return ExpeditionStatus.UNDERWAY;
        }
        if(compareExpeditionColor(expeCompleteColor, paraFleetIndex)){
            return ExpeditionStatus.COMPLETED;
        }
        if(compareExpeditionColor(expeIdleColor, paraFleetIndex)){
            return ExpeditionStatus.IDLE;
        }
        return ExpeditionStatus.OTHERS;
    }

    private boolean compareExpeditionColor(Color expeditionColor, int paraFleetIndex){
        return nexus.compareImageColorDefault(expeditionColor, (int)expeOneStatusPoint.getX(), (int)expeOneStatusPoint.getY() + expeStatusGap * (paraFleetIndex - 1));
    }

    //进入母港界面
    private void rendezvousBackHarbor(){
        nexus.clickScreen(subInterfaceMainMenuPoint);
    }

    //进入远征界面
    private void rendezvousBattle(){
        nexus.clickScreen(menuBattlePoint);
    }

    //进入远征界面
    private void rendezvousExpe(){
        nexus.clickScreen(battleExpePoint);
    }

    //母港点击
    private void clickBlankHarbor(){
        nexus.clickScreen(harborClickPoint);
    }

    //选择指定远征，需要已经处于远征界面下
    private void selectExpe(int paraPage, int paraIndex){
        if(paraPage > 1){
            nexus.clickScreen(expeTabOnePoint.getX() + expeTabGap * (paraPage - 1), expeTabOnePoint.getY());
        }
        Nexus.threadWait(500,1000);
        nexus.clickScreen(expeCouseOnePoint.getX(), expeCouseOnePoint.getY() + expeCouseGap * (paraIndex - 1));
    }

    //选择指定远征，需要已经处于远征界面下，参数是舰队编号
    private void selectFleetExpe(int fleetIndex){
        selectExpe(fleetExpePage[fleetIndex], fleetExpeIndex[fleetIndex]);
    }

    //选择指定舰队的远征，需要已经处于远征界面下
    private void selectFleet(int fleetIndex){
        nexus.clickScreen(expeFleetOnePoint.getX() + expeFleetGap * (fleetIndex - 1), expeFleetOnePoint.getY());
    }

    //补给并发动远征
    private void resupplyAndLaunch(){
        nexus.clickScreen(expeSupplyPoint);
        Nexus.threadWait(5000,2000);
        nexus.clickScreen(expeLaunchPoint);
    }
}
