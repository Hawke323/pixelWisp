package wisp;

import wisp.data.GameWindow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


//图片直接相关操作，部分是静态方法
public class Observer {

    private Robot robot;

    Observer(Robot paraRobot){
        this.robot = paraRobot;
    }

    //给定范围，截图获得一张颜色矩阵
    public BufferedImage captureImage(GameWindow paraWindow){
        return robot.createScreenCapture(paraWindow);
    }

    //给定图片，获得指定点的颜色
    public static Color getPixelColor(BufferedImage paraImage, int x, int y){
        int rgbInt = paraImage.getRGB(x, y);
        return new Color(rgbInt);
    }

    //给定两个颜色，还需要提供一个百分比和绝对差值参数（为或关系 满足一个即可），返回是否相似
    public static boolean compareColors(Color paraColor1, Color paraColor2, double paraPercentDiff, int paraAbsDiff){
        return (compareRGB( paraColor1.getRed(), paraColor2.getRed(), paraPercentDiff, paraAbsDiff) &&
                compareRGB( paraColor1.getGreen(), paraColor2.getGreen(), paraPercentDiff, paraAbsDiff) &&
                compareRGB( paraColor2.getBlue(), paraColor2.getBlue(), paraPercentDiff, paraAbsDiff));
    }

    private static boolean compareRGB(int paraNum1, int paraNum2, double paraPercentDiff, int paraAbsDiff){
        double num1 = (double) paraNum1;
        double num2 = (double) paraNum2;
        double percentDiff = Math.abs(num1 - num2)/paraNum2;

        int absDiff = Math.abs(paraNum1 - paraNum2);

        return ( percentDiff <= paraPercentDiff ||  absDiff <= paraAbsDiff);
    }

    //给定一个图，一个坐标和一个颜色，判定该坐标的点的颜色和指定颜色的相似度，同上一个方法
    public static boolean compareImagePixelColor(BufferedImage paraImage, int x, int y, Color paraColor, double paraPercentDiff, int paraAbsDiff){
        Color colorOne = getPixelColor(paraImage, x, y);
        return compareColors(colorOne, paraColor, paraPercentDiff, paraAbsDiff);
    }

    public static boolean compareImagePixelColor(BufferedImage paraImage, Point paraPoint, Color paraColor, double paraPercentDiff, int paraAbsDiff){
        Color colorOne = getPixelColor(paraImage, (int)paraPoint.getX(), (int)paraPoint.getY());
        return compareColors(colorOne, paraColor, paraPercentDiff, paraAbsDiff);
    }

    public static void saveBufferedImage(BufferedImage paraImage, String paraPathName){
        try {
            File outputFile = new File(paraPathName);
            if(!outputFile.exists()){
                if(outputFile.createNewFile()){
                    Nexus.print("新建文件" + paraPathName);
                }
            }
            ImageIO.write(paraImage, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveBufferedImageDefaultPathName(BufferedImage paraImage){
        saveBufferedImage(paraImage, "D:\\test\\" + System.currentTimeMillis()+".png");
    }
}