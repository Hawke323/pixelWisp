package kantaiWisp;

import wisp.Nexus;

import static wisp.Nexus.print;

public class KantaiWisp {
    Nexus nexus = new Nexus("poi");

    @SuppressWarnings("InfiniteLoopStatement")
    private void startLoop(){
        print("Starting Core...");
        nexus.getCachedThreadPool().execute(() -> {
            while (true) {
                if (refreshImageSwitch) {
                    refreshImage();
                }
                threadWait(interval);
            }
        });
    }
}
