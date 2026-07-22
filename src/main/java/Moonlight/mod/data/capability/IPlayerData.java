package Moonlight.mod.data.capability;

import Moonlight.mod.ability.base.Ability;
import Moonlight.mod.data.capability.playerStats.*;
import Moonlight.mod.data.capability.playerStats.DelayedTickEvents.DelayAction;
import Moonlight.mod.manual.ManualList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public interface IPlayerData {
    // Main Methods
    void tick(LivingEntity owner);
    void generate(ServerPlayer player);
    void init(LivingEntity owner);

    double getRealPower();
    double getRealPower(double experience);
    double getAbilityPower();

    void increaseOutput();
    void decreaseOutput();
    void maxOutput();
    float getOutput();

    int getCoins(CoinType coinType);
    void addCoins(CoinType coinType, int amount);
    boolean removeCoins(CoinType coinType, int amount);

    // Core Attributes
    double getStrength();
    double getAgility();
    double getConstitution();
    double getPerception();

    void addStrength(Double amount, Double bonusAmount, Double multiplier);
    void addAgility(Double amount, Double bonusAmount, Double multiplier);
    void addConstitution(Double amount, Double bonusAmount, Double multiplier);
    void addPerception(Double amount, Double bonusAmount, Double multiplier);

    boolean setStrength(Double amount, Double bonusAmount, Double multiplier);
    boolean setAgility(Double amount, Double bonusAmount, Double multiplier);
    boolean setConstitution(Double amount, Double bonusAmount, Double multiplier);
    boolean setPerception(Double amount, Double bonusAmount, Double multiplier);

    // Qi Data
    double getCurrentQi();
    double getMaxQi();
    double getQiRecovery();
    double getQiPurity();
    double getQiControl();

    void addQi(double amount);
    void addMaxQi(Double amount, Double bonusAmount, Double multiplier);
    void addQiRecovery(Double amount, Double bonusAmount, Double multiplier);
    void addQiPurity(Double amount);
    void addQiControl(Double amount);

    void refillQi();

    boolean setQi(Double amount);
    boolean useQi(Double amount);
    boolean setMaxQi(Double amount, Double bonusAmount, Double multiplier);
    boolean setQiRecovery(Double amount, Double bonusAmount, Double multiplier);
    boolean setQiPurity(Double amount);
    boolean setQiControl(Double amount);

    // Talent Data
    TalentGrade getBodyTalent();
    TalentGrade getQiTalent();
    TalentGrade getComprehensionTalent();
    TalentGrade getWeaponTalent(WeaponType type);

    void setBodyTalent(TalentGrade talent);
    void setQiTalent(TalentGrade talent);
    void setComprehensionTalent(TalentGrade talent);
    void setWeaponTalent(WeaponType type, TalentGrade talent);

    // Potential Data
    PotentialGrade getBodyPotential();
    PotentialGrade getQiPotential();
    PotentialGrade getComprehensionPotential();
    PotentialGrade getWeaponPotential(WeaponType type);

    void setBodyPotential(PotentialGrade potential);
    void setQiPotential(PotentialGrade potential);
    void setComprehensionPotential(PotentialGrade potential);
    void setWeaponPotential(WeaponType type, PotentialGrade potential);

    // Weapon Masteries
    WeaponMastery getWeaponMastery(WeaponType type);

    void setWeaponMastery(WeaponType type, MasteryRank masteryRank);

    // Technique Data
    List<MartialTechnique> getKnownTechniques();

    // Internal Art Data
    // maybe change internal art file? to accommodate the ManualList and have them registered there instead?
    String getInternalArtName();
    ManualGrade getInternalArtQuality();
    MartialPath getInternalArtPath();
    ManualList getInternalArtData();

    void setInternalArt(InternalArtData internalArt);

    // Body Conditions
    boolean isFatigued();
    boolean isPoisoned();
    boolean isBleeding();
    boolean hasInternalInjury();
    boolean hasQiDeviation();

    List<Title> getUnlockedTitles();
    Title getCurrentTitle();

    Sect getSect();
    SectRank getRankInSect();
    int getSectContributionPoints();

    MartialRealm getCurrentRealm();
    void setCurrentRealm(MartialRealm newRealm);

    double getBodyExperience();
    double getQiExperience();

    void addBodyExperience(Double amount);
    void addQiExperience(Double amount);

    void setBodyExperience(Double amount);
    void setQiExperience(Double amount);

    void disrupt(Ability ability, int duration);

    boolean isUnlocked(Ability ability);
    void lock(Ability ability);
    void unlock(Ability ability);
    void unlockAll(List<Ability> abilities);
    void lockAll();

    void clearToggled();
    Set<Ability> getToggled();
    boolean hasToggled(Ability ability);
    void toggle(Ability ability);

    void addCooldown(Ability ability);
    void setCooldown(Ability ability, int time);
    void clearCooldown(Ability ability);
    int getRemainingCooldown(Ability ability);
    boolean isCooldownDone(Ability ability);
    void addDuration(Ability ability);
    void resetCooldowns();

    @Nullable Ability getChanneled();
    void channel(@Nullable Ability ability);
    boolean isChanneling(Ability ability);
    int getCharge(Ability ability);

    void setDisable(int duration);
    int getDisable();
    boolean hasDisable();
    void resetDisable();

    void wipe(ServerPlayer owner);

    boolean isMeditating();
    void setMeditating(boolean value);

    boolean isConsumingPill();
    void setConsumingPill(boolean value);

    void delayTickEvent(DelayAction action, int delay, @Nullable Boolean save);

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
}
