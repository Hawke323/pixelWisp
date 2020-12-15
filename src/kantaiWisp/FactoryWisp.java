package kantaiWisp;

import wisp.Nexus;

import java.awt.*;

public class FactoryWisp {
    Nexus nexus;
    FactoryWisp(Nexus paraNexus){
        this.nexus = paraNexus;
    }

    //___________________________配置参数___________________________//

    //TODO 按钮位置配置
    Point menuFactoryPoint = new Point(300, 400);
    Point subInterfaceBackHaborPoint = new Point(115, 410);
    Point bannerQuestPoint = new Point(300, 400);

    Point developEquipmentPoint = new Point(300, 400);
    Point confirmResourceAndBuildPoint = new Point(300, 400);
    Point buildShipPoint1 = new Point(300, 400);
    int buildShipPointGapY = 30;

    Point receiveShipPoint1 = new Point(300, 400);
    Point receiveShipPoint2 = new Point(300, 400);

    Point firstQuestPoint = new Point(300, 400);
    int questGapY = 40;
    int questPerPage = 8;
    Point receiveQuestRewardPoint = new Point(300, 400);

    Point questTypeColorCheckPoint = new Point(300, 400);
    int questTypeColorCheckGapY = 40;
    Color questTypeFactoryQuestColor = new Color(123,123,123);

    private String UI_NAME_HARBOR = "harbor";
    private String UI_NAME_QUEST = "quests";
    private String UI_NAME_QUEST_REWARD = "quest_reward";
    private String UI_NAME_FACTORY = "factory";
    //___________________________配置参数___________________________//

    //工厂日常任务：一次开发，一次建造（船位1），三次开发，一次建造（船位2）
    //主要的问题在于三次交付任务，需要找到第一个工厂任务并交付
    public void factoryDailies(){
        //需要母港：进行领取工厂任务流程（切换页面直到发现第一个工厂任务，并点击，然后退回到母港）
        rendezvousQuests();
        searchFirstFactoryQuestAndClick();
        returnHarbor();
        //需要母港：进入工厂界面
        rendezvousHarborToFactory();
        //接收前一天建造完成的舰船
        receiveBuiltShipAndReturnFactory();

        //需要工厂界面 ：开发一次
        factoryManufactureEquipmentThenReturnFactory(100, 100, 100, 100);
        //进行交付工厂任务流程（切换页面直到发现第一个工厂任务，并点击，接收奖励）
        rendezvousQuests();
        searchFirstFactoryQuestAndClick();
        confirmRewards();

        //进行领取工厂任务流程
        searchFirstFactoryQuestAndClick();
        //需要母港：进入工厂界面
        rendezvousHarborToFactory();
        //需要工厂界面：建造一次（船位1）
        factoryBuildShip(1);
        //进行交付工厂任务流程
        rendezvousQuests();
        searchFirstFactoryQuestAndClick();
        confirmRewards();

        //进行领取工厂任务流程
        searchFirstFactoryQuestAndClick();
        //需要母港：进入工厂界面
        returnHarbor();
        rendezvousHarborToFactory();
        //需要工厂界面 ：开发一次（循环进行三次）
        for(int i = 0; i < 3; i ++){
            factoryManufactureEquipmentThenReturnFactory(100, 100, 100, 100);
        }
        rendezvousQuests();
        searchFirstFactoryQuestAndClick();
        confirmRewards();
        //进行交付工厂任务流程

        //进行领取工厂任务流程
        searchFirstFactoryQuestAndClick();
        returnHarbor();
        //需要母港：进入工厂界面
        rendezvousHarborToFactory();
        //需要工厂界面：建造一次（船位2）
        factoryBuildShip(2);
    }

    //不需要前置界面（母港和工厂均可能），进入任务界面
    private void rendezvousQuests(){
        nexus.clickScreen(this.bannerQuestPoint);
        Nexus.threadWait(2 * 1000, 500);
    }

    //任务界面下，切页直到找到工厂任务，然后点击第一个工厂任务
    private void searchFirstFactoryQuestAndClick(){
        nexus.waitUtilUIPreloaded(this.UI_NAME_QUEST, 20 * 1000);
        int factoryQuestIndex = getFactoryQuestIndex();
        if(factoryQuestIndex != 0){
            nexus.clickScreen(Nexus.ySwift(firstQuestPoint, questGapY, factoryQuestIndex));
        }
    }

    private int getFactoryQuestIndex(){
        int questIndex = 0;
        for(int searchIndex = 1; searchIndex < questPerPage; searchIndex ++){
            if(ifQuestIsFactory(searchIndex)){
                questIndex = searchIndex;
                break;
            }
        }
        return questIndex;
    }

    private boolean ifQuestIsFactory(int paraIndex){
        Point currentColorCheckPoint = Nexus.ySwift(questTypeColorCheckPoint, questTypeColorCheckGapY, paraIndex);
        return nexus.compareImageColorDefault(questTypeFactoryQuestColor,currentColorCheckPoint);
    }

    //需要工厂界面：轮流接收之前建造的舰船
    private void receiveBuiltShipAndReturnFactory(){
        nexus.waitUtilUIPreloaded(this.UI_NAME_FACTORY, 20 * 1000);
        nexus.clickScreen(receiveShipPoint1);
        Nexus.threadWait(10 * 1000);
        nexus.clickScreen(receiveShipPoint2);
        Nexus.threadWait(3 * 1000);
        nexus.clickScreen(receiveShipPoint2);
        Nexus.threadWait(10 * 1000);
        nexus.clickScreen(receiveShipPoint2);
    }

    //需要工厂任务奖励界面：接收工厂任务的奖励
    private void confirmRewards(){
        nexus.waitUtilUIThenClickPoint(UI_NAME_QUEST_REWARD, 10*1000, receiveQuestRewardPoint, 100);
        Nexus.threadWait(3 * 1000, 500);
    }

    //点击左上返回母港
    private void returnHarbor(){
        nexus.clickScreen(subInterfaceBackHaborPoint);
        Nexus.threadWait(3 * 1000,500);
    }

    //从母港进入工厂界面
    private void rendezvousHarborToFactory(){
        nexus.waitUtilUIThenClickPoint(UI_NAME_HARBOR, 5*1000, menuFactoryPoint, 500);
        Nexus.threadWait(3 * 1000,500);
    }

    //工厂开发一个装备，然后点击直到回到工厂界面。第一期只支持全30开发
    private void factoryManufactureEquipmentThenReturnFactory(int paraOil, int paraAmmo, int paraSteel, int paraAluminum){
        nexus.clickScreen(developEquipmentPoint);
        Nexus.threadWait(2 * 1000, 200);
        nexus.clickScreen(confirmResourceAndBuildPoint);
        Nexus.threadWait(5 * 1000, 1000);
        nexus.clickScreen(confirmResourceAndBuildPoint);
        Nexus.threadWait( 2000, 500);
    }

    //工厂选择槽位建造一次(默认全30)
    private void factoryBuildShip(int paraSlotIndex){
        nexus.clickScreen(Nexus.ySwift(buildShipPoint1, paraSlotIndex, buildShipPointGapY));
        Nexus.threadWait(1000, 200);
        nexus.clickScreen(confirmResourceAndBuildPoint);
        Nexus.threadWait(1000, 200);
    }

}
