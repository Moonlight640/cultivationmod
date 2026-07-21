package Moonlight.mod.config;

import Moonlight.mod.data.capability.playerStats.PotentialGrade;
import Moonlight.mod.data.capability.playerStats.TalentGrade;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerConfig {
    public final ForgeConfigSpec.DoubleValue thirdRateExp;
    public final ForgeConfigSpec.DoubleValue secondRateExp;
    public final ForgeConfigSpec.DoubleValue semiFirstRateExp;
    public final ForgeConfigSpec.DoubleValue firstRateExp;
    public final ForgeConfigSpec.DoubleValue masterExp;
    public final ForgeConfigSpec.DoubleValue grandmasterExp;
    public final ForgeConfigSpec.DoubleValue transcendentExp;




    public final ForgeConfigSpec.BooleanValue destruction;
    public final ForgeConfigSpec.BooleanValue foodQiRegen;



    public final ForgeConfigSpec.DoubleValue qiRegenAmount;



    public final ForgeConfigSpec.DoubleValue npcHPMultiplier;
    public final ForgeConfigSpec.DoubleValue npcHPMin;
    public final ForgeConfigSpec.DoubleValue playerHPMultiplier;
    public final ForgeConfigSpec.DoubleValue playerHPMin;




    public ServerConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Progression").push("progression");

        this.thirdRateExp = builder.comment("The experience required for Third Rate")
                .defineInRange("thirdRateExp", 500.0d, 0.0d, 100000.0d);
        this.secondRateExp = builder.comment("The experience required for Second Rate")
                .defineInRange("secondRateExp", 1500.0d, 0.0d, 100000.0d);
        this.semiFirstRateExp = builder.comment("The experience required for Semi-First Rate")
                .defineInRange("semiFirstRateExp", 3500.0d, 0.0d, 100000.0d);
        this.firstRateExp = builder.comment("The experience required for First Rate")
                .defineInRange("firstRateExp", 5000.0d, 0.0d, 100000.0d);
        this.masterExp = builder.comment("The experience required for Master")
                .defineInRange("masterExp", 10000.0d, 0.0d, 100000.0d);
        this.grandmasterExp = builder.comment("The experience required for Grandmaster")
                .defineInRange("grandmasterExp", 25000.0d, 0.0d, 100000.0d);
        this.transcendentExp = builder.comment("The experience required for Transcendent")
                .defineInRange("transcendentExp", 100000, 0.0d, 100000.0d);

        builder.pop();



        builder.comment("Miscellaneous").push("misc");
        this.destruction = builder.comment("Whether destruction is enabled when attacks are used.")
                        .define("destruction", true);
        this.foodQiRegen = builder.comment("When enabled qi regen will scale off hunger.")
                        .define("foodQiRegen", true);


        builder.pop();

        builder.comment("Qi").push("qi");
        this.qiRegenAmount = builder.comment("Amount of Qi regenerated per tick.")
                .defineInRange("qiRegenAmount", 2.0d, 0.0d, 100000.0d);
        builder.pop();


        builder.comment("Player").push("player");
        this.npcHPMultiplier = builder.comment("NPCs Hp Multiplier.")
                .defineInRange("npcHPMultiplier", 1.0D, 0.1D, 1000.0D);
        this.npcHPMin = builder.comment("NPCs Hp Minimum.")
                .defineInRange("npcHPMin", 1.0D, 0.0D, 1000.0D);
        this.playerHPMultiplier = builder.comment("Players Hp Multiplier.")
                .defineInRange("playerHPMultiplier", 1.0D, 0.1D, 1000.0D);
        this.playerHPMin = builder.comment("Players Hp Minimum.")
                .defineInRange("playerHPMin", 1.0D, 0.0D, 1000.0D);

    }

    public Map<String, Integer> getTalents() {
        Map<String, Integer> talentWeights = new HashMap<>();

        List<String> talents = List.of(
                TalentGrade.DEFECTIVE.name(),
                TalentGrade.BELOW_AVERAGE.name(),
                TalentGrade.AVERAGE.name(),
                TalentGrade.ABOVE_AVERAGE.name(),
                TalentGrade.GENIUS.name(),
                TalentGrade.HEAVENLY.name(),
                TalentGrade.MONSTROUS.name()
        );

        for (String t : talents) {
            switch (t) {
                case "DEFECTIVE" -> talentWeights.put(t, 25);
                case "BELOW_AVERAGE" -> talentWeights.put(t, 100);
                case "AVERAGE" -> talentWeights.put(t, 250);
                case "ABOVE_AVERAGE" -> talentWeights.put(t ,100);
                case "GENIUS" -> talentWeights.put(t, 25);
                case "HEAVENLY" -> talentWeights.put(t, 15);
                case "MONSTROUS" -> talentWeights.put(t, 5);
            }
        }

        return talentWeights;
    }
}
