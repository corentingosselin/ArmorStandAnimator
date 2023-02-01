package fr.cocoraid.armorstandanimator;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ArmorstandAnimator extends JavaPlugin {

    private static ArmorstandAnimator instance;
    private AnimationLoader animationLoader = new AnimationLoader();

    @Override
    public void onEnable() {
        instance = this;

        try {
            animationLoader.load();

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDisable() {
        if(thread != null) {
            thread.stop();
            this.thread = null;
        }

    }


    private ArmorstandThread thread;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ArmorStand as = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
            as.setBasePlate(false);
            as.setArms(true);
           if(thread != null) {
                thread.stop();
                this.thread = null;
            }

            this.thread = new ArmorstandThread(instance,animationLoader.getSkeleton(),as);
            thread.start();

        }
        return true;
    }


    public AnimationLoader getAnimationLoader() {
        return animationLoader;
    }
}
