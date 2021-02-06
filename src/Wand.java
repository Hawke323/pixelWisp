import com.alibaba.fastjson.JSON;
import com.sun.jna.platform.DesktopWindow;
import kantaiWisp.KantaiWispCore;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.platform.WindowUtils;
import wisp.Adjutant;
import wisp.Nexus;
import wisp.data.Wisp;
import wowwisp.WowWispCore;

import javax.imageio.ImageIO;

import static wisp.Nexus.print;


public class Wand {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        new KantaiWispCore();
    }s

    @Test
    void leatherSearchTest(){
        WowWispCore wowWispCore = new WowWispCore();
        //wowWispCore.leatherSearchTest();
    }

    @Test
    void removeFileSuffix(){
        String str = "haha.png";
        String[] buff = str.split("\\.");
        print("数组是"+ Arrays.toString(buff));
        print("文件名是"+buff[0]);
    }

    @Test
    void getWindowLocationTest(){
        Rectangle rect;
        for (DesktopWindow desktopWindow : WindowUtils.getAllWindows(true)) {
            if (desktopWindow.getTitle().contains("IDEA")) {
                rect = desktopWindow.getLocAndSize();
                print("包含IDEA的窗口的位置是" + rect.toString());
            }
        }
    }

    @Test
    void adjutantTestGenerate(){
        List<Color> signatureColors = new ArrayList<>();
        signatureColors.add(new Color(255,255,255));
        signatureColors.add(new Color(0,0,0));
        signatureColors.add(new Color(237,28,36));
        Adjutant.generateWisps("D:\\test", signatureColors);
    }

    @Test
    void adjutantTestCompare(){
        BufferedImage gameImage = null;
        try {
            gameImage = ImageIO.read(new File("D:\\test\\testTarget.png"));
        } catch (Exception e) {
            print("读取图片失败" + e);
        }
        print("匹配结果是" + Adjutant.compareFolderWisps(gameImage,"D:\\test", 0.5));
    }

    @Test
    void jsonTest(){
        print("JSON序列化测试");
        Wisp wisp = getWisp();

        String jsonOutput= JSON.toJSONString(wisp);
        print(jsonOutput);

        Wisp wispRead = JSON.parseObject(jsonOutput, Wisp.class);
        print(wispRead);
    }

    Wisp getWisp(){
        Wisp wisp = new Wisp();
        wisp.getSignatureColors().add(new Color(255,255,255));
        wisp.getTargetPoints().add(new Point(66,11));
        wisp.getTargetPointsColor().add(new Color(66,66,66));
        return wisp;
    }

    @Test
    void fileTest() throws IOException {

        print("JSON序列化并写入文件测试");
        Wisp wisp = getWisp();
        String jsonOutput= JSON.toJSONString(wisp);
        String jsonInput = "";

        File outputFile = new File("D:\\test\\json.wisp");

        if(!outputFile.exists()){
            if(outputFile.createNewFile()){
                Nexus.print("新建文件");
            }
        }

        //写入文件
        try (OutputStream os = new FileOutputStream(outputFile)) {
            // 把内容转换成字节数组
            byte[] data = jsonOutput.getBytes();
            // 向文件写入内容
            print("待写入字符串" + jsonOutput);
            os.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //读出文件
        try (InputStream is = new FileInputStream(outputFile)) {
            // 创建字节数组
            byte[] data = new byte[1024];
            // 读取内容，放到字节数组里面
            is.read(data);
            print("文件内容:" + new String(data));
            jsonInput = new String(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        print("待反序列化字符串" + jsonInput);
        Wisp wispRead = JSON.parseObject(jsonInput, Wisp.class);
        print(wispRead);
    }

    @Test
    void getFileListTest(){

        File file = new File("D:\\test");
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                System.out.println("文件：" + tempList[i]);
                if(tempList[i].getName().contains(".png")){
                    print("文件是png文件");
                }
            }
            if (tempList[i].isDirectory()) {
                System.out.println("文件夹：" + tempList[i]);
            }
        }
    }

}
