package wisp;

import com.alibaba.fastjson.JSON;
import wisp.data.Wisp;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static wisp.Nexus.print;

//用于Wisp文件的相关操作
public class Adjutant {


    private List<Wisp> preloadedWispList;

    Adjutant(){
    }

    public void preloadWisps(String paraWispFilePath){
        preloadedWispList = new ArrayList<>();
        print("开始预载wisp文件");
        //拉取目录下的所有wisp文件
        List<File> wispFiles = getTypeFiles(paraWispFilePath, "wisp");
        //遍历所有文件，逐个生成Wisp对象，调用compareWithWisp和对象列表里面的比对，如果有匹配项，break并返回wisp名称
        Wisp iterWisp;
        for(File iter: wispFiles){
            iterWisp = convertFileToWisp(iter);
            preloadedWispList.add(iterWisp);
            print("预载的wisp是" + iterWisp);
        }
        print("预载完成，载入了" + preloadedWispList.size() + "项");
    }

    public String compareWithPreloadedWisps(BufferedImage paraImage, double paraSimThreshold){
        Wisp resultWisp = new Wisp();
        for(Wisp iterWisp: preloadedWispList){
            if(compareWithWisp(paraImage, iterWisp, paraSimThreshold)){
                print("匹配" + iterWisp.getWispName() + "通过");
                resultWisp = iterWisp;
                break;
            }
        }
        return resultWisp.getWispName();
    }

    //将指定目录里面的所有图片文件都转化为wisp文件
    public static void generateWisps(String paraPath, List<Color> paraSignatureColor){
        /*
        读取给定目录下的所有png文件
        对于每个文件进行单独的处理
        * */
        List<File> wispFiles = getTypeFiles(paraPath, "png");
        for(File iter: wispFiles){
            processImageFile(paraPath, iter, paraSignatureColor);
        }
    }

    //单个文件处理成wisp文件
    private static void processImageFile(String paraPath, File paraFile, List<Color> paraSignatureColor){
        /*
        将png文件处理为Wisp对象
        截取文件名，创建.wisp文件，写入用Wisp对象转换成的字符串
        * */
        print("处理文件" + paraFile.getName());
        String fileName = cutSuffix(paraFile.getName());
        Wisp resultWisp = processImageIntoWisp(paraFile, paraSignatureColor);
        //写入到.wisp文件中
        createWispFileWriteString(paraPath + "//" + fileName + ".wisp", JSON.toJSONString(resultWisp));
    }

    //处理给定文件名的图片，寻找其中的锚点和对应的颜色，将其保存为单独的wisp文件，记录锚点的坐标和颜色
    private static Wisp processImageIntoWisp(File paraImageFile, List<Color> paraSignatureColor){
        //转化为bufferedImage
        BufferedImage image;
        try {
            image = ImageIO.read(paraImageFile);
        } catch (Exception e) {
            print("读取图片失败" + e);
            return new Wisp();
        }

        Wisp wisp = new Wisp();
        wisp.setWispName(cutSuffix(paraImageFile.getName()));
        //根据SignatureColor找到对应的点和颜色，写入Wisp中
        for(int row = 0; row < image.getHeight(); row ++){
            for(int col = 0; col < image.getWidth() - (paraSignatureColor.size() + 1); col ++){
                //如果匹配，则将最后一个点加入Wisp文件中
                if(colorMatchingCheck(image, paraSignatureColor,col, row )){
                    Point newSignaturePoint = new Point(col + paraSignatureColor.size(), row);
                    Color newSignaturePointColor = new Color(image.getRGB((int)newSignaturePoint.getX(), (int)newSignaturePoint.getY()));
                    //print("写入签名点" + newSignaturePoint + newSignaturePointColor);
                    wisp.setSignatureColors(paraSignatureColor);
                    wisp.getTargetPoints().add(newSignaturePoint);
                    wisp.getTargetPointsColor().add(newSignaturePointColor);
                }
            }
        }
        print("图片" + paraImageFile.getName() + "处理完成，发现签名点" + wisp.getTargetPoints().size() + "个");
        //序列化Wisp对象，得到字符串，写入.wisp文件，存入图片同一层目录下
        return wisp;
    }

    //如果匹配，则返回true
    private static boolean colorMatchingCheck(BufferedImage paraImage, List<Color> paraSignatureColor, int paraX, int paraY){
        boolean checkerFlag = true;
        Color comparingColor;
        for(int index = 0; index < paraSignatureColor.size(); index++){
            int columnIndex = paraX + index;
            comparingColor = new Color(paraImage.getRGB(columnIndex, paraY));
            //print("检测点位置(" + columnIndex + "," + paraY + ") 颜色是" + comparingColor + "希望的颜色是" + paraSignatureColor.get(index));
            if(!Observer.compareColors(comparingColor, paraSignatureColor.get(index),0.05, 5)){
                checkerFlag = false;
                break;
            }
        }
        return checkerFlag;
    }

    //给定一个点颜色矩阵，判断其和指定Wisp的相似度，从而进行当前界面的判断
    public static boolean compareWithWisp(BufferedImage paraImage, Wisp paraWisp, double paraSimThreshold){
        //循环Wisp的所有点，调用ob里面的静态方法进行比对
        int similarPointCount = 0;
        for(int index = 0; index < paraWisp.getTargetPoints().size(); index ++){
            if(Observer.compareImagePixelColor(paraImage, paraWisp.getTargetPoints().get(index), paraWisp.getTargetPointsColor().get(index),0.05, 5)){
                //print("发现匹配点" +  paraWisp.getTargetPoints().get(index));
                similarPointCount ++;
            }
        }
        double similarityScore = similarPointCount/(double)paraWisp.getTargetPoints().size();
        print("当前画面和" + paraWisp.getWispName() + "的匹配分数是" + similarityScore);
        return similarityScore > paraSimThreshold;
    }

    //使用源文件路径，将其中所有的wisp文件和另一个给定的颜色矩阵相比，给出匹配的wisp文件的名称
    public static String compareFolderWisps(BufferedImage paraImage, String paraPath, double paraSimThreshold){
        //拉取目录下的所有wisp文件
        List<File> wispFiles = getTypeFiles(paraPath, "wisp");
        //遍历所有文件，逐个生成Wisp对象，调用compareWithWisp和对象列表里面的比对，如果有匹配项，break并返回wisp名称
        Wisp resultWisp = new Wisp();
        Wisp iterWisp;
        for(File iter: wispFiles){
            iterWisp = convertFileToWisp(iter);
            print("读取的wisp是" + iterWisp);
            if(compareWithWisp(paraImage, iterWisp, paraSimThreshold)){
                print("匹配" + iterWisp.getWispName() + "通过");
                resultWisp = iterWisp;
                break;
            }
        }
        return resultWisp.getWispName();
    }

    private static List<File> getTypeFiles(String paraPath, String paraSuffix){
        List<File> qualifiedFiles = new ArrayList<>();

        File filepath = new File(paraPath);
        File[] fileList = filepath.listFiles();
        String actualSuffix =  "." + paraSuffix;

        assert (!Objects.isNull(fileList)):"文件列表为空";

        for(File iter: fileList){
            if (iter.isFile() && iter.getName().contains(actualSuffix)) {
                qualifiedFiles.add(iter);
            }
        }
        return qualifiedFiles;
    }

    private static String cutSuffix(String originalFileName){
        String[] buffer = originalFileName.split("\\.");
        return buffer[0];
    }

    private static Wisp convertFileToWisp(File paraWispFile){
        String wispContentString = "";
        try (InputStream inputStream = new FileInputStream(paraWispFile)) {
            // 创建字节数组
            byte[] data = new byte[1024];
            // 读取内容，放到字节数组里面
            inputStream.read(data);
            wispContentString = new String(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.parseObject(wispContentString, Wisp.class);
    }

    private static void createWispFileWriteString(String paraPathName, String paraContent){
        File outputFile = new File(paraPathName);
        try {
            if(!outputFile.exists()){
                if(outputFile.createNewFile()){
                    print("新建文件" + paraPathName);
                }
            }else{
                if(outputFile.delete() && outputFile.createNewFile()){
                    print("删除并重建文件" + paraPathName);
                }
            }
        } catch (Exception e) {
            print("创建文件" + paraPathName + "失败！" + e);
        }

        try (OutputStream os = new FileOutputStream(outputFile)) {
            // 把内容转换成字节数组
            byte[] data = paraContent.getBytes();
            // 向文件写入内容
            //print("待写入字符串" + paraContent);
            os.write(data);
        } catch (Exception e) {
            print("写入文件" + paraPathName + "失败！" + e);
        }

    }
}
