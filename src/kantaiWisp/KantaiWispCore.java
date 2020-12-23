package kantaiWisp;

import wisp.Nexus;

import static wisp.Nexus.print;
import static wisp.Nexus.threadWait;

public class KantaiWispCore {

    //______________________________________配置参数
    private String windowNameSubString = "poi";
    private int loopInterval = 60 * 1000;
    //______________________________________配置参数

    public static Nexus nexus;
    public static ExpeditionWisp expeditionWisp;

    public KantaiWispCore(){
        //启动专属nexus，使用默认参数
        nexus = new Nexus();
        if(!nexus.startNexus(windowNameSubString)){
            print("未找到窗口 核心启动失败");
            return;
        }
        print("启动图形界面");
        KanColleWisp.initUI();
        print("图形界面启动完成");
        //启动子模块
        expeditionWisp = new ExpeditionWisp(nexus);
        //启动循环
        this.startLoop();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void startLoop(){
        print("Starting Kantaiwisp Core...");
        nexus.getCachedThreadPool().execute(() -> {
            while (true) {
                expeditionWisp.expeditionsCheck();
                print("远征检测结束 等待中");
                //expeditionWisp.expeCheckTest();
                threadWait(loopInterval);
            }
        });
    }


}
