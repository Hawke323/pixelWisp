package wowwisp;

import wisp.Nexus;
import wowwisp.data.Target;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static wisp.Nexus.print;


//寻找所有满血血条并攻击
public class LeatherWisp {
    private Nexus nexus;

    LeatherWisp(Nexus paraNexus){
        this.nexus = paraNexus;
    }

    Color healthBarColor = new Color(170, 29, 28);
    //最小距离 小于这个距离就会被当成同一单位
    int minDistance = 80;
    List<Target> registeredTargets = new ArrayList<>();

    public Target getAvailableTarget(){
        //从list当中返回一个随机的没有被tag的目标
        List<Target> untouchedTargets = new ArrayList<>();
        for(Target iter: registeredTargets){
            if(!iter.isTagged()){
                untouchedTargets.add(iter);
            }
        }
        print("找到" + untouchedTargets.size() + "个可用目标");
        if(untouchedTargets.size() > 0){
            return untouchedTargets.get((int) (new Random().nextDouble() * untouchedTargets.size()));
        }else {
            return null;
        }
    }

    public void tagTarget(Target paraTarget){
        print("tag了目标" + paraTarget);
        paraTarget.setTagged(true);
    }

    public void leatherMobRefresh(){
        //首先获得所有血条的坐标，然后和现有的比较并去重，将新增的加入
        List<Point> barList = getMobHealthBarLocations();
        this.registerNewTarget(barList);

        //从registeredTargets中移除此次搜索没有找到的单位
        this.removeNonExistsTargets(barList);
    }

    private void registerNewTarget(List<Point> barList){
        for(Point iter: barList){
            boolean exists = false;
            for(Target iterTar: registeredTargets){
                if(getDistance(iter, iterTar.getTargetLocation()) < minDistance){
                    exists = true;
                    break;
                }
            }
            if(!exists){
                registeredTargets.add(new Target(iter));
                print("新增单位" + iter);
            }
        }
    }

    private void removeNonExistsTargets(List<Point> barList){
        Iterator<Target> iter = registeredTargets.iterator();
        while (iter.hasNext()) {
            boolean exists = false;
            Target iterTarget = iter.next();
            for(Point iterPoints: barList){
                if(getDistance(iterPoints, iterTarget.getTargetLocation()) < minDistance){
                    exists = true;
                    break;
                }
            }
            if(!exists){
                print("移除单位" + iterTarget.getTargetLocation());
                iter.remove();
            }
        }
    }

    private int getDistance(Point point1, Point point2){
        return (int) (Math.pow(Math.abs(point1.getX() - point2.getX()), 2) + Math.pow(Math.abs(point1.getY() - point2.getY()), 2));
    }

    List<Point> getMobHealthBarLocations(){
        long timeStarted = System.currentTimeMillis();
        print("开始检测");
        List<Point> targetPoints = new ArrayList<>();
        //逐行扫描，寻找匹配颜色，如果之后长度满足，则标记为合法点。搜索血条上边缘，颜色约是170 29 28
        for(int y = 0; y < nexus.getGameImage().getHeight(); y += 2){
            for(int x = 0; x < nexus.getGameImage().getWidth(); x += 100){
                //print("检测位置" + x + "," + y);
                if(isFullHealthBar(x, y)){
                    //print("发现血条");
                    targetPoints.add(new Point(x,y));
                    y += 21;
                    break;
                }
            }
        }
        print("检测完毕 耗时" + (System.currentTimeMillis() - timeStarted));
        return targetPoints;
    }

    private boolean isFullHealthBar(int paraStartX, int paraY){
        int length = 0;
        int startingX = paraStartX;
        //如果不是特征颜色，直接返回
        if(!nexus.compareImageColor(healthBarColor, paraStartX , paraY, 0.2, 20)){
            return false;
        }
        //先拉回寻找起始点
        for(int backTraceIndex = 0; paraStartX - backTraceIndex > 0; backTraceIndex ++){
            if(!nexus.compareImageColor(healthBarColor, paraStartX - backTraceIndex, paraY, 0.2, 20)){
                startingX = paraStartX - backTraceIndex + 1;
                break;
            }
        }
        //计算血条长度
        for(int checkingX = 0; checkingX + startingX < nexus.getGameImage().getWidth(); checkingX ++){
            if(nexus.compareImageColor(healthBarColor, checkingX + startingX, paraY, 0.2, 20)){
                length++;
            }else{
                break;
            }
        }
        //print("检测到特征颜色，起始点" + startingX + "," + paraY + "血条长度" + length);
        return length > 182;
    }

}
