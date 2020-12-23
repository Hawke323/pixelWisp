package wowwisp.data;

import java.awt.*;

public class Target {
    @Override
    public String toString() {
        return "Target{" +
                "tagged=" + tagged +
                ", targetLocation=" + targetLocation +
                '}';
    }

    public Target(Point paraLocation){
        this.targetLocation = paraLocation;
    }

    public Point getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Point targetLocation) {
        this.targetLocation = targetLocation;
    }

    public boolean isTagged() {
        return tagged;
    }

    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    private boolean tagged;
    private Point targetLocation;
}
