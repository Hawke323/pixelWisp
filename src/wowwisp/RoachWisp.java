package wowwisp;

import wisp.Nexus;

import java.awt.event.KeyEvent;

import static wisp.Nexus.print;

public class RoachWisp {

    private Nexus nexus;

    RoachWisp(){
        nexus = new Nexus();
    }

    int LEFT_MOVE_BUTTON = KeyEvent.VK_LEFT;
    int RIGHTMOVE_BUTTON = KeyEvent.VK_RIGHT;
    int AOE_BUTTON = KeyEvent.VK_F4;
    int LOOT_BUTTON = KeyEvent.VK_F6;
    int REGEN_BUTTON = KeyEvent.VK_L;

    public void startLoop(){
        print("Starting HarvestWisp Core...");
        nexus.getCachedThreadPool().execute(this::wispLoop);
    }

    private void wispLoop() {
        //来回移动同时使用aoe攻击技能
        for(int i =0; i < 30; i ++) {
            nexus.pressButton(AOE_BUTTON);
            nexus.buttonDown(LEFT_MOVE_BUTTON);
            Nexus.threadWait(80);
            nexus.buttonUp(LEFT_MOVE_BUTTON);
            Nexus.threadWait(10);
            nexus.buttonDown(RIGHTMOVE_BUTTON);
            Nexus.threadWait(80);
            nexus.buttonUp(RIGHTMOVE_BUTTON);
        }
        //使用拾取
        nexus.pressButton(LOOT_BUTTON);
        Nexus.threadWait(1000);
        //使用恢复
        nexus.pressButton(REGEN_BUTTON);
        //期间等待
        Nexus.threadWait(5000);
    }
}
