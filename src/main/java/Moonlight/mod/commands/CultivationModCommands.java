package Moonlight.mod.commands;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.commands.commandList.TalentCommand;
import Moonlight.mod.commands.commandList.rerollCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CultivationMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CultivationModCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        TalentCommand.register(event.getDispatcher());
        rerollCommand.register(event.getDispatcher());
    }
}
