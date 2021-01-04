package wowwisp;

import wisp.Nexus;

import java.awt.event.KeyEvent;

import static wisp.Nexus.print;

public class HarvestWisp {
    private Nexus nexus;

    HarvestWisp(){
        nexus = new Nexus();
    }

    int CHARS_AMOUNT = 5;
    int MAX_TRAIL_COUNT = 20;
    int SKIN_FOCUS_TARGET_TAUNT = KeyEvent.VK_F4;
    int LOG_OUT_BTN = KeyEvent.VK_F8;
    int SKILL_1_BTN = KeyEvent.VK_F5;
    int SKILL_2_BTN = KeyEvent.VK_F6;
    int SKILL_3_BTN = KeyEvent.VK_F7;
    int SKILL_4_BTN = KeyEvent.VK_F9;

    int currentChar = 0;

    public void startLoop(){
        print("Starting HarvestWisp Core...");
        nexus.getCachedThreadPool().execute(this::wispLoop);
    }

    private void wispLoop(){
        while(true){
            //进入人物选择界面并取得全局锁
            waitForCharSelectInterface();
            waitForLockReleasedAndClaim();
            print("取得锁，开始收集");
            //选取下一个或者第一个任务
            selectNextCharInLine();

            //最多攻击MAX_TRAIL_COUNT个目标
            for(int trail = 0; trail < MAX_TRAIL_COUNT; trail ++){
                //尝试选取目标并攻击，如果超时则直接登出
                print("尝试选取目标并进入战斗");
                if(!tryTargetEnemyPullAndSkinFocus()){
                    break;
                }
                //找到目标，则不断使用攻击和剥皮按钮，最多尝试攻击MAX_TRAIL_COUNT次
                print("尝试击杀目标战斗");
                for(int trailAttack = 0; trailAttack < MAX_TRAIL_COUNT; trailAttack++){
                    //目标死亡，跳出当前循环
                    if(!isTargetAlive()){
                        break;
                    }
                    //目标存活时，不断按下技能按钮
                    pressSkillButtons();
                }
            }
            print("选取不到目标，登出游戏并释放锁");
            //选取不到目标或者超时时，登出并释放锁
            logout();
            Nexus.releaseLock();
            Nexus.threadWait(20 * 1000);
        }
    }

    private void selectNextCharInLine(){
        //达到最大角色数，选择第一个角色
        if(currentChar >= CHARS_AMOUNT){
            print("选取第一个角色");
            for(int i = 0; i < CHARS_AMOUNT; i ++){
                selectPreviousChar();
            }
            login();
            return;
        }
        //选择下一个角色
        print("选取下一个角色");
        currentChar ++;
        selectNextChar();
        login();
    }

    private void selectNextChar(){
        nexus.pressButton(KeyEvent.VK_DOWN);
    }

    private void selectPreviousChar(){
        nexus.pressButton(KeyEvent.VK_UP);
    }

    private void login(){
        nexus.pressButton(KeyEvent.VK_ENTER);
    }

    //等待直到进入角色选择界面，注意分辨率
    private void waitForCharSelectInterface(){
        while(true){
            if(ifCharSelect()){
                break;
            }
            Nexus.threadWait(100);
        }
    }

    //检查是否为角色选取界面
    private boolean ifCharSelect(){
        return("pickChar".equals(nexus.getRefreshUINamePreloaded()));
    }

    //100毫秒检查一次获取锁
    private void waitForLockReleasedAndClaim(){
        while(true){
            if(Nexus.tryClaimLockifNotOccupied()){
                break;
            }
            Nexus.threadWait(100);
        }
    }

    //一定时间内拉怪，拉到则立刻返回true，超时返回false
    private boolean tryTargetEnemyPullAndSkinFocus(){
        for(int i = 0; i < 12; i ++){
            nexus.pressButton(SKIN_FOCUS_TARGET_TAUNT);
            Nexus.threadWait(500);
            if(inCombat()){
                return true;
            }
        }
        return false;
    }

    //检测是否在战斗状态
    private boolean inCombat(){
        //TODO
        return true;
    }

    //按登出按钮
    private void logout(){
        nexus.pressButton(LOG_OUT_BTN);
    }

    //按攻击技能键
    private void pressSkillButtons(){
        nexus.pressButton(SKILL_1_BTN);
        nexus.pressButton(SKILL_2_BTN);
        nexus.pressButton(SKILL_3_BTN);
        nexus.pressButton(SKILL_4_BTN);
        Nexus.threadWait(1500);
    }

    private boolean isTargetAlive(){
        //TODO
        return true;
    }
}
