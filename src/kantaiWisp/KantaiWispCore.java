package kantaiWisp;

import wisp.Nexus;

import static wisp.Nexus.print;
import static wisp.Nexus.threadWait;

public class KantaiWispCore {

    //______________________________________配置参数
    private String windowNameSubString = "kantai";
    private int loopInterval = 3 * 60 * 1000;
    //______________________________________配置参数

    private Nexus nexus;
    private ExpeditionWisp expeditionWisp;

    public KantaiWispCore(){
        nexus = new Nexus();
        if(!nexus.startNexus(windowNameSubString)){
            print("未找到窗口 核心启动失败");
            System.exit(-1);
        }
        expeditionWisp = new ExpeditionWisp(nexus);
        this.startLoop();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void startLoop(){
        print("Starting Kantaiwisp Core...");
        nexus.getCachedThreadPool().execute(() -> {
            while (true) {
                //expeditionWisp.expeditionsCheck();
                //expeditionWisp.expeCheckTest();
                threadWait(loopInterval);
            }
        });
    }
}
