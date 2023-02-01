package fr.cocoraid.armorstandanimator;

import org.bukkit.util.EulerAngle;

import java.util.LinkedList;

public class SkeletonPart {

    private SkeletonType skeletonType;
    private LinkedList<EulerAngle> frameAnimations;

    public SkeletonPart(SkeletonType skeletonType) {
        this.skeletonType = skeletonType;
        this.frameAnimations = new LinkedList<>();
    }

    public SkeletonType getSkeletonType() {
        return skeletonType;
    }


    public LinkedList<EulerAngle> getFrameAnimations() {
        return frameAnimations;
    }

    public void setFrameAnimations(LinkedList<EulerAngle> frameAnimations) {
        this.frameAnimations = frameAnimations;
    }
}
