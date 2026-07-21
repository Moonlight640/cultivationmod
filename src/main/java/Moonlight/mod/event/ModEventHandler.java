package Moonlight.mod.event;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.data.capability.playerStats.MartialRealm;
import Moonlight.mod.network.PacketHandler;
import Moonlight.mod.network.packets.s2c.SyncPlayerDataS2C;
import Moonlight.mod.util.CultivationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.Objects;


public class ModEventHandler {
    @Mod.EventBusSubscriber(modid = CultivationMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ModEventHandlerForgeEvents {
        @SubscribeEvent
        public static void onExplosion(ExplosionEvent.Detonate event) {
            // Do stuff
            //        Explosion explosion = event.getExplosion();
            //        LivingEntity entity = explosion.getIndirectSourceEntity();
            //
            //        Iterator<BlockPos> iter = explosion.getToBlow().iterator();
            //
            //        while (iter.hasNext()) {
            //            BlockPos pos = iter.next();
            //            Vec3 center = pos.getCenter();
            //
            //            //iter.remove()
            //            // remove explosion at the blockpos
            //        }
        }

        @SubscribeEvent
        public static void onEntityTeleport(EntityTeleportEvent event) {
//            Level level = event.getEntity().level();
//            if (level.isClientSide) return;
//
//            BlockPos from = BlockPos.containing(event.getPrevX(), event.getPrevY(), event.getPrevZ());
//            BlockPos to = BlockPos.containing(event.getTargetX(), event.getTargetY(), event.getTargetZ());

            //event.setCanceled(true);
            //cancels teleport
        }

        @SubscribeEvent
        public static void onLivingDestroyBlock(LivingDestroyBlockEvent event) {
//            LivingEntity entity = event.getEntity();
//            Vec3 center = event.getPos().getCenter();
            //event.setCanceled(true);
            //cancels the block break
        }

        @SubscribeEvent
        public static void onBlockBreak(BlockEvent.BreakEvent event) {
//            Player player = event.getPlayer();
//            BlockPos pos = event.getPos();
//            Level level = (Level) event.getLevel();
//            BlockState state = level.getBlockState(pos);
//            Block block = state.getBlock();
//
//            Vec3 center = pos.getCenter();
//            event.setCanceled(true);
//
//            // Optional: re-sync block state to client so it doesn’t look broken
//            if (level instanceof ServerLevel serverLevel) {
//                serverLevel.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
//            }
        }

        @SubscribeEvent
        public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
//            Entity entity = event.getEntity();
//            ServerLevel level = (ServerLevel) event.getLevel();
//            BlockPos pos = event.getPos();
//            //event.setCanceled(true)
        }

        @SubscribeEvent
        public static void onSleepFinished(SleepFinishedTimeEvent event) {
            if (!(event.getLevel() instanceof ServerLevel level)) return;

            for (ServerPlayer player : level.players()) {
                if (player.isSleepingLongEnough()) {
                    if (!player.getCapability(DataHandler.INSTANCE).isPresent()) continue;

                    IPlayerData cap = player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
                    //Increase qi up a bit
                    //Recover qi from sleep
                    //Gain a small multiplier instead maybe? and increase qi Regen momentarily? (for like 10seconds?)
                    PacketHandler.sendToClient(new SyncPlayerDataS2C(cap.serializeNBT()), player);
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player)) return;

            //player.setGameMode(GameType.SURVIVAL);
        }

        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            LivingEntity victim = event.getEntity();

            if (!(event.getSource().getEntity() instanceof LivingEntity owner)) return;

            if (!owner.getCapability(DataHandler.INSTANCE).isPresent()) return;
            IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
            //if (cap.hasTrait(Trait.DEATH_PAINTING) && owner.getHealth() < owner.getMaxHealth() * 0.3F && HelperMethods.isMelee(event.getSource())  ) {
            //    victim.addEffect(new MobEffectInstance(MobEffects.POISON, 10 * 20, 4));
            //    victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10 * 20, 0));
            //}
            // If the target is dead we should not trigger any IAttack's
            if (victim.getHealth() - event.getAmount() <= 0.0F) return;

            //cap.attack(event.getSource(), victim);

            // If the target died from the IAttack's then cancel (yes this is very scuffed lmao)
            if (victim.isDeadOrDying()) event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onPlayerClone(PlayerEvent.Clone event) {
            Player original = event.getOriginal();
            Player player = event.getEntity();

            original.reviveCaps();

            IPlayerData oldCap = original.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
            IPlayerData newCap = player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

            newCap.deserializeNBT(oldCap.serializeNBT());

            if (event.isWasDeath()) {
                newCap.setQi(newCap.getMaxQi());
                newCap.resetCooldowns();
                newCap.resetDisable();
                newCap.clearToggled();

                if (!player.level().isClientSide) {
                    PacketHandler.sendToClient(new SyncPlayerDataS2C(newCap.serializeNBT()), (ServerPlayer) player);
                }
            }
            original.invalidateCaps();
        }

        @SubscribeEvent
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
            LivingEntity owner = event.getEntity();

            if (owner.isDeadOrDying()) return;

            if (!owner.getCapability(DataHandler.INSTANCE).isPresent()) return;
            IPlayerData cap = owner.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

            cap.tick(owner);



            //if ((cap.hasTrait(Trait.SIX_EYES) && (!owner.getItemBySlot(EquipmentSlot.HEAD).is(JJKItems.BLINDFOLD.get()) && !CuriosUtil.findSlot(owner, "head").is(JJKItems.BLINDFOLD.get()) ) ) || cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
            //    owner.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
            //}

            if (!owner.level().isClientSide) {

            }


            //if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
            //    owner.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 2, 1, false, false, false));
            //}

            //owner.addEffect(new MobEffectInstance(MobEffects.JUMP, 2, 2, false, false, false));

            if (owner instanceof Player player) {
                //if ( (cap.getType() == JujutsuType.SORCERER && ConfigHolder.SERVER.sorcererSaturation.get()) || (cap.getType() == JujutsuType.CURSE && ConfigHolder.SERVER.curseSaturation.get()) ) {
                    //player.getFoodData().setFoodLevel(20);
                    // TODO: add qi pills that give hidden saturation for a small amount of time and fill hunger (grain pills)
                //}

            }
        }

        @SubscribeEvent
        public static void onLivingFall(LivingFallEvent event) {

            LivingEntity victim = event.getEntity();

//            victim.getCapability(DataHandler.INSTANCE).ifPresent(cap -> {
//                event.setDistance(event.getDistance() * 0.33F);
//            });
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();

            //if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            //LivingEntity victim = event.getEntity();

            //if (victim.level().isClientSide) return;

                //ItemStack stack = source.getDirectEntity() instanceof ThrownChainProjectile chain ? chain.getStack() : attacker.getItemInHand(InteractionHand.MAIN_HAND);

                //List<Item> stacks = new ArrayList<>();
                //stacks.add(stack.getItem());
                //stacks.addAll(CuriosUtil.findSlots(attacker, attacker.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand")
                        //.stream().map(ItemStack::getItem).toList());
                //if (!victim.getCapability(DataHandler.INSTANCE).isPresent()) return;
                //IPlayerData cap = victim.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
//                if (cap.getType() == JujutsuType.CURSE && !cap.hasTrait(Trait.DEATH_PAINTING) ) {
//                    boolean cursed = false;
//
//                    if (event.getSource() instanceof JJKDamageSources.JujutsuDamageSource) {
//                        cursed = true;
//                    } else if (HelperMethods.isMelee(source) && (stacks.stream().anyMatch(item -> item instanceof CursedToolItem))) {
//                        cursed = true;
//                    } else if (attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
//                        // ISorcererData attackerCap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
//                        cursed = true;
//                    }
//
//                    if (!cursed) {
//                        event.setCanceled(true);
//                    }
//                }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingHurt(LivingHurtEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;
            DamageSource source = event.getSource();


            Entity attackerEntity = source.getEntity();

            if (attackerEntity instanceof Projectile projectile) {
                attackerEntity = projectile.getOwner();
            }

            if (!(attackerEntity instanceof LivingEntity attacker)) return;

            if (victim.getCapability(DataHandler.INSTANCE).isPresent()) {
                IPlayerData victimCap = victim.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
                if (victimCap != null) {
                    victimCap.addBodyExperience(event.getAmount() * 0.05d);


                    // do a check if whether the attack is of Qi or Normal.
                    //float newDamage = (event.getAmount() / victimCap.getBodyExperience())
                    //event.setAmount(newDamage);
                }
            }

            if (!victim.getCapability(DataHandler.INSTANCE).isPresent()) return;

            IPlayerData cap = victim.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

            double armor = CultivationUtil.getDefense(cap.getBodyExperience());

//            if (victim instanceof Player) {
//                armor*=ConfigHolder.SERVER.jujutsuDefenseMult.get().floatValue();
//            }


            double blocked = event.getAmount()/armor;
            event.setAmount((float) blocked);
        }

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            if (!victim.getCapability(DataHandler.INSTANCE).isPresent()) return;
            IPlayerData victimCap = victim.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

//            switch (victimCap.getType()) {
//                case SORCERER -> {
//                    if (HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.sorcererFleshRarity.get()) == 0) {
//                        ItemStack stack = new ItemStack(JJKItems.SORCERER_FLESH.get());
//                        CursedEnergyFleshItem.setGrade(stack, SorcererUtil.getGrade(victimCap.getExperience()));
//                        victim.spawnAtLocation(stack);
//                    }
//                }
//                case CURSE -> {
//                    if (HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.curseFleshRarity.get()) == 0) {
//                        ItemStack stack = new ItemStack(JJKItems.CURSE_FLESH.get());
//                        CursedEnergyFleshItem.setGrade(stack, SorcererUtil.getGrade(victimCap.getExperience()));
//                        victim.spawnAtLocation(stack);
//                    }
//                }
//            }
//            if (victim.level() instanceof ServerLevel serverLevel) {
//                for (ServerPlayer player : serverLevel.players()) {
//                    if ( player == victim || !player.getCapability(DataHandler.INSTANCE).isPresent()) continue;
//                    IPlayerData cap = player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
//                    //if (cap.hasTrait(Trait.DEATH_PAINTING)) {
//                    //    player.sendSystemMessage(Component.translatable(String.format("chat.%s.siblingdeath", CultivationMod.MODID), victim.getName()));
//                    //}
//                }
//            }

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

//            if (attacker instanceof ServerPlayer player) {
//                if (victim instanceof HeianSukunaEntity && victimCap.getFingers() == 20) {
//                    PlayerUtil.giveAdvancement(player, "the_strongest_of_all_time");
//                }
//            }
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            final double AMOUNT = 2.5D;
            if (event.player.level().isClientSide) {
                return;
            }
            if (event.phase != TickEvent.Phase.END) {
                return;
            }

            if (!event.player.getCapability(DataHandler.INSTANCE).isPresent()) return;
            IPlayerData cap = event.player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();
            if (cap.isMeditating()) {
                //if (event.player.tickCount % 20 == 0) {
                cap.addQi((AMOUNT / 20));

                //TODO: remove this qi add. only allow qiExperience when using qiMoves?
                cap.addQiExperience((AMOUNT / 20) / 100.0D); // divide by 20 and 100 as well (removed for testing purposes)

                //event.player.sendSystemMessage(Component.literal("Qi: " + cap.getCurrentQi() + "/" + cap.getMaxQi()));
                //}
            }

            if (cap.getCurrentRealm().canAdvance(cap)) {
                MartialRealm martialRealm = cap.getCurrentRealm();

                event.player.sendSystemMessage(Component.literal("\nPlayer has advanced a realm."
                + "\nOld Realm: " + cap.getCurrentRealm().getName().getString()
                + "\nNew Realm: " + martialRealm.getNext().getName().getString()));

                cap.setCurrentRealm(martialRealm.getNext());
                PacketHandler.sendToClient(new SyncPlayerDataS2C(cap.serializeNBT()), (ServerPlayer) event.player);
            }
        }
    }
}
