package Moonlight.mod.commands.commandList;

import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.data.capability.playerStats.PotentialGrade;
import Moonlight.mod.data.capability.playerStats.TalentGrade;
import Moonlight.mod.data.capability.playerStats.WeaponType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.command.EnumArgument;

import javax.annotation.Nullable;

public class potentialCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(
                Commands.literal("cultivationPotential")
                        .requires((player) -> player.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.entity())

                                .then(Commands.literal("Qi")
                                        .then(Commands.literal("set")
                                                .then(Commands.argument("potentialType", EnumArgument.enumArgument(PotentialGrade.class))
                                                        .executes((ctx) -> {
                                                            PotentialGrade potentialType = ctx.getArgument("potentialType", PotentialGrade.class);

                                                            return setTalent(EntityArgument.getPlayer(ctx, "player"), "Qi", null, potentialType);
                                                        }))))
                                .then(Commands.literal("Body")
                                        .then(Commands.literal("set")
                                                .then(Commands.argument("potentialType", EnumArgument.enumArgument(PotentialGrade.class))
                                                        .executes((ctx) -> {
                                                            PotentialGrade potentialType = ctx.getArgument("potentialType", PotentialGrade.class);

                                                            return setTalent(EntityArgument.getPlayer(ctx, "player"),
                                                                    "Body",null, potentialType);
                                                        }))))
                                .then(Commands.literal("Comprehension")
                                        .then(Commands.literal("set")
                                                .then(Commands.argument("potentialType", EnumArgument.enumArgument(PotentialGrade.class))
                                                        .executes((ctx) -> {
                                                            PotentialGrade potentialType = ctx.getArgument("potentialType", PotentialGrade.class);

                                                            return setTalent(EntityArgument.getPlayer(ctx, "player"),
                                                                    "Comprehension", null, potentialType);
                                                        }))))
                                .then(Commands.literal("WeaponTalents")
                                        .then(Commands.argument("weaponType", EnumArgument.enumArgument(WeaponType.class))
                                                .then(Commands.literal("set")
                                                        .then(Commands.argument("potentialType", EnumArgument.enumArgument(PotentialGrade.class))
                                                                .executes((ctx) -> {
                                                                    WeaponType weaponType = ctx.getArgument("weaponType", WeaponType.class);
                                                                    PotentialGrade potentialType = ctx.getArgument("potentialType", PotentialGrade.class);

                                                                    return setTalent(EntityArgument.getPlayer(ctx, "player"),
                                                                            "weaponTalents", weaponType, potentialType);
                                                                })))
                                        ))));
    }

    private static int setTalent(ServerPlayer player, String stats, @Nullable WeaponType weaponType, PotentialGrade potentialGrade) {
        IPlayerData cap = player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

        switch (stats) {
            case "Qi":
                cap.setQiPotential(potentialGrade);
                return 1;
            case "Body":
                cap.setBodyPotential(potentialGrade);
                return 1;
            case "Comprehension":
                cap.setComprehensionPotential(potentialGrade);
                return 1;
            case "weaponTalents":
                cap.setWeaponPotential(weaponType, potentialGrade);
                return 1;
        }

        return 0;
    }
}
