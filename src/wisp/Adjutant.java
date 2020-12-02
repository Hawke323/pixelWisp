package wisp;

import java.awt.*;

//用于Wisp文件的相关操作
public class Adjutant {

    private Robot robot;

    Adjutant(Robot paraRobot){
        this.robot = paraRobot;
    }

    //处理给定文件名的图片，寻找其中的锚点和对应的颜色，将其保存为单独的wisp文件，记录锚点的坐标和颜色
    static void processImageIntoWisp(){

    }

    //给定一个点颜色矩阵，判断其和指定wisp文件的相似度，从而进行当前界面的判断
    static void compareWithWisp(){

    }

    //给定一个路径，将其中所有的wisp文件和另一个给定的颜色矩阵相比
    static void compareFolderWisps(){

    }
}
