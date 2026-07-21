package Moonlight.mod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;


public class ClientWrapper {
    public static @Nullable Level getLevel() {
        return Minecraft.getInstance().level;
    }

    public static @Nullable Player getPlayer() {
        return Minecraft.getInstance().player;
    }

    public static void setOverlayMessage(Component component, boolean animate) {
        Minecraft.getInstance().gui.setOverlayMessage(component, animate);
    }
}
