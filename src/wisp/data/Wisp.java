package wisp.data;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Wisp {

    public Wisp(){
    }

    @Override
    public String toString() {
        return "Wisp{" +
                "wispName='" + wispName + '\'' +
                ", signatureColors=" + signatureColors +
                ", targetPoints=" + targetPoints +
                ", targetPointsColor=" + targetPointsColor +
                '}';
    }


    public String getWispName() {
        return wispName;
    }

    public void setWispName(String wispName) {
        this.wispName = wispName;
    }

    public List<Color> getSignatureColors() {
        return signatureColors;
    }

    public void setSignatureColors(List<Color> signatureColors) {
        this.signatureColors = signatureColors;
    }

    public List<Point> getTargetPoints() {
        return targetPoints;
    }

    public void setTargetPoints(List<Point> targetPoints) {
        this.targetPoints = targetPoints;
    }

    public List<Color> getTargetPointsColor() {
        return targetPointsColor;
    }

    public void setTargetPointsColor(List<Color> targetPointsColor) {
        this.targetPointsColor = targetPointsColor;
    }

    String wispName;
    List<Color> signatureColors = new ArrayList();
    List<Point> targetPoints = new ArrayList();
    List<Color> targetPointsColor = new ArrayList();
}
