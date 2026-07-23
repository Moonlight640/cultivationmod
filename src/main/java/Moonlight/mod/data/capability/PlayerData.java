package Moonlight.mod.data.capability;

import Moonlight.mod.ability.AbilityStopEvent;
import Moonlight.mod.ability.CultivationAbilities;
import Moonlight.mod.ability.base.Ability;
import Moonlight.mod.config.ConfigHolder;
import Moonlight.mod.data.capability.playerStats.*;
import Moonlight.mod.data.capability.playerStats.DelayedTickEvents.DelayAction;
import Moonlight.mod.data.capability.playerStats.DelayedTickEvents.DelayedTickEvent;
import Moonlight.mod.manual.ManualList;
import Moonlight.mod.network.PacketHandler;
import Moonlight.mod.network.packets.s2c.SyncPlayerDataS2C;
import Moonlight.mod.util.CultivationUtil;
import Moonlight.mod.util.EntityUtil;
import Moonlight.mod.util.HelperMethods;
import Moonlight.mod.util.PlayerUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.*;

public class PlayerData implements IPlayerData {
    private boolean initialised;

    private Stat strength;
    private Stat agility;
    private Stat constitution;
    private Stat perception;

    private float output;

    private double currentQi;
    private Stat maxQi;
    private Stat qiRecovery;
    private double qiPurity;
    private double qiControl;

    private TalentGrade bodyTalent;
    private TalentGrade qiTalent;
    private TalentGrade comprehensionTalent;
    private EnumMap<WeaponType, TalentGrade> weaponTalents;

    private PotentialGrade bodyPotential;
    private PotentialGrade qiPotential;
    private PotentialGrade comprehensionPotential;
    private EnumMap<WeaponType, PotentialGrade> weaponPotentials;

    private EnumMap<WeaponType, WeaponMastery> weaponMasteries;

    private List<MartialTechnique> knownTechniques;
    private @Nullable MartialTechnique currentTechnique;

    private InternalArtData currentInternalArt;

    private boolean fatigued;
    private boolean poisoned;
    private boolean bleeding;
    private boolean internalInjury;
    private boolean qiDeviation;

    private List<Title> unlockedTitles;
    private @Nullable Title equippedTitle;

    private Sect currentSect;
    private SectRank sectRank;
    private int sectContributionPoints;

    private MartialRealm currentRealm;

    private double bodyExperience;
    private double qiExperience;

    private LivingEntity owner;

    private boolean meditating;
    private boolean consumingPill;

    private final List<DelayedTickEvent> delayedTickEvents;

    private final Set<Ability> unlocked;

    private @Nullable Ability channeled;
    private int charge;

    private final Set<Ability> toggled;

    private final Map<Ability, Integer> cooldowns;
    private final Map<Ability, Integer> durations;
    private final Map<Ability, Integer> disrupted;

    private int disable;

    private PillMultiplier pillMultiplier;



    private static final UUID MAX_HEALTH_UUID = UUID.fromString("72ff5080-3a82-4a03-8493-3be970039cfe");
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("4979087e-da76-4f8a-93ef-6e5847bfa2ee");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("a2aef906-ed31-49e8-a56c-decccbfa2c1f");
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("9fe023ca-f22b-4429-a5e5-c099387d5441");


    public PlayerData() {
//        this.strength = 0;
//        this.agility = 0;
//        this.constitution = 0;
//        this.perception = 0;
        this.strength = new Stat();
        this.agility = new Stat();
        this.constitution = new Stat();
        this.perception = new Stat();

        this.maxQi = new Stat();
        this.maxQi.setBaseValue(100.0d);
        this.qiRecovery = new Stat();

        this.output = 1.0f;
        this.currentQi = 0.0d;
//        this.maxQi = 100.0f;
//        this.qiRecovery = 0.05f;
        this.qiPurity = 0.0d;
        this.qiControl = 0.0d;

        //this.weaponPotentials = new EnumMap<>(WeaponType.class);
        //this.weaponMasteries = new EnumMap<>(WeaponType.class);
        //this.knownTechniques = new ArrayList<>();
        this.bodyTalent = TalentGrade.DEFECTIVE;
        this.qiTalent = TalentGrade.AVERAGE; //MONSTROUS
        this.comprehensionTalent = TalentGrade.AVERAGE;

        this.bodyPotential = PotentialGrade.DEFECTIVE;
        this.qiPotential = PotentialGrade.AVERAGE; // LEGENDARY
        this.comprehensionPotential = PotentialGrade.AVERAGE;

        this.fatigued = false;
        this.poisoned = false;
        this.bleeding = false;
        this.internalInjury = false;
        this.qiDeviation = false;

        //this.unlockedTitles = new ArrayList<>();

        this.currentSect = Sect.WANDERER;
        this.sectRank = SectRank.NONE;
        this.sectContributionPoints = 0;

        this.currentRealm = MartialRealm.REGULAR;

        this.bodyExperience = 0.0d;
        this.qiExperience = 0.0d;


        this.meditating = false;
        this.consumingPill = false;

        this.currentInternalArt = InternalArtData.NO_MANUAL;

        this.delayedTickEvents = new ArrayList<>();

        this.unlocked = new HashSet<>();

        this.charge = 0;

        this.toggled = new HashSet<>();
        this.cooldowns = new HashMap<>();
        this.disrupted = new HashMap<>();
        this.durations = new HashMap<>();

        this.pillMultiplier = new PillMultiplier(0, 0, 0, 0);

    }

    @Override
    public double getStrength() {
        return this.strength.getValue();
    }

    @Override
    public double getAgility() {
        return this.agility.getValue();
    }

    @Override
    public double getConstitution() {
        return this.constitution.getValue();
    }

    @Override
    public double getPerception() {
        return this.perception.getValue();
    }

    @Override
    public void addStrength(Double amount, Double bonusAmount, Double multiplier) {
        if (amount != null) {
            if (amount < 0) return;
            this.strength.addBaseValue(amount);
        }
        if (bonusAmount != null) {
            if (bonusAmount < 0) return;
            this.strength.addBonusValue(bonusAmount);
        }
        if (multiplier != null) {
            if (multiplier < 0) return;
            this.strength.addMultiplier(multiplier);
        }
    }

    @Override
    public void addAgility(Double amount, Double bonusAmount, Double multiplier) {
        if (amount != null) {
            if (amount < 0) return;
            this.agility.addBaseValue(amount);
        }
        if (bonusAmount != null) {
            if (bonusAmount < 0) return;
            this.agility.addBonusValue(bonusAmount);
        }
        if (multiplier != null) {
            if (multiplier < 0) return;
            this.agility.addMultiplier(multiplier);
        }
    }

    @Override
    public void addConstitution(Double amount, Double bonusAmount, Double multiplier) {
        if (amount != null) {
            if (amount < 0) return;
            this.constitution.addBaseValue(amount);
        }
        if (bonusAmount != null) {
            if (bonusAmount < 0) return;
            this.constitution.addBonusValue(bonusAmount);
        }
        if (multiplier != null) {
            if (multiplier < 0) return;
            this.constitution.addMultiplier(multiplier);
        }
    }

    @Override
    public void addPerception(Double amount, Double bonusAmount, Double multiplier) {
        if (amount != null) {
            if (amount < 0) return;
            this.perception.addBaseValue(amount);
        }
        if (bonusAmount != null) {
            if (bonusAmount < 0) return;
            this.perception.addBonusValue(bonusAmount);
        }
        if (multiplier != null) {
            if (multiplier < 0) return;
            this.perception.addMultiplier(multiplier);
        }
    }

    @Override
    public boolean setStrength(Double amount, Double bonusAmount, Double multiplier) {
        if (amount != null) {
            if (amount < 0) return false;
            this.strength.setBaseValue(amount);
        }
        if (bonusAmount != null) {
            if (bonusAmount < 0) return false;
            this.strength.setBonusValue(bonusAmount);
        }
        if (multiplier != null) {
            if (multiplier < 0) return false;
            this.strength.setMultiplier(multiplier);
        }

        return true;
    }

    @Override
    public boolean setAgility(Double amount, Double bonusAmount, Double multiplier) {
        if (amount != null) {
            if (amount < 0) return false;
            this.agility.setBaseValue(amount);
        }
        if (bonusAmount != null) {
            if (bonusAmount < 0) return false;
            this.agility.setBonusValue(bonusAmount);
        }
        if (multiplier != null) {
            if (multiplier < 0) return false;
            this.agility.setMultiplier(multiplier);
        }

        return true;
    }

    @Override
    public boolean setConstitution(Double amount, Double bonusAmount, Double multiplier) {
        if (amount != null) {
            if (amount < 0) return false;
            this.constitution.setBaseValue(amount);
        }
        if (bonusAmount != null) {
            if (bonusAmount < 0) return false;
            this.constitution.setBonusValue(bonusAmount);
        }
        if (multiplier != null) {
            if (multiplier < 0) return false;
            this.constitution.setMultiplier(multiplier);
        }

        return true;
    }

    @Override
    public boolean setPerception(Double amount, Double bonusAmount, Double multiplier) {
        if (amount != null) {
            if (amount < 0) return false;
            this.perception.setBaseValue(amount);
        }
        if (bonusAmount != null) {
            if (bonusAmount < 0) return false;
            this.perception.setBonusValue(bonusAmount);
        }
        if (multiplier != null) {
            if (multiplier < 0) return false;
            this.perception.setMultiplier(multiplier);
        }

        return true;
    }

    @Override
    public double getCurrentQi() {
        return this.currentQi;
    }

    @Override
    public double getMaxQi() {
        return this.maxQi.getValue();
    }

    @Override
    public double getQiRecovery() {
        return this.qiRecovery.getValue();
    }

    @Override
    public double getQiPurity() {
        return this.qiPurity;
    }

    @Override
    public double getQiControl() {
        return this.qiControl;
    }

    @Override
    public void addQi(double amount) {
        if (amount < 0) return;
        if (this.currentInternalArt != null) {
            amount *= this.currentInternalArt.getManualData().getQiGatheringBoost();
        }
        if (this.pillMultiplier.getQiGainMultiplier() > 0) {
            amount *= this.pillMultiplier.getQiGainMultiplier();
        }
        //TODO: FALLOFF of qiTalent and qiPotential need to be added
        amount *= this.qiTalent.getGrowthMultiplier();

        if (this.currentQi + amount > this.maxQi.getValue()) {
            this.maxQi.addBaseValue(amount * 0.05f);
            this.currentQi = this.maxQi.getValue();
        } else {
            this.currentQi += amount;
        }
    }

    @Override
    public void addMaxQi(Double amount, Double bonusAmount, Double multiplier) {
        if (amount != null) {
            if (amount < 0) return;
            this.maxQi.addBaseValue(amount);
        }
        if (bonusAmount != null) {
            if (bonusAmount < 0) return;
            this.maxQi.addBonusValue(bonusAmount);
        }
        if (multiplier != null) {
            if (multiplier < 0) return;
            this.maxQi.addMultiplier(multiplier);
        }
    }

    @Override
    public void addQiRecovery(Double amount, Double bonusAmount, Double multiplier) {
        if (amount != null) {
            if (amount < 0) return;
            this.qiRecovery.addBaseValue(amount);
        }
        if (bonusAmount != null) {
            if (bonusAmount < 0) return;
            this.qiRecovery.addBonusValue(bonusAmount);
        }
        if (multiplier != null) {
            if (multiplier < 0) return;
            this.qiRecovery.addMultiplier(multiplier);
        }
    }

    @Override
    public void addQiPurity(Double amount) {
        if (amount != null) {
            if (amount < 0) return;
            this.qiPurity += amount;
        }
    }

    @Override
    public void addQiControl(Double amount) {
        if (amount != null) {
            if (amount < 0) return;
            this.qiControl += amount;
        }
    }

    @Override
    public void refillQi() {
        this.currentQi = this.maxQi.getValue();
    }

    @Override
    public boolean setQi(Double amount) {
        if (amount != null) {
            if (amount < 0) return false;
            this.currentQi = amount;
        }

        return true;
    }

    @Override
    public boolean useQi(Double amount) {
        if (amount != null) {
            if (amount < 0) return false;
            if (this.currentQi - amount <= 0) {
                this.fatigued = true;
            }
            this.currentQi -= Math.max(0, amount);
        }

        return false;
    }

    @Override
    public boolean setMaxQi(Double amount, Double bonusAmount, Double multiplier) {
        if (amount != null) {
            if (amount < 0) return false;
            this.maxQi.setBaseValue(amount);
        }
        if (bonusAmount != null) {
            if (bonusAmount < 0) return false;
            this.maxQi.setBonusValue(bonusAmount);
        }
        if (multiplier != null) {
            if (multiplier < 0) return false;
            this.maxQi.setMultiplier(multiplier);
        }

        return true;
    }

    @Override
    public boolean setQiRecovery(Double amount, Double bonusAmount, Double multiplier) {
        if (amount != null) {
            if (amount < 0) return false;
            this.qiRecovery.setBaseValue(amount);
        }
        if (bonusAmount != null) {
            if (bonusAmount < 0) return false;
            this.qiRecovery.setBonusValue(bonusAmount);
        }
        if (multiplier != null) {
            if (multiplier < 0) return false;
            this.qiRecovery.setMultiplier(multiplier);
        }

        return true;
    }

    @Override
    public boolean setQiPurity(Double amount) {
        if (amount != null) {
            if (amount < 0) return false;
            this.qiPurity = amount;
        }

        return true;
    }

    @Override
    public boolean setQiControl(Double amount) {
        if (amount != null) {
            if (amount < 0) return false;
            this.qiControl = amount;
        }

        return true;
    }

    @Override
    public TalentGrade getBodyTalent() {
        return this.bodyTalent;
    }

    @Override
    public TalentGrade getQiTalent() {
        return this.qiTalent;
    }

    @Override
    public TalentGrade getComprehensionTalent() {
        return this.comprehensionTalent;
    }

    @Override
    public TalentGrade getWeaponTalent(WeaponType type) {
        return this.weaponTalents.getOrDefault(type, TalentGrade.AVERAGE);
    }

    @Override
    public void setBodyTalent(TalentGrade talent) {
        this.bodyTalent = talent;
    }

    @Override
    public void setQiTalent(TalentGrade talent) {
        this.qiTalent = talent;
    }

    @Override
    public void setComprehensionTalent(TalentGrade talent) {
        this.comprehensionTalent = talent;
    }

    @Override
    public void setWeaponTalent(WeaponType type, TalentGrade talent) {
        this.weaponTalents.put(type, talent);
    }

    @Override
    public PotentialGrade getBodyPotential() {
        return this.bodyPotential;
    }

    @Override
    public PotentialGrade getQiPotential() {
        return this.qiPotential;
    }

    @Override
    public PotentialGrade getComprehensionPotential() {
        return this.comprehensionPotential;
    }

    @Override
    public PotentialGrade getWeaponPotential(WeaponType type) {
        return this.weaponPotentials.getOrDefault(type, PotentialGrade.AVERAGE);
    }

    @Override
    public void setBodyPotential(PotentialGrade potential) {
        this.bodyPotential = potential;
    }

    @Override
    public void setQiPotential(PotentialGrade potential) {
        this.qiPotential = potential;
    }

    @Override
    public void setComprehensionPotential(PotentialGrade potential) {
        this.comprehensionPotential = potential;
    }

    @Override
    public void setWeaponPotential(WeaponType type, PotentialGrade potential) {
        this.weaponPotentials.put(type, potential);
    }

    @Override
    public WeaponMastery getWeaponMastery(WeaponType type) {
        return this.weaponMasteries.get(type);
    }

    @Override
    public void setWeaponMastery(WeaponType type, MasteryRank masteryRank) {

    }

    @Override
    public List<MartialTechnique> getKnownTechniques() {
        return this.knownTechniques;
    }

    @Override
    public String getInternalArtName() {
        assert this.currentInternalArt != null;
        return this.currentInternalArt.getName();
    }

    @Override
    public ManualGrade getInternalArtQuality() {
        assert this.currentInternalArt != null;
        return this.currentInternalArt.getGrade();
    }

    @Override
    public MartialPath getInternalArtPath() {
        assert this.currentInternalArt != null;
        return this.currentInternalArt.getPath();
    }

    @Override
    public ManualList getInternalArtData() {
        assert this.currentInternalArt != null;
        return this.currentInternalArt.getManualData();
    }

    @Override
    public void setInternalArt(InternalArtData internalArt) {
        this.currentInternalArt = internalArt;
    }

    @Override
    public boolean isFatigued() {
        return this.fatigued;
    }

    @Override
    public boolean isPoisoned() {
        return this.poisoned;
    }

    @Override
    public boolean isBleeding() {
        return this.bleeding;
    }

    @Override
    public boolean hasInternalInjury() {
        return this.internalInjury;
    }

    @Override
    public boolean hasQiDeviation() {
        return this.qiDeviation;
    }

    @Override
    public List<Title> getUnlockedTitles() {
        return this.unlockedTitles;
    }

    @Override
    public Title getCurrentTitle() {
        return this.equippedTitle;
    }

    @Override
    public Sect getSect() {
        return this.currentSect;
    }

    @Override
    public SectRank getRankInSect() {
        return this.sectRank;
    }

    @Override
    public int getSectContributionPoints() {
        return this.sectContributionPoints;
    }

    @Override
    public MartialRealm getCurrentRealm() {
        return this.currentRealm;
    }

    @Override
    public void setCurrentRealm(MartialRealm newRealm) {
        this.currentRealm = newRealm;
    }

    @Override
    public double getBodyExperience() {
        return this.bodyExperience;
    }

    @Override
    public double getQiExperience() { return this.qiExperience; }

    @Override
    public void addBodyExperience(Double amount) {
        if (amount < 0) return;
        if (this.currentInternalArt != null) {
            amount *= this.currentInternalArt.getManualData().getBodyGatheringBoost();
        }
        amount *= this.bodyTalent.getGrowthMultiplier();

        double potentialFalloff = 10000.0d;
        double minMultiplier = 0.05;

        if (this.getBodyExperience() > this.bodyPotential.getPotentialCap()) {
            double overflow = this.getBodyExperience() - this.bodyPotential.getPotentialCap();
            double multiplier = minMultiplier + (1.0 - minMultiplier) * Math.exp(-overflow / potentialFalloff);

            amount *= multiplier;
        }

        //TODO: do calculations for multipliers of experience and potential caps (bodyPotential cap lowers amount gained past it, bodyTalent increases gain, Manual may affect as well, etc etc
        this.bodyExperience += amount;
        PacketHandler.sendToClient(new SyncPlayerDataS2C(this.serializeNBT()), (ServerPlayer) this.owner);
    }

    @Override
    public void addQiExperience(Double amount) {
        if (amount < 0) return;
        if (this.currentInternalArt != null) {
            amount *= this.currentInternalArt.getManualData().getQiGatheringBoost();
        }
        amount *= this.qiTalent.getGrowthMultiplier();

        double potentialFalloff = 10000.0d;
        double minMultiplier = 0.05;

        if (this.getQiExperience() > this.qiPotential.getPotentialCap()) {
            double overflow = this.getQiExperience() - this.qiPotential.getPotentialCap();
            double multiplier = minMultiplier + (1.0 - minMultiplier) * Math.exp(-overflow / potentialFalloff);

            amount *= multiplier;
        }

        //TODO: do calculations for multipliers of experience and potential caps (qiPotential cap lowers amount gained past it, qiTalent increases gain, Manual may affect as well, etc etc
        this.qiExperience += amount;
        PacketHandler.sendToClient(new SyncPlayerDataS2C(this.serializeNBT()), (ServerPlayer) this.owner);
    }

    @Override
    public void setBodyExperience(Double amount) {
        //TODO: ADD PRECAUTIONS
        this.bodyExperience = amount;
        PacketHandler.sendToClient(new SyncPlayerDataS2C(this.serializeNBT()), (ServerPlayer) this.owner);
    }

    @Override
    public void setQiExperience(Double amount) {
        //TODO: ADD PRECAUTIONS
        this.qiExperience = amount;
        PacketHandler.sendToClient(new SyncPlayerDataS2C(this.serializeNBT()), (ServerPlayer) this.owner);
    }

    @Override
    public boolean isMeditating() {
        return this.meditating;
    }

    @Override
    public void setMeditating(boolean value) {
        this.meditating = value;
    }

    @Override
    public boolean isConsumingPill() {
        return this.consumingPill;
    }

    @Override
    public void setConsumingPill(boolean value) {
        this.owner.sendSystemMessage(Component.literal("New Value: " + value));
        this.consumingPill = value;
    }

    @Override
    public void delayTickEvent(DelayAction action, int delay, @Nullable Boolean save) {
        this.delayedTickEvents.add(new DelayedTickEvent(action, delay, save));
    }

    private void updateTickEvents(ServerLevel level) {
        this.delayedTickEvents.removeIf(DelayedTickEvent::finished);

        for (DelayedTickEvent current : new ArrayList<>(this.delayedTickEvents)) {
            current.tick();

            if (current.finished()) {
                current.run(level);
            }
        }
    }

    private void updateCooldowns() {
        Iterator<Map.Entry<Ability, Integer>> iter = this.cooldowns.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Ability, Integer> entry = iter.next();

            int remaining = entry.getValue();
            if (remaining > 0) {
                this.cooldowns.put(entry.getKey(), --remaining);
            } else {
                iter.remove();
            }
        }
    }

    private void updateDurations() {
        Iterator<Map.Entry<Ability, Integer>> iter = this.durations.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Ability, Integer> entry = iter.next();

            Ability ability = entry.getKey();

            if (!this.isChanneling(ability)) {
                iter.remove();
                continue;
            }

            int remaining = entry.getValue();
            if (remaining >= 0) {
                this.durations.put(entry.getKey(), --remaining);
            } else {
                if (ability instanceof Ability.IToggled) {
                    if (this.hasToggled(ability)) {
                        this.toggle(ability);
                    } else if (ability instanceof Ability.IChannelened) {
                        if (this.isChanneling(ability)) {
                            this.channel(ability);
                        }
                    }
                    iter.remove();
                }
            }
        }
    }

    private void updateToggled() {
        List<Ability> remove = new ArrayList<>();

        for (Ability ability : new ArrayList<>(this.toggled)) {
            if (this.disrupted.containsKey(ability)) {
                remove.add(ability);
                continue;
            }
            Ability.Status status = ability.isStillUsable(this.owner);

            if (status == Ability.Status.SUCCESS || status == Ability.Status.COOLDOWN || (status == Ability.Status.QI && ability instanceof Ability.IAttack)) {
                ability.run(this.owner);

                ((Ability.IToggled) ability).applyModifiers(this.owner);
            } else {
                remove.add(ability);
            }
        }

        for (Ability ability : remove) {
            this.toggle(ability);
        }
    }

    private void updateChanneled() {
        if (this.channeled != null) {
            //if (this.disrupted.containsKey(this.channeled)) return;
            Ability.Status status = this.channeled.isStillUsable(this.owner);
            if (status == Ability.Status.SUCCESS || status == Ability.Status.COOLDOWN || (status == Ability.Status.QI && this.channeled instanceof Ability.IAttack)) {
                this.channeled.run(this.owner);
            } else {
                this.channel(this.channeled);
            }
            this.charge++;
        } else {
            this.charge = 0;
        }
    }

    private void updateDisrupted() {
        Iterator<Map.Entry<Ability, Integer>> iter = this.disrupted.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Ability, Integer> entry = iter.next();

            int remaining = entry.getValue();

            if (remaining > 0) {
                this.disrupted.put(entry.getKey(), --remaining);
            } else {
                iter.remove();
            }
        }
    }

    private void sync() {
        if (!this.owner.level().isClientSide) {
//            ClientVisualHandler.ClientData data = new ClientVisualHandler.ClientData(this.getToggled(), this.channeled, this.getTraits(), this.getTechniques(), this.getTechnique(), this.getType(),
//                    this.getExperience(), this.getCursedEnergyColor());
//            PacketHandler.broadcast(new SyncVisualDataS2CPacket(this.owner.getUUID(), data.serializeNBT()));
        }
    }

    @Override
    public void tick(LivingEntity owner) {
        if (this.owner == null) {
            this.owner = owner;
        }

        if ((owner.level() instanceof ServerLevel serverLevel)) {
            this.updateCooldowns();
            this.updateDurations();
            this.updateTickEvents(serverLevel);
            this.updateToggled();
            this.updateChanneled();
            this.updateDisrupted();
        }

        if (!this.owner.level().isClientSide) {
            if (this.owner instanceof ServerPlayer player) {
                if (!this.initialised) {
                    this.initialised = true;
                    this.generate(player);
                }
                this.checkAdvancements(player);
            }
        }

        if (this.disable > 0) {
            this.disable--;
        }

        this.currentQi = Math.min(this.currentQi + (ConfigHolder.SERVER.qiRegenAmount.get().doubleValue() * ((this.owner instanceof Player player && ConfigHolder.SERVER.foodQiRegen.get()) ? (player.getFoodData().getFoodLevel() / 20.0f) : 1.0f)), this.getMaxQi());

        double health = (Math.ceil(((this.getRealPower() - 1.0D) * ConfigHolder.SERVER.npcHPMultiplier.get().floatValue()) / 20) * 20) + ConfigHolder.SERVER.npcHPMin.get();
        //double damage = this.getRealPower() * 1.0D;

        if (this.owner instanceof Player player) {
            health = (Math.ceil(((this.getRealPower() - 1.0D) * ConfigHolder.SERVER.playerHPMultiplier.get().doubleValue()) / 20) * 20) + ConfigHolder.SERVER.playerHPMin.get();
        }
        if (this.owner.getMaxHealth() != health && EntityUtil.applyModifier(this.owner, Attributes.MAX_HEALTH, MAX_HEALTH_UUID, "Max health", health, AttributeModifier.Operation.ADDITION)) {
            this.owner.setHealth(this.owner.getMaxHealth());
        }

        //float healthRatio = owner.getHealth() / owner.getMaxHealth();

//        double movement = (0.5D + (this.getQiExperience() + this.getBodyExperience()) / 15000.0D) * 0.025D;
//        double realMovement = 4.0d;
//
//        if (this.getOutput() < 1) {
//            movement *= (0.5 * this.getOutput());
//            realMovement *= (0.5 * this.getOutput());
//            damage *= this.getOutput();
//        }
//
//        if (healthRatio <= 0.5 && healthRatio > 0.35) {
//         movement *= 0.4;
//         realMovement *= 0.4;
//        }
//        if (healthRatio <= 0.35 && healthRatio > 0.2) {
//         movement *= 0.25;
//         realMovement *= 0.25;
//        }
//        if (healthRatio <= 0.2) {
//         movement *= 0.15;
//         realMovement *= 0.15;
//        }
//
//        EntityUtil.applyModifier(this.owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, "Attack damage", damage, AttributeModifier.Operation.ADDITION);
//        EntityUtil.applyModifier(this.owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Movement speed", Math.min(realMovement, movement), AttributeModifier.Operation.ADDITION);
//        EntityUtil.applyArmorBoost(this.owner);
    }

    @Override
    public void generate(ServerPlayer player) {
        this.initialised = true;

        this.currentInternalArt = InternalArtData.NO_MANUAL;
        this.currentTechnique = null;

        //this.unlockedTitles.clear();
        //this.weaponTalents.clear();
        //this.weaponPotentials.clear();
        //this.weaponMasteries.clear();
        this.strength = new Stat();
        this.agility = new Stat();
        this.constitution = new Stat();
        this.perception = new Stat();

        this.currentQi = 0;
        this.maxQi = new Stat();
        this.maxQi.setBaseValue(100.0d);
        this.qiRecovery = new Stat();

        this.currentSect = Sect.WANDERER;
        this.sectRank = SectRank.NONE;
        this.sectContributionPoints = 0;

        this.currentRealm = MartialRealm.REGULAR;

        this.bodyExperience = 0.0f;
        this.qiExperience = 0.0f;

        this.qiPotential = PotentialGrade.valueOf(randomTalPotGrade()); //MONSTROUS
        this.qiTalent = TalentGrade.valueOf(randomTalPotGrade()); //MONSTROUS

        this.bodyPotential = PotentialGrade.valueOf(randomTalPotGrade()); //DEFECTIVE
        this.bodyTalent = TalentGrade.valueOf(randomTalPotGrade()); //DEFECTIVE

        PacketHandler.sendToClient(new SyncPlayerDataS2C(this.serializeNBT()), (ServerPlayer) this.owner);
    }

    private String randomTalPotGrade() {
        Map<String, Integer> weights = ConfigHolder.SERVER.getTalents();

        weights.replaceAll((k, v) -> (int) (v));

        int totalWeight = weights.values().stream().mapToInt(Integer::intValue).sum();
        int roll = HelperMethods.RANDOM.nextInt(totalWeight);
        int cumulative = 0;
        String chosen = null;

        for (Map.Entry<String, Integer> entry : weights.entrySet()) {
            cumulative += entry.getValue();
            if (roll < cumulative) {
                chosen = entry.getKey();
                break;
            }
        }

        return chosen;
    }

    @Override
    public void init(LivingEntity owner) {
        this.owner = owner;
    }

    private void checkAdvancements(ServerPlayer player) {
        //
    }

    @Override
    public void disrupt(Ability ability, int duration) {
        this.disrupted.put(ability, duration);
    }

    @Override
    public boolean isUnlocked(Ability ability) {
        return this.unlocked.contains(ability);
    }

    @Override
    public void lock(Ability ability) {
        this.unlocked.remove(ability);
    }

    @Override
    public void unlock(Ability ability) {
        this.unlocked.add(ability);
    }

    @Override
    public void unlockAll(List<Ability> abilities) {
        this.unlocked.addAll(abilities);
    }

    @Override
    public void lockAll() {
        this.unlocked.clear();
    }

    public void toggle(Ability ability) {
        if (!this.owner.level().isClientSide && this.owner instanceof Player) {
            if (ability.shouldLog(this.owner)) {
                if (this.hasToggled(ability)) {
                    this.owner.sendSystemMessage(ability.getDisableMessage());
                } else {
                    this.owner.sendSystemMessage(ability.getEnableMessage());
                }
            }
        }

        if (this.toggled.contains(ability)) {
            this.toggled.remove(ability);

            //ability.cooldown(this.owner);

            ((Ability.IToggled) ability).onDisabled(this.owner);

            ((Ability.IToggled) ability).removeModifiers(this.owner);

            MinecraftForge.EVENT_BUS.post(new AbilityStopEvent(this.owner, ability));
        } else {
            this.toggled.add(ability);
            ((Ability.IToggled) ability).onEnabled(this.owner);
            ability.run(this.owner);
        }

        this.sync();
    }

    @Override
    public void clearToggled() {
        this.toggled.clear();
    }

    @Override
    public Set<Ability> getToggled() {
        return this.toggled;
    }

    @Override
    public void addCooldown(Ability ability) {
        this.cooldowns.put(ability, ability.getRealCooldown(this.owner));
    }

    @Override
    public void setCooldown(Ability ability, int time) {
        this.cooldowns.put(ability, time);
    }

    @Override
    public void clearCooldown(Ability ability) {
        this.cooldowns.put(ability, 1);
    }

    @Override
    public int getRemainingCooldown(Ability ability) {
        return this.cooldowns.getOrDefault(ability, 0);
    }

    @Override
    public boolean isCooldownDone(Ability ability) {
        return !this.cooldowns.containsKey(ability);
    }

    @Override
    public void addDuration(Ability ability) {
        this.durations.put(ability, ((Ability.IDurationable) ability).getRealDuration(this.owner));
    }

    @Override
    public void resetCooldowns() {
        this.cooldowns.clear();
    }

    @Override
    public @Nullable Ability getChanneled() {
        return this.channeled;
    }

    @Override
    public void channel(@Nullable Ability ability) {
        if (this.channeled != null) {
            ((Ability.IChannelened) this.channeled).onStop(this.owner);

            if (this.channeled instanceof Ability.ICharged charged) {
                if (charged.onRelease(this.owner)) {
                    this.channeled.charge(this.owner);
                }
            }
            if (!this.owner.level().isClientSide && this.channeled.shouldLog(this.owner)) {
                this.owner.sendSystemMessage(this.channeled.getDisableMessage());
            }
            // this.channeled.cooldown(this.owner);
            MinecraftForge.EVENT_BUS.post(new AbilityStopEvent(this.owner, ability));
        }

        if (this.channeled == ability) {
            this.channeled = null;
        } else {
            this.channeled = ability;

            if (this.channeled != null) {
                ((Ability.IChannelened) this.channeled).onStart(this.owner);
                if (!this.owner.level().isClientSide && this.channeled.shouldLog(this.owner)) {
                    this.owner.sendSystemMessage(this.channeled.getEnableMessage());
                }
            }
        }
        this.sync();
    }

    @Override
    public boolean isChanneling(Ability ability) {
        return this.channeled == ability;
    }

    @Override
    public int getCharge(Ability ability) {
        return this.channeled == ability ? this.charge : 0;
    }

    @Override
    public void wipe(ServerPlayer owner) {
        this.setBodyExperience(0.0D);
        this.setQiExperience(0.0D);
        //PlayerUtil.removeAdvancement(owner, "six_eyes");
        this.clearToggled();
        this.lockAll();
        this.generate(owner);
    }

    public boolean hasToggled(Ability ability) {
        return this.toggled.contains(ability)  && !this.disrupted.containsKey(ability);
    }

    @Override
    public double getRealPower() {
        return CultivationUtil.getPower((this.bodyExperience + this.qiExperience));
    }

    @Override
    public double getRealPower(double experience) {
        return CultivationUtil.getPower(experience);
    }

    @Override
    public double getAbilityPower() {
        double power = this.getRealPower() * this.getOutput();
        return power;
    }

    @Override
    public void increaseOutput() {
        this.output = Math.min(1.0f, (this.output + 0.1f));
    }

    @Override
    public void decreaseOutput() {
        this.output = Math.max(0.0f, this.output - 0.1f);
    }

    @Override
    public void maxOutput() {
        this.output = 1.0f;
    }

    @Override
    public float getOutput() {
        return Math.min(1.0f, this.output);
    }

    @Override
    public void setDisable(int duration) {
        if (this.getDisable() >= duration) return;
        this.disable = duration;
    }

    @Override
    public int getDisable() {
        return this.disable;
    }

    @Override
    public boolean hasDisable() {
        return this.disable > 0;
    }

    @Override
    public void resetDisable() {
        this.disable = 0;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("initialised", this.initialised);

        nbt.put("strength", this.strength.serializeNBT());
        nbt.put("agility", this.agility.serializeNBT());
        nbt.put("constitution", this.constitution.serializeNBT());
        nbt.put("perception", this.perception.serializeNBT());

        nbt.putFloat("output", this.output);
        nbt.putDouble("currentQi", this.currentQi);
        nbt.put("maxQi", this.maxQi.serializeNBT());
        nbt.put("qiRecovery", this.qiRecovery.serializeNBT());
        nbt.putDouble("qiPurity", this.qiPurity);
        nbt.putDouble("qiControl", this.qiControl);

        nbt.putInt("bodyTalent", this.bodyTalent.ordinal());
        nbt.putInt("qiTalent", this.qiTalent.ordinal());
        nbt.putInt("comprehensionTalent", this.comprehensionTalent.ordinal());

        nbt.putInt("bodyPotential", this.bodyPotential.ordinal());
        nbt.putInt("qiPotential", this.qiPotential.ordinal());
        nbt.putInt("comprehensionPotential", this.comprehensionPotential.ordinal());

        nbt.putBoolean("fatigued", this.fatigued);
        nbt.putBoolean("poisoned", this.poisoned);
        nbt.putBoolean("bleeding", this.bleeding);
        nbt.putBoolean("internalInjury", this.internalInjury);
        nbt.putBoolean("qiDeviation", this.qiDeviation);

        nbt.putInt("currentSect", this.currentSect.ordinal());
        nbt.putInt("sectRank", this.sectRank.ordinal());
        nbt.putInt("sectContributionPoints", this.sectContributionPoints);

        nbt.putInt("currentRealm", this.currentRealm.ordinal());

        nbt.putDouble("bodyExperience", this.bodyExperience);
        nbt.putDouble("qiExperience", this.qiExperience);



        nbt.putBoolean("consumingPill", this.consumingPill);

        nbt.putInt("disable", this.disable);


        nbt.putDouble("pillMultiplierQi", this.pillMultiplier.getQiGainMultiplier());
        nbt.putDouble("pillMultiplierBody", this.pillMultiplier.getBodyGainMultiplier());
        nbt.putDouble("pillMultiplierQiExp", this.pillMultiplier.getQiExpGainMultiplier());
        nbt.putDouble("pillMultiplierBodyExp", this.pillMultiplier.getBodyExpGainMultiplier());

        ListTag delayedTickEventsTag = new ListTag();
        for (DelayedTickEvent delayedTickEvent : this.delayedTickEvents) {
            delayedTickEventsTag.add(delayedTickEvent.serializeNBT());
        }

        nbt.put("delayedTickEvents", delayedTickEventsTag);

        ListTag unlockedTag = new ListTag();

        for (Ability ability : this.unlocked) {
            ResourceLocation key = CultivationAbilities.getKey(ability);

            if (key == null) continue;

            unlockedTag.add(StringTag.valueOf(key.toString()));
        }

        nbt.put("unlocked", unlockedTag);

        ListTag toggledTag = new ListTag();

        for (Ability ability : this.toggled) {
            ResourceLocation key = CultivationAbilities.getKey(ability);

            if (key == null) continue;

            toggledTag.add(StringTag.valueOf(key.toString()));
        }

        nbt.put("toggled", toggledTag);

        ListTag cooldownsTag = new ListTag();

        for (Map.Entry<Ability, Integer> entry : this.cooldowns.entrySet()) {
            ResourceLocation key = CultivationAbilities.getKey(entry.getKey());

            if (key == null) continue;

            CompoundTag data = new CompoundTag();
            data.putString("identifier", key.toString());
            data.putInt("cooldown", entry.getValue());
            cooldownsTag.add(data);
        }

        nbt.put("cooldowns", cooldownsTag);

        ListTag disruptedTag = new ListTag();

        for (Map.Entry<Ability, Integer> entry : this.disrupted.entrySet()) {
            ResourceLocation key = CultivationAbilities.getKey(entry.getKey());

            if (key == null) continue;

            CompoundTag data = new CompoundTag();
            data.putString("identifier", key.toString());
            data.putInt("duration", entry.getValue());
            disruptedTag.add(data);
        }

        nbt.put("disrupted", disruptedTag);


//        CompoundTag masteriesTag = new CompoundTag();
//
//        for (Map.Entry<WeaponType, WeaponMastery> entry : this.weaponMasteries.entrySet()) {
//            masteriesTag.put(entry.getKey().name(), entry.getValue().serializeNBT());
//        }
//
//        nbt.put("weaponMasteries", masteriesTag);
//
//        ListTag knownTechniquesTag = new ListTag();
//
//        for (MartialTechnique technique : this.knownTechniques) {
//            knownTechniquesTag.add(technique.serializeNBT());
//        }
//
//        nbt.put("knownTechniques", knownTechniquesTag);
//
//        ListTag unlockedTitlesTag = new ListTag();
//
//        for (Title title : this.unlockedTitles)
//            unlockedTitlesTag.add(title.serializeNBT());
//
//        nbt.put("unlockedTitles", unlockedTitlesTag);
//
//        CompoundTag weaponPotentialsTag = new CompoundTag();
//
//        for (var entry : this.weaponPotentials.entrySet()) {
//            weaponPotentialsTag.putString(entry.getKey().name(), entry.getValue().name());
//        }
//
//        nbt.put("weaponPotentials", weaponPotentialsTag);
//
//        CompoundTag weaponTalentsTag = new CompoundTag();
//
//        for (var entry : this.weaponTalents.entrySet()) {
//            weaponTalentsTag.putInt(entry.getKey().name(), entry.getValue().ordinal());
//        }
//
//        nbt.put("weaponTalents", weaponPotentialsTag);

        if (this.currentTechnique != null) {
            nbt.put("currentTechnique", this.currentTechnique.serializeNBT());
        }
        if (this.currentInternalArt != null) {
            nbt.putString("currentInternalArt", this.currentInternalArt.name());
        }
        if (this.equippedTitle != null) {
            nbt.put("equippedTitle", this.equippedTitle.serializeNBT());
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.initialised = nbt.getBoolean("initialised");

        this.strength = Stat.fromNBT(nbt.getCompound("strength"));
        this.agility = Stat.fromNBT(nbt.getCompound("agility"));
        this.constitution = Stat.fromNBT(nbt.getCompound("constitution"));
        this.perception = Stat.fromNBT(nbt.getCompound("perception"));

        this.output = nbt.getFloat("output");
        this.currentQi = nbt.getDouble("currentQi");
        this.maxQi = Stat.fromNBT(nbt.getCompound("maxQi"));
        this.qiRecovery = Stat.fromNBT(nbt.getCompound("qiRecovery"));
        this.qiPurity = nbt.getDouble("qiPurity");
        this.qiControl = nbt.getDouble("qiControl");

        this.bodyTalent = TalentGrade.values()[nbt.getInt("bodyTalent")];
        this.qiTalent = TalentGrade.values()[nbt.getInt("qiTalent")];
        this.comprehensionTalent = TalentGrade.values()[nbt.getInt("comprehensionTalent")];

        this.bodyPotential = PotentialGrade.values()[nbt.getInt("bodyPotential")];
        this.qiPotential = PotentialGrade.values()[nbt.getInt("qiPotential")];;
        this.comprehensionPotential = PotentialGrade.values()[nbt.getInt("comprehensionPotential")];

        this.fatigued = nbt.getBoolean("fatigued");
        this.poisoned = nbt.getBoolean("poisoned");
        this.bleeding = nbt.getBoolean("bleeding");
        this.internalInjury = nbt.getBoolean("internalInjury");
        this.qiDeviation = nbt.getBoolean("qiDeviation");

        this.currentSect = Sect.values()[nbt.getInt("currentSect")];
        this.sectRank = SectRank.values()[nbt.getInt("sectRank")];
        this.sectContributionPoints = nbt.getInt("sectContributionPoints");

        this.currentRealm = MartialRealm.values()[nbt.getInt("currentRealm")];

        this.bodyExperience = nbt.getDouble("bodyExperience");
        this.qiExperience = nbt.getDouble("qiExperience");


        this.consumingPill = nbt.getBoolean("consumingPill");

        this.disable = nbt.getInt("disable");


        this.pillMultiplier = new PillMultiplier(
                nbt.getDouble("pillMultiplierQi"),
                nbt.getDouble("pillMultiplierBody"),
                nbt.getDouble("pillMultiplierQiExp"),
                nbt.getDouble("pillMultiplierBodyExp"));


        ListTag delayedTickEventsTag = nbt.getList("delayedTickEvents", Tag.TAG_COMPOUND);
        this.delayedTickEvents.clear();

        for (Tag tag : delayedTickEventsTag) {
            this.delayedTickEvents.add(
                    DelayedTickEvent.deserializeNBT((CompoundTag) tag)
            );
        }

        this.unlocked.clear();
        for (Tag tag : nbt.getList("unlocked", Tag.TAG_STRING)) {
            this.unlocked.add(CultivationAbilities.getValue(new ResourceLocation(tag.getAsString())));
        }

        this.toggled.clear();
        for (Tag tag : nbt.getList("toggled", Tag.TAG_STRING)) {
            this.toggled.add(CultivationAbilities.getValue(new ResourceLocation(tag.getAsString())));
        }

        this.cooldowns.clear();
        for (Tag tag : nbt.getList("cooldowns", Tag.TAG_COMPOUND)) {
            CompoundTag data = (CompoundTag) tag;
            this.cooldowns.put(CultivationAbilities.getValue(new ResourceLocation(data.getString("identifier"))),
                    data.getInt("cooldown"));
        }

        this.disrupted.clear();
        for (Tag tag : nbt.getList("disrupted", Tag.TAG_COMPOUND)) {
            CompoundTag data = (CompoundTag) tag;
            this.disrupted.put(CultivationAbilities.getValue(new ResourceLocation(data.getString("identifier"))),
                    data.getInt("duration"));

        }

//        CompoundTag masteriesTag = nbt.getCompound("weaponMasteries");
//        this.weaponMasteries.clear();
//
//        for (String key : masteriesTag.getAllKeys()) {
//            WeaponType type = WeaponType.valueOf(key);
//            WeaponMastery mastery = WeaponMastery.fromNBT(masteriesTag.getCompound(key));
//
//            weaponMasteries.put(type, mastery);
//        }
//
//        ListTag knownTechniquesTag = nbt.getList("knownTechniques", Tag.TAG_COMPOUND);
//
//        knownTechniques.clear();
//
//        for (Tag t : knownTechniquesTag) {
//            knownTechniques.add(
//                    MartialTechnique.fromNBT((CompoundTag)t)
//            );
//        }
//
//        ListTag unlockedTitlesTag = nbt.getList("unlockedTitles", Tag.TAG_COMPOUND);
//
//        unlockedTitles.clear();
//
//        for (Tag t : unlockedTitlesTag) {
//            unlockedTitles.add(
//                    Title.fromNBT((CompoundTag)t)
//            );
//        }
//
//        CompoundTag weaponPotentialsTag = nbt.getCompound("weaponPotentials");
//
//        weaponPotentials.clear();
//
//        for (String key : weaponPotentialsTag.getAllKeys()) {
//            weaponPotentials.put(
//                    WeaponType.valueOf(key),
//                    PotentialGrade.values()[weaponPotentialsTag.getInt(key)]
//            );
//        }
//
//        CompoundTag weaponTalentsTag = nbt.getCompound("weaponTalents");
//
//        weaponTalents.clear();
//
//        for (String key : weaponTalentsTag.getAllKeys()) {
//            weaponTalents.put(
//                    WeaponType.valueOf(key),
//                    TalentGrade.values()[weaponTalentsTag.getInt(key)]
//            );
//        }

        if (nbt.contains("currentTechnique", Tag.TAG_COMPOUND)) {
            this.currentTechnique = MartialTechnique.fromNBT(nbt.getCompound("currentTechnique"));
        }
        if (nbt.contains("currentInternalArt")) {
            this.currentInternalArt = InternalArtData.valueOf(nbt.getString("currentInternalArt"));
        }
        if (nbt.contains("equippedTitle", Tag.TAG_COMPOUND)) {
            this.equippedTitle = Title.fromNBT(nbt.getCompound("equippedTitle"));
        }
    }
}
