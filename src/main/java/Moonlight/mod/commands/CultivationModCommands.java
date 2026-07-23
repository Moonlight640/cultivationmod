package Moonlight.mod.commands;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.commands.commandList.potentialCommand;
import Moonlight.mod.commands.commandList.talentCommand;
import Moonlight.mod.commands.commandList.rerollCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CultivationMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CultivationModCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        talentCommand.register(event.getDispatcher());
        potentialCommand.register(event.getDispatcher());
        rerollCommand.register(event.getDispatcher());
    }
}
