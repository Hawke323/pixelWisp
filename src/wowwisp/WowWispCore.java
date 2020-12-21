package wowwisp;

import wisp.Nexus;
import wowwisp.data.Target;

import java.awt.event.KeyEvent;
import java.util.Random;

import static wisp.Nexus.print;
import static wisp.Nexus.threadWait;

public class WowWispCore {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        new WowWispCore();
    }

    //______________________________________配置参数
    private int loopInterval = 100;
    private int globalCD = (int) (1.5 * 1000);
    //______________________________________配置参数

    public static Nexus nexus;
    public static LeatherWisp leatherWisp;

    public WowWispCore(){
        nexus = new Nexus();
        nexus.startNexus("leather");
        leatherWisp = new LeatherWisp(nexus);
        this.startLoop();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void startLoop(){
        print("Starting WOWWisp Core...");
        nexus.getCachedThreadPool().execute(() -> {
            long lastCastTime = System.currentTimeMillis();
            while (true) {
                if(lastCastTime + globalCD < System.currentTimeMillis()){
                    nexus.refreshImage();
                    leatherWisp.leatherMobRefresh();
                    Target picked = leatherWisp.getAvailableTarget();
                    if(null != picked){
                        print("取到的单位是" + picked);
                        leatherWisp.tagTarget(picked);
                        nexus.moveMouse(picked.getTargetLocation());
                        nexus.pressButton(KeyEvent.VK_F4);
                        lastCastTime = System.currentTimeMillis();
                    }
                }
                threadWait(loopInterval);
            }
        });
    }

    public void leatherSearchTest(){
        /*for(int i = 0; i < 10; i++){
            print("随机数测试" + (int)(5 * (new Random().nextDouble())));
        }*/

        print("第一次刷新");
        nexus.loadImage("D:\\test\\leather.png");
        leatherWisp.leatherMobRefresh();
        Target picked = leatherWisp.getAvailableTarget();
        print("取到的单位是" + picked);
        leatherWisp.tagTarget(picked);
        nexus.moveMouse(picked.getTargetLocation());
        nexus.pressButton(KeyEvent.VK_F4);

        /*print("第二次刷新");
        nexus.loadImage("D:\\test\\leather2.png");
        leatherWisp.leatherMobRefresh();
        picked = leatherWisp.getAvailableTarget();
        print("取到的单位是" + picked);*/
    }

}