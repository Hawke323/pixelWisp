package kantaiWisp;

import wisp.Nexus;

import java.awt.*;

import static wisp.Nexus.print;

public class FactoryWisp {
    Nexus nexus;
    FactoryWisp(Nexus paraNexus){
        this.nexus = paraNexus;
    }

    //___________________________配置参数___________________________//

    Point menuFactoryPoint = new Point(400, 580);
    Point subInterfaceBackHaborPoint = new Point(70, 70);
    Point bannerQuestPoint = new Point(840, 100);

    Point developEquipmentPoint = new Point(360, 555);
    Point confirmResourceAndBuildPoint = new Point(1060, 690);
    Point buildShipPoint1 = new Point(937, 290);
    int buildShipPointGapY = 120;

    Point receiveShipPoint1 = new Point(937, 290);
    Point receiveShipPoint2 = new Point(937, 410);

    Point firstQuestPoint = new Point(260, 237);
    int questGapY = 105;
    int questPerPage = 5;
    Point receiveQuestRewardPoint = new Point(600, 630);

    Point questTypeColorCheckPoint = new Point(238, 209);
    int questTypeColorCheckGapY = 104;
    Color questTypeFactoryQuestColor = new Color(148,96,72);

    Point nextQuestPagePoint = new Point(779, 727);

    private String UI_NAME_HARBOR = "UI_NAME_HARBOR";
    private String UI_NAME_QUEST = "UI_NAME_QUEST";
    private String UI_NAME_QUEST_REWARD = "UI_NAME_QUEST_REWARD";
    private String UI_NAME_FACTORY = "UI_NAME_FACTORY";
    //___________________________配置参数___________________________//


    public void factoryTest(){
    }

    //工厂日常任务：一次开发，一次建造（船位1），三次开发，一次建造（船位2）
    //主要的问题在于三次交付任务，需要找到第一个工厂任务并交付
    public void factoryDailies(){
        if(!Nexus.tryClaimLockIfNotOccupied()){
            print("工厂任务：锁被占用，直接返回");
            return;
        }
        //需要母港：进行领取工厂任务流程（切换页面直到发现第一个工厂任务，并点击，然后退回到母港）
        rendezvousQuests();
        //移除npc
        rendezvousQuests();
        searchFirstFactoryQuestAndClick();
        returnHarbor();
        //需要母港：进入工厂界面
        rendezvousHarborToFactory();
        //接收建造完成的舰船
        receiveBuiltShipAndReturnFactory();

        //需要工厂界面 ：开发一次
        factoryManufactureEquipmentThenReturnFactory(100, 100, 100, 100);
        //进行交付工厂任务流程（切换页面直到发现第一个工厂任务，并点击，接收奖励）
        rendezvousQuests();
        //移除npc
        rendezvousQuests();
        searchFirstFactoryQuestAndClick();
        confirmRewards();

        //进行领取工厂任务流程
        searchFirstFactoryQuestAndClick();
        returnHarbor();
        //需要母港：进入工厂界面
        rendezvousHarborToFactory();
        //需要工厂界面：建造一次（船位1）
        factoryBuildShip(1);
        //进行交付工厂任务流程
        rendezvousQuests();
        //移除npc
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
        //移除npc
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
        returnHarbor();
        Nexus.releaseLock();
    }

    //不需要前置界面（母港和工厂均可能），进入任务界面
    private void rendezvousQuests(){
        nexus.clickScreen(this.bannerQuestPoint);
        Nexus.threadWait(2 * 1000, 500);
    }

    //任务界面下，切页直到找到工厂任务，然后点击第一个工厂任务
    private void searchFirstFactoryQuestAndClick(){
        nexus.waitUtilUIPreloaded(this.UI_NAME_QUEST, 20 * 1000);
        for(int trail = 0; trail < 6; trail ++){
            nexus.refreshImage();
            int factoryQuestIndex = getFactoryQuestIndex();
            if(factoryQuestIndex != 0){
                print("发现点，位置为" + Nexus.ySwift(firstQuestPoint, questGapY, factoryQuestIndex));
                nexus.clickScreen(Nexus.ySwift(firstQuestPoint, questGapY, factoryQuestIndex));
                Nexus.threadWait(1000, 110);
                break;
            }
            nexus.clickScreen(nextQuestPagePoint);
            Nexus.threadWait(500, 50);
        }
    }

    private int getFactoryQuestIndex(){
        int questIndex = 0;
        for(int searchIndex = 0; searchIndex < questPerPage; searchIndex ++){
            if(ifQuestIsFactory(searchIndex)){
                questIndex = searchIndex;
                break;
            }
        }
        return questIndex;
    }

    private boolean ifQuestIsFactory(int paraIndex){
        Point currentColorCheckPoint = Nexus.ySwift(questTypeColorCheckPoint, questTypeColorCheckGapY, paraIndex);
        print("检测点" + currentColorCheckPoint + " 颜色为" + nexus.getImageColor((int)currentColorCheckPoint.getX(), (int)currentColorCheckPoint.getY()));
        return nexus.compareImageColorDefault(questTypeFactoryQuestColor,currentColorCheckPoint);
    }

    //需要工厂界面：轮流接收之前建造的舰船
    private void receiveBuiltShipAndReturnFactory(){
        nexus.waitUtilUIPreloaded(this.UI_NAME_FACTORY, 20 * 1000);
        if(nexus.compareImageColorDefault(new Color(51,51,51),943,325)){
            nexus.clickScreen(receiveShipPoint1);
        }
        Nexus.threadWait(10 * 1000);
        nexus.clickScreen(receiveShipPoint1);
        Nexus.threadWait(1 * 1000);
        if(nexus.compareImageColorDefault(new Color(51,51,51),943,442)){
            nexus.clickScreen(receiveShipPoint2);
        }
        Nexus.threadWait(10 * 1000);
        nexus.clickScreen(receiveShipPoint1);
        Nexus.threadWait(2 * 1000);
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
        Nexus.threadWait(10 * 1000, 1000);
        nexus.clickScreen(confirmResourceAndBuildPoint);
        Nexus.threadWait( 1000, 500);
    }

    //工厂选择槽位建造一次(默认全30)
    private void factoryBuildShip(int paraSlotIndex){
        nexus.clickScreen(Nexus.ySwift(buildShipPoint1, paraSlotIndex, buildShipPointGapY));
        Nexus.threadWait(1000, 200);
        nexus.clickScreen(confirmResourceAndBuildPoint);
        Nexus.threadWait(1000, 200);
    }

}
