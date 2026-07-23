package Moonlight.mod.commands.commandList;

import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
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

public class talentCommand {
     public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(
                Commands.literal("cultivationTalent")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.entity())

                .then(Commands.literal("Qi")
                    .then(Commands.literal("set")
                            .then(Commands.argument("talentType", EnumArgument.enumArgument(TalentGrade.class))
                                    .executes((ctx) -> {
                                        TalentGrade talentType = ctx.getArgument("talentType", TalentGrade.class);

                                        return setTalent(EntityArgument.getPlayer(ctx, "player"), "Qi", null, talentType);
                                    }))))
                .then(Commands.literal("Body")
                        .then(Commands.literal("set")
                                .then(Commands.argument("talentType", EnumArgument.enumArgument(TalentGrade.class))
                                        .executes((ctx) -> {
                                            TalentGrade talentType = ctx.getArgument("talentType", TalentGrade.class);

                                            return setTalent(EntityArgument.getPlayer(ctx, "player"),
                                                    "Body",null, talentType);
                                        }))))
                .then(Commands.literal("Comprehension")
                        .then(Commands.literal("set")
                                .then(Commands.argument("talentType", EnumArgument.enumArgument(TalentGrade.class))
                                        .executes((ctx) -> {
                                            TalentGrade talentType = ctx.getArgument("talentType", TalentGrade.class);

                                            return setTalent(EntityArgument.getPlayer(ctx, "player"),
                                                    "Comprehension", null, talentType);
                                        }))))
                .then(Commands.literal("WeaponTalents")
                    .then(Commands.argument("weaponType", EnumArgument.enumArgument(WeaponType.class))
                            .then(Commands.literal("set")
                                    .then(Commands.argument("talentType", EnumArgument.enumArgument(TalentGrade.class))
                                            .executes((ctx) -> {
                                                WeaponType weaponType = ctx.getArgument("weaponType", WeaponType.class);
                                                TalentGrade talentType = ctx.getArgument("talentType", TalentGrade.class);

                                                return setTalent(EntityArgument.getPlayer(ctx, "player"),
                                                        "weaponTalents", weaponType, talentType);
                                            })))
                    ))));
     }

    private static int setTalent(ServerPlayer player, String stats, @Nullable WeaponType weaponType, TalentGrade talentGrade) {
        IPlayerData cap = player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

        switch (stats) {
            case "Qi":
                cap.setQiTalent(talentGrade);
                return 1;
            case "Body":
                cap.setBodyTalent(talentGrade);
                return 1;
            case "Comprehension":
                cap.setComprehensionTalent(talentGrade);
                return 1;
            case "weaponTalents":
                cap.setWeaponTalent(weaponType, talentGrade);
                return 1;
        }

        return 0;
    }
}
