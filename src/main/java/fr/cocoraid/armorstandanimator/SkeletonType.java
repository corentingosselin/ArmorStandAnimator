package fr.cocoraid.armorstandanimator;

public enum SkeletonType {

    HEAD("mixamorig_Head-anim", "mixamorig_Head-Matrix-animation-output-transform-array"),
    LEFT_ARM("mixamorig_LeftArm-anim", "mixamorig_LeftForeArm-Matrix-animation-output-transform-array"),
    RIGHT_ARM("mixamorig_RightArm-anim", "mixamorig_RightForeArm-Matrix-animation-output-transform-array"),

    LEFT_FORE_ARM("mixamorig_LeftForeArm-anim", "mixamorig_LeftForeArm-Matrix-animation-output-transform-array"),
    RIGHT_FORE_ARM("mixamorig_RightForeArm-anim", "mixamorig_RightForeArm-Matrix-animation-output-transform-array"),
    LEFT_LEG("mixamorig_LeftLeg-anim", "mixamorig_LeftLeg-Matrix-animation-output-transform-array"),
    RIGHT_LEG("mixamorig_RightLeg-anim", "mixamorig_RightLeg-Matrix-animation-output-transform-array"),
    LEFT_UP_LEG("mixamorig_LeftUpLeg-anim", "mixamorig_LeftUpLeg-Matrix-animation-output-transform-array"),
    RIGHT_UP_LEG("mixamorig_RightUpLeg-anim", "mixamorig_RightUpLeg-Matrix-animation-output-transform-array"),
    BODY("mixamorig_Hips-anim", "mixamorig_Hips-Matrix-animation-output-transform-array");


    private String name;
    private String outputKey;

    SkeletonType(String name, String outputKey) {
        this.name = name;
        this.outputKey = outputKey;
    }

    public String getName() {
        return name;
    }

    public String getOutputKey() {
        return outputKey;
    }

    public static SkeletonType getSkeletonTypeByName(String name) {
        for (SkeletonType type : values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
