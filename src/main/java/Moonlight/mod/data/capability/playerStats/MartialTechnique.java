package Moonlight.mod.data.capability.playerStats;

import net.minecraft.nbt.CompoundTag;

public class MartialTechnique {
    /**
     * Unique identifier.
     */
    private String id;

    /**
     * Current mastery rank.
     */
    private TechniqueRank rank;

    /**
     * Experience towards the next mastery rank.
     */
    private float experience;

    /**
     * Classification of the martial technique.
     *
     * Examples:
     * Sword Art
     * Palm Art
     * Footwork
     * Internal Art
     */
    private TechniqueType type;

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString("id", id);
        tag.putInt("rank", rank.ordinal());
        tag.putFloat("experience", experience);
        tag.putInt("type", type.ordinal());

        return tag;
    }

    public static MartialTechnique fromNBT(CompoundTag tag) {
        MartialTechnique technique = new MartialTechnique();

        technique.id = tag.getString("id");
        technique.rank = TechniqueRank.values()[tag.getInt("rank")];
        technique.experience = tag.getFloat("Experience");
        technique.type = TechniqueType.values()[tag.getInt("type")];

        return technique;
    }
}
