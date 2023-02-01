package fr.cocoraid.armorstandanimator;

import java.util.EnumMap;

public class Skeleton {

   private EnumMap<SkeletonType,SkeletonPart> skeletonParts = new EnumMap<>(SkeletonType.class);

    public Skeleton() {
         for(SkeletonType skeletonType : SkeletonType.values()) {
              skeletonParts.put(skeletonType,new SkeletonPart(skeletonType));
         }
    }

    public SkeletonPart getSkeletonPart(SkeletonType skeletonType) {
         return skeletonParts.get(skeletonType);
    }


    public EnumMap<SkeletonType, SkeletonPart> getSkeletonParts() {
        return skeletonParts;
    }

}
