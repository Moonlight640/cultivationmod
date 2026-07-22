package Moonlight.mod.ability.test;


import Moonlight.mod.CultivationMod;
import Moonlight.mod.ability.CultivationAbilities;
import Moonlight.mod.ability.base.Ability;
import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.util.HelperMethods;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// TODO: CHANGE INFINITY TO LIKE A QI PULSE (FREEZES PROJECTILES FOR A QUICK SECOND AND STOPS THEIR MOVEMENT)
public class Infinity extends Ability implements Ability.IToggled {
    private static final double SLOWING_FACTOR = 0.0001D;
    private static final double RANGE = 3.0D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean usesHands() {
        return false;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        return true;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return true;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.0F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        owner.sendSystemMessage(Component.literal("Infinity On"));
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        owner.sendSystemMessage(Component.literal("Infinity Off"));
    }


    public static class FrozenProjectileData extends SavedData {
        public static final String IDENTIFIER = "frozen_projectile_data";
        private final Map<UUID, FrozenProjectileNBT> frozen;

        public FrozenProjectileData() {
            this.frozen = new HashMap<>();
        }

        public static FrozenProjectileData load(CompoundTag nbt) {
            FrozenProjectileData data = new FrozenProjectileData();
            ListTag frozenTag = nbt.getList("frozen", Tag.TAG_COMPOUND);

            for (Tag tag : frozenTag) {
                FrozenProjectileNBT pNBT = new FrozenProjectileNBT((CompoundTag) tag);
                data.frozen.put(pNBT.getTarget(), pNBT);
            }
            return data;
        }

        @Override
        public @NotNull CompoundTag save(CompoundTag nbt) {
            ListTag frozenTag = new ListTag();
            frozenTag.addAll(this.frozen.values());
            nbt.put("frozen", frozenTag);
            return nbt;
        }

        public void add(LivingEntity source, Projectile target) {
            if (!this.frozen.containsKey(target.getUUID())) {
                this.frozen.put(target.getUUID(), new FrozenProjectileNBT(source, target));
                this.setDirty();
            }
        }

//        public void add(LivingEntity source, LivingEntity target) {
//            if (!this.frozen.containsKey(target.getUUID())) {
//                this.frozen.put(target.getUUID(), new FrozenProjectileNBT(source, target));
//                this.setDirty();
//            }
//        }

        public void tick(ServerLevel level) {
            Iterator<FrozenProjectileNBT> iter = this.frozen.values().iterator();

            while (iter.hasNext()) {
                FrozenProjectileNBT nbt = iter.next();
                Entity projectile = level.getEntity(nbt.getTarget());

                if (!(level.getEntity(nbt.getSource()) instanceof LivingEntity entity) || entity.isDeadOrDying()) {
                    if (projectile != null) {
                        System.out.println("Proj Name: " + projectile.getName() + "\nDelta Movement: " + nbt.getMovement());

                        projectile.setDeltaMovement(nbt.getMovement());
                        projectile.setNoGravity(true);

                        projectile.setXRot(nbt.getXRot());
                        projectile.setYRot(nbt.getYRot());
                        projectile.xRotO = nbt.getXRot();
                        projectile.yRotO = nbt.getYRot();
                        //projectile.setOldPosAndRot();

                        projectile.hurtMarked = true;
                    }
                    iter.remove();
                    this.setDirty();
                    continue;
                }
                if (projectile == null) {
                    iter.remove();
                    this.setDirty();
                    continue;
                }

                Vec3 forward = projectile.getLookAngle();
                Vec3 start = entity.position().add(forward.scale(entity.getBbWidth() / 2.0F));
                Vec3 end = projectile.position().add(forward.scale(-projectile.getBbWidth() / 2.0F));
                float dx = (float) (start.x - end.x);
                float dy = (float) (start.y - end.y);
                float dz = (float) (start.z - end.z);
                float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

                if (CultivationAbilities.hasToggled(entity, CultivationAbilities.INFINITY.get()) && distance <= RANGE) {
                    Vec3 original = nbt.getMovement();
                    double t = Math.min(distance / (RANGE + 5), 0.75);
                    double speedMultiplier = t * t * t * t;
                    if (distance <= 1.0F) speedMultiplier = 0.0001D;
                    projectile.setDeltaMovement(projectile.getDeltaMovement().scale(Math.min(speedMultiplier, distance * speedMultiplier)));
                    System.out.println("Original Movement: " + original);

                    projectile.setXRot(nbt.getXRot());
                    projectile.setYRot(nbt.getYRot());
                    projectile.xRotO = nbt.getXRot();
                    projectile.yRotO = nbt.getYRot();
                    //projectile.setOldPosAndRot();

                    projectile.setNoGravity(true);
                    projectile.hurtMarked = true;
                } else {
                    projectile.setDeltaMovement(0D, 0D, 0D);//nbt.getMovement());
                    projectile.setNoGravity(false);

                    projectile.setXRot(nbt.getXRot());
                    projectile.setYRot(nbt.getYRot());
                    projectile.xRotO = nbt.getXRot();
                    projectile.yRotO = nbt.getYRot();
                    //projectile.setOldPosAndRot();

                    System.out.println("Movement Restore: " + nbt.getMovement() + "\nRange: " + (distance <= RANGE));

                    projectile.hurtMarked = true;
                    iter.remove();
                    this.setDirty();

                }
            }
        }
    }

    private static class FrozenProjectileNBT extends CompoundTag {
        public FrozenProjectileNBT(LivingEntity source, Projectile target) {
            this.putUUID("source", source.getUUID());
            this.putUUID("target", target.getUUID());

            Vec3 movement = target.getDeltaMovement();
            this.putDouble("movement_x", movement.x);
            this.putDouble("movement_y", movement.y);
            this.putDouble("movement_z", movement.z);

            this.putFloat("xRot", target.getXRot());
            this.putFloat("yRot", target.getYRot());
        }

        public FrozenProjectileNBT(LivingEntity source, LivingEntity target) {
            this.putUUID("source", source.getUUID());
            this.putUUID("target", target.getUUID());

            Vec3 movement = target.getDeltaMovement();
            this.putDouble("movement_x", movement.x);
            this.putDouble("movement_y", movement.y);
            this.putDouble("movement_z", movement.z);

            this.putFloat("xRot", target.getXRot());
            this.putFloat("yRot", target.getYRot());
        }

        public FrozenProjectileNBT(CompoundTag nbt) {
            this.putUUID("source", nbt.getUUID("source"));
            this.putUUID("target", nbt.getUUID("target"));

            this.putDouble("movement_x", nbt.getDouble("movement_x"));
            this.putDouble("movement_y", nbt.getDouble("movement_y"));
            this.putDouble("movement_z", nbt.getDouble("movement_z"));

            this.putFloat("xRot", nbt.getFloat("xRot"));
            this.putFloat("yRot", nbt.getFloat("yRot"));
        }

        public UUID getSource() {
            return this.getUUID("source");
        }

        public UUID getTarget() {
            return this.getUUID("target");
        }

        public Vec3 getMovement() {
            double x = this.getDouble("movement_x");
            double y = this.getDouble("movement_y");
            double z = this.getDouble("movement_z");
            return new Vec3(x, y, z);
        }

        public float getXRot() {
            return this.getFloat("xRot");
        }

        public float getYRot() {
            return this.getFloat("yRot");
        }
    }


    @Mod.EventBusSubscriber(modid = CultivationMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class InfinityForgeEvents {
        @SubscribeEvent
        public static void onLevelTick(TickEvent.LevelTickEvent event) {
            if (event.phase == TickEvent.Phase.START) return;

            if (event.level instanceof ServerLevel level) {
                FrozenProjectileData data = level.getDataStorage().computeIfAbsent(FrozenProjectileData::load, FrozenProjectileData::new,
                        FrozenProjectileData.IDENTIFIER);
                data.tick(level);
            }
        }

        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            if (!(event.getRayTraceResult() instanceof EntityHitResult hit)) return;
            if (!(hit.getEntity() instanceof LivingEntity entity)) return;
            if (!(entity.level() instanceof ServerLevel level)) return;
            if(!CultivationAbilities.hasToggled(entity, CultivationAbilities.INFINITY.get())) return;

            FrozenProjectileData data = level.getDataStorage().computeIfAbsent(FrozenProjectileData::load, FrozenProjectileData::new,
                    FrozenProjectileData.IDENTIFIER);

            Projectile projectile = event.getProjectile();
            if (!HelperMethods.isBlockable(entity, projectile)) return;

            data.add(entity, projectile);

            //event.setImpactResult(ProjectileImpactEvent.ImpactResult.STOP_AT_CURRENT_NO_DAMAGE);
            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
            LivingEntity target = event.getEntity();
            if (!(target.level() instanceof ServerLevel level)) return;

            FrozenProjectileData data = level.getDataStorage().computeIfAbsent(FrozenProjectileData::load, FrozenProjectileData::new,
                    FrozenProjectileData.IDENTIFIER);

            if (!CultivationAbilities.hasToggled(target, CultivationAbilities.INFINITY.get())) return;

            for (Projectile projectile : level.getEntitiesOfClass(Projectile.class, target.getBoundingBox().inflate(RANGE))) {
                if (!HelperMethods.isBlockable(target, projectile)) continue;

                data.add(target, projectile);
            }

            for (LivingEntity entityTarget : level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(RANGE))) {
                if (entityTarget == target || entityTarget instanceof Warden) {
                    continue;
                }
                if (entityTarget.getCapability(DataHandler.INSTANCE).isPresent()) {
                    IPlayerData cap = entityTarget.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
                }

                double distance = target.distanceTo(entityTarget);
                double t = Math.min(distance / (RANGE + 5), 0.75);
                double speedMultiplier = t * t * t;
                if (distance <= 0.5F) speedMultiplier = 0.0001D;

                entityTarget.setDeltaMovement(entityTarget.getDeltaMovement().scale(Math.min(speedMultiplier, distance * speedMultiplier)));//Math.min(SLOWING_FACTOR, distance * SLOWING_FACTOR)));
            }
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity target = event.getEntity();

            if (target.level().isClientSide) return;
            if (!CultivationAbilities.hasToggled(target, CultivationAbilities.INFINITY.get())) return;

            DamageSource source = event.getSource();

            if (!HelperMethods.isBlockable(target, source)) return;

            if (!(source.getDirectEntity() instanceof Projectile)) {
                if (source.getEntity() instanceof LivingEntity attacker) {
                    if (attacker instanceof Warden || attacker instanceof IronGolem) {
                        target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.MASTER, 1.0F, 1.0F);
                        return;
                    }

                    IPlayerData cap = attacker.getCapability(DataHandler.INSTANCE).resolve().orElse(null);
                    if (cap != null) {
//                        if (cap.hasToggled()) { // ABILITY THAT BYPASSES THIS?
//                            target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.MASTER, 1.0F, 1.0F);
//                            return;
//                        }
                    }
                }
                target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.MASTER, 1.0F, 1.0F);
            }
            event.setCanceled(true);
        }
    }
}
