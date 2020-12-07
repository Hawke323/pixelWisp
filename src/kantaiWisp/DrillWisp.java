package kantaiWisp;

import wisp.Nexus;

import java.awt.*;

import static wisp.Nexus.print;
import static wisp.Nexus.threadWait;

public class DrillWisp {

    private Nexus nexus;
    DrillWisp(Nexus paraNexus){
        this.nexus = paraNexus;
    }

    //___________________________配置参数___________________________//
    private int formationSelected = 2;

    //游戏主菜单界面远征按钮位置和演习界面母港按钮位置
    Point menuBattlePoint = new Point(300, 400);
    Point battleDrillPoint = new Point(1000, 370);
    Point subInterfaceBackHaborPoint = new Point(115, 410);

    //TODO 按钮位置配置
    Point menuRefuelPoint = new Point(300, 400);
    Point refuelFleetPoint = new Point(300, 400);
    Point refuelResupplyPoint = new Point(300, 400);

    Point drillOpponentOnePoint = new Point(1000, 370);
    int drillOpponentGapY = 100;
    Point drillStartPoint = new Point(1000, 370);

    Point formationOnePoint = new Point(1000, 370);
    int formationGapX = 100;

    Point drillNightBattlePoint = new Point(1000, 370);

    Point drillCompleteRandomClickPoint = new Point(115, 410);

    private String UI_NAME_BATTLE = "battle";
    private String UI_NAME_HARBOR = "harbor";
    private String UI_NAME_DRILL = "drill";
    private String UI_NAME_FORMATION = "formation";
    private String UI_NAME_DRILL_RESULT = "drill_result";
    private String UI_NAME_DRILL_NIGHT_BATTLE = "drill_night_battle";

    //___________________________配置参数——结束___________________________//

    enum DrillResult{
        COMPLETE, NIGHT_BATTLE, OVER_TIME
    }

    //逐个进行演习操作
    public void startDrills(){

        //将lock的flag设为true，避免其他wisp操作
        if(!nexus.tryClaimLockifNotOccupied()){
            print("其他操作进行中，尝试发动演习失败");
            return;
        }
        print("发动演习");

        int drillFleetIndex;
        //按照顺序进行演习，如果轮到3或者5舰队，则先进行补给
        for(drillFleetIndex = 1; drillFleetIndex <= 5; drillFleetIndex ++){
            if(drillFleetIndex == 3 || drillFleetIndex == 5){
                refuelAndReturn();
            }
            //等待母港界面
            if(!nexus.waitUtilUIPreloaded(UI_NAME_HARBOR, 10000)){
                print("等待母港界面超时，放弃演习操作");
                return;
            }
            //从母港进入演习界面
            rendezvousDrillUI();

            //进行演习，需要演习界面
            if(!nexus.waitUtilUIPreloaded(UI_NAME_DRILL, 10000)){
                print("等待演习界面超时，放弃演习操作");
                return;
            }
            commenceDrill(drillFleetIndex, formationSelected);
            //等待演习结果，演习或者结算界面
            DrillResult result = waitDrillResult();
            if(result.equals(DrillResult.NIGHT_BATTLE)){
                //如果结果是夜战判定，则进入夜战
                startNightBattle();
            }else if(result.equals(DrillResult.OVER_TIME)){
                //超时情况
                print("等待演习结果超时，放弃演习操作");
                return;
            }
            if(!nexus.waitUtilUIPreloaded(UI_NAME_DRILL_RESULT, 10000)){
                print("等待演习结算超时，放弃演习操作");
                return;
            }
            //结束演习，回到母港
            reviewAndReturn();
        }
        //结束 释放锁
        nexus.releaseLock();
    }

    //需要母港：从母港进入演习界面
    private void rendezvousDrillUI(){
        nexus.clickScreen(menuBattlePoint);
        if(!nexus.waitUtilUIPreloaded(UI_NAME_BATTLE, 10000)){
            print("等待出击界面超时，放弃演习操作");
            return;
        }
        nexus.clickScreen(battleDrillPoint);
    }

    //需要演习界面：选择对应的对手和阵型，然后开始演习
    private void commenceDrill(int paraOpponentIndex, int paraFormation){
        //选择对手，然后点击开始
        selectCertainOpponent(paraOpponentIndex);
        threadWait(3000);
        nexus.clickScreen(drillStartPoint);
        //等待阵型选择界面
        if(!nexus.waitUtilUIPreloaded(UI_NAME_FORMATION, 10000)){
            print("等待阵型界面超时，放弃演习操作");
            return;
        }
        //选择阵型，开始演习
        selectCertainFormation(paraFormation);
    }

    //选择对应的对手
    private void selectCertainOpponent(int paraOpponentIndex){
        nexus.clickScreen(drillOpponentOnePoint.getX(), drillOpponentOnePoint.getY() + (paraOpponentIndex-1) * drillOpponentGapY);
    }

    //选择对应的阵型，只支持1-3阵型
    private void selectCertainFormation(int paraOpponentIndex){
        nexus.clickScreen(formationOnePoint.getX() + (paraOpponentIndex - 1) * formationGapX, formationOnePoint.getY());
    }

    //等待演习结果
    private DrillResult waitDrillResult(){
        int msWaited = 0;
        print("开始等待界面演习结束");
        String result;
        while (true) {
            print("等待演习结束");
            result = nexus.getRefreshUINamePreloaded();
            if(result.equals(UI_NAME_DRILL_NIGHT_BATTLE)){
                print("演习结束：转夜战");
                return DrillResult.NIGHT_BATTLE;
            }
            if(result.equals(UI_NAME_DRILL_RESULT)){
                print("演习结束：结算");
                return DrillResult.COMPLETE;
            }

            threadWait(1000);
            msWaited += 1000;
            if(msWaited >= 1000 * 200){
                print("等待演习结束超时");
                break;
            }
        }
        return DrillResult.OVER_TIME;
    }

    //需要夜战界面：开始夜战
    private void startNightBattle(){
        nexus.clickScreen(drillNightBattlePoint);
    }

    //需要结算界面：持续点击直到回到母港
    private void reviewAndReturn(){
        int msWaited = 0;
        while (true) {
            print("等待回到母港，随机点击");
            nexus.clickScreen(drillCompleteRandomClickPoint);
            if(nexus.compareRefreshUINamePreloaded(UI_NAME_HARBOR)){
                print("演习结束并回到母港");
                break;
            }
            threadWait(1200, 1000);
            msWaited += 1700;
            if(msWaited >= 1000 * 20){
                print("等待演习结束回到母港超时");
                break;
            }
        }
    }

    //需要母港：对一舰队补给，并回到母港
    private void refuelAndReturn(){
        nexus.clickScreen(menuRefuelPoint);
        threadWait(3000,1000);
        nexus.clickScreen(refuelFleetPoint);
        threadWait(1000,1000);
        nexus.clickScreen(refuelResupplyPoint);
        threadWait(1000,1000);
        nexus.clickScreen(subInterfaceBackHaborPoint);
    }

}
