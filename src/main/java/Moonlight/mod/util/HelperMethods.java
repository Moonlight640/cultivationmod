package Moonlight.mod.util;

import Moonlight.mod.config.ConfigHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class HelperMethods {
    public static final RandomSource RANDOM = RandomSource.createThreadSafe();

    public static boolean isBlockable(LivingEntity entity, Projectile projectile) {
        if (projectile.getOwner() == entity) return false;

        return true;
    }

    public static boolean isBlockable(LivingEntity entity, DamageSource source) {
        if (source.is(DamageTypes.STARVE)) {
            return false;
        }
        if (source.getEntity() == entity) return false;

        //if (source.getEntity() instanceof LivingEntity entity && isMelee(source)) {

        //}

        if (source.getDirectEntity() instanceof Projectile projectile && !isBlockable(entity, projectile)) return false;

        return source.getEntity() != entity;
    }

    public static boolean isMelee(DamageSource source) {
        return !source.isIndirect() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK));
    }

    public static boolean isDestroyable(BlockGetter getter, @Nullable LivingEntity source, BlockPos pos) {
        if (!ConfigHolder.SERVER.destruction.get()) return false;

        if (source != null && !(source instanceof Player) && !source.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) return false;
        BlockState state = getter.getBlockState(pos);
        boolean destroyable = !state.isAir() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE;

        return destroyable;
    }
}
