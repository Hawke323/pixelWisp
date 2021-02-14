package kantaiWisp;

import wisp.Nexus;

import static wisp.Nexus.print;
import static wisp.Nexus.threadWait;

public class KantaiWispCore {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        new KantaiWispCore();
    }

    //______________________________________配置参数
    private String windowNameSubString = "poi";
    private int loopInterval = 5 * 60 * 1000;
    //______________________________________配置参数

    public static Nexus nexus;
    public static ExpeditionWisp expeditionWisp;
    public static FactoryWisp factoryWisp;

    public KantaiWispCore(){
        //启动专属nexus，使用默认参数
        nexus = new Nexus();
        if(!nexus.startNexus(windowNameSubString)){
            print("未找到窗口 核心启动失败");
            return;
        }
        print("启动图形界面");
        KanColleWispGUI.initUI();
        print("图形界面启动完成");
        //启动子模块
        expeditionWisp = new ExpeditionWisp(nexus);
        factoryWisp = new FactoryWisp(nexus);
        nexus.setWispFilePath("D:\\kantai\\wisp");
        nexus.generateReloadWisp();
        //启动循环
        this.startLoop();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void startLoop(){
        print("Starting Kantaiwisp Core...");
        nexus.getCachedThreadPool().execute(() -> {
            while (true) {
                if(KanColleWispGUI.isAutoReceiveLaunchExpo()){
                    expeditionWisp.expeditionsCheck();
                    print("远征检测结束 等待中");
                }else{
                    print("不进行远征检测");
                }
                threadWait(loopInterval);
            }
        });
    }


}
