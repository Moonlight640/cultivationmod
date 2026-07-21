package Moonlight.mod.data.capability.playerStats;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class Title {

    /**
     * Unique identifier.
     */
    private String id;

    /**
     * Display name.
     */
    private String name;

    /**
     * Description shown in the UI.
     */
    private String description;

    /**
     * The category this title belongs to.
     */
    private TitleType type;

    /**
     * Optional faction that granted this title.
     */
    private Sect grantingSect;

    /**
     * Display priority.
     *
     * Used when a player owns multiple titles.
     */
    private int priority;

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString("id", id);
        tag.putString("name", name);
        tag.putString("description", description);
        tag.putInt("type", type.ordinal());
        tag.putInt("grantingSect", grantingSect.ordinal());
        tag.putInt("priority", priority);

        return tag;
    }

    public static Title fromNBT(CompoundTag tag) {
        Title technique = new Title();

        technique.id = tag.getString("id");
        technique.name = tag.getString("name");
        technique.description = tag.getString("description");
        technique.type = TitleType.values()[tag.getInt("type")];
        technique.grantingSect = Sect.values()[tag.getInt("grantingSect")];
        technique.priority = tag.getInt("priority");

        return technique;
    }
}