package fr.cocoraid.armorstandanimator;


import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;


public class ArmorstandThread extends BukkitRunnable {

    private ArmorstandAnimator instance;
    private boolean running = false;
    private Skeleton skeleton;
    private ArmorStand armorStand;


    public ArmorstandThread(ArmorstandAnimator instance, Skeleton skeleton, ArmorStand armorStand) {
        this.instance = instance;
        this.skeleton = skeleton;
        this.armorStand = armorStand;
    }


    @Override
    public void run() {
        int i = 0;
        while (running) {
            try {
                long startTime = System.currentTimeMillis();


                for (SkeletonPart skeletonPart : skeleton.getSkeletonParts().values()) {

                    switch (skeletonPart.getSkeletonType()) {
                        case HEAD -> armorStand.setHeadPose(skeletonPart.getFrameAnimations().get(i));

                        case BODY -> {
                            Location l = armorStand.getLocation();
                            l.setYaw((float) Math.toDegrees(skeletonPart.getFrameAnimations().get(i).getX()));
                            armorStand.teleport(l);
                        }
                        case LEFT_ARM -> armorStand.setLeftArmPose(skeletonPart.getFrameAnimations().get(i));
                        case RIGHT_ARM -> armorStand.setRightArmPose(skeletonPart.getFrameAnimations().get(i));
                        case LEFT_LEG -> armorStand.setLeftLegPose(skeletonPart.getFrameAnimations().get(i));
                        case RIGHT_LEG -> armorStand.setRightLegPose(skeletonPart.getFrameAnimations().get(i));

                    }
                }

                long duration = System.currentTimeMillis() - startTime;
                i++;
                if (i >= skeleton.getSkeletonParts().get(SkeletonType.LEFT_LEG).getFrameAnimations().size()) {
                    i = 0;
                }
                // delay the game loop if necessary
                Thread.sleep(30);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void start() {
        this.running = true;
        runTaskAsynchronously(instance);
    }

    public void stop() {
        this.running = false;
        this.cancel();
    }
}
