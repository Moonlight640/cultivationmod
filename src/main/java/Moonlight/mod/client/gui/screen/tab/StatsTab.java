package Moonlight.mod.client.gui.screen.tab;

import Moonlight.mod.CultivationMod;
import Moonlight.mod.client.gui.screen.CultivationScreen;
import Moonlight.mod.data.DataHandler;
import Moonlight.mod.data.capability.IPlayerData;
import Moonlight.mod.data.capability.playerStats.MartialRealm;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Items;

import java.text.DecimalFormat;
import java.util.List;

import static Moonlight.mod.client.gui.screen.CultivationScreen.WINDOW_INSIDE_WIDTH;

public class StatsTab extends CultivationTab {
    private static final Component TITLE = Component.translatable(String.format("gui.%s.stats", CultivationMod.MODID));
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");

    private static final int[] TEST_SPLIT_OFFSETS = new int[] { 0, 10, -10, 25, -25 };

    public StatsTab(Minecraft minecraft, CultivationScreen screen, CultivationTabType type, int index, int page) {
        super(minecraft, screen, type, index, page, Items.BOOKSHELF.getDefaultInstance(), TITLE, BACKGROUND, true);
    }

    private static void drawHead(GuiGraphics pGuiGraphics, ResourceLocation pAtlasLocation, int xOffset, int yOffset, int pSize) {
        int i = 8;
        int j = 8;
        pGuiGraphics.blit(pAtlasLocation, xOffset, yOffset, i * pSize, j * pSize, 8.0F, 8.0F, i, j, 64, 64);
        drawHat(pGuiGraphics, pAtlasLocation, xOffset, yOffset, pSize);
    }

    private static void drawHat(GuiGraphics pGuiGraphics, ResourceLocation pAtlasLocation, int xOffset, int yOffset, int pSize) {
        int i = 8;
        int j = 8;
        RenderSystem.enableBlend();
        pGuiGraphics.blit(pAtlasLocation, xOffset, yOffset, i * pSize, j * pSize, 40.0F, 8.0F, i, j, 64, 64);
        RenderSystem.disableBlend();
    }

    private static void drawUpperBody(GuiGraphics pGuiGraphics, ResourceLocation pAtlasLocation, int xOffset, int yOffset, int pSize) {
        int i = 8;
        int j = 12 / 2;
        pGuiGraphics.blit(pAtlasLocation, xOffset, yOffset, i * pSize, j * pSize, 20.0F, 20.0F, i, j, 64, 64);
    }

    private static void drawRightArm(GuiGraphics pGuiGraphics, ResourceLocation pAtlasLocation, int xOffset, int yOffset, int pSize) {
        int i = 4 / 2;
        int j = 12 / 2;
        pGuiGraphics.blit(pAtlasLocation, xOffset, yOffset, i * pSize, j * pSize, 44.0F, 20.0F, i, j, 64, 64);
    }

    private static void drawLeftArm(GuiGraphics pGuiGraphics, ResourceLocation pAtlasLocation, int xOffset, int yOffset, int pSize) {
        int i = 4 / 2;
        int j = 12 / 2;
        pGuiGraphics.blit(pAtlasLocation, xOffset, yOffset, i * pSize, j * pSize, 36.0F, 52.0F, i, j, 64, 64);
    }

    private static float getMaxWidth(StringSplitter pManager, List<FormattedText> pText) {
        return (float) pText.stream().mapToDouble(pManager::stringWidth).max().orElse(0.0D);
    }

    private List<FormattedText> findOptimalLines(Component pComponent, int pMaxWidth) {
        StringSplitter stringsplitter = this.minecraft.font.getSplitter();
        List<FormattedText> list = null;
        float f = Float.MAX_VALUE;

        for (int i : TEST_SPLIT_OFFSETS) {
            List<FormattedText> list1 = stringsplitter.splitLines(pComponent, pMaxWidth - i, Style.EMPTY);
            float f1 = Math.abs(getMaxWidth(stringsplitter, list1) - (float)pMaxWidth);

            if (f1 <= 10.0F) {
                return list1;
            }
            if (f1 < f) {
                f = f1;
                list = list1;
            }
        }
        return list;
    }

    @Override
    public void drawContents(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.drawContents(pGuiGraphics, pX, pY);

        int i = (this.screen.width - CultivationScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - CultivationScreen.WINDOW_HEIGHT) / 2;

        int xOffset = i + (CultivationScreen.WINDOW_WIDTH - CultivationScreen.WINDOW_INSIDE_WIDTH);
        int yOffset = j + (CultivationScreen.WINDOW_HEIGHT - CultivationScreen.WINDOW_INSIDE_HEIGHT);

        drawHead(pGuiGraphics, this.minecraft.player.getSkinTextureLocation(), xOffset + 12, yOffset, 6);
        drawUpperBody(pGuiGraphics, this.minecraft.player.getSkinTextureLocation(), xOffset + 12, yOffset + 47, 6);
        drawRightArm(pGuiGraphics, this.minecraft.player.getSkinTextureLocation(), xOffset, yOffset + 47, 6);
        drawLeftArm(pGuiGraphics, this.minecraft.player.getSkinTextureLocation(), xOffset + 60, yOffset + 47, 6);

        IPlayerData cap = this.minecraft.player.getCapability(DataHandler.INSTANCE).resolve().orElseThrow();

        MartialRealm realm = cap.getCurrentRealm();

        DecimalFormat format = new DecimalFormat("#.##");

        MutableComponent component = Component.empty();
        component.append(Component.translatable(String.format("gui.%s.stats.realm", CultivationMod.MODID), realm.getName()));
        component.append("\n");
        component.append(Component.translatable(String.format("gui.%s.stats.qi", CultivationMod.MODID), format.format(cap.getCurrentQi()), format.format(cap.getMaxQi())));
        component.append("\n");


        List<FormattedCharSequence> lines = Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(component, Style.EMPTY), WINDOW_INSIDE_WIDTH - 104));

        int offset = 0;

        for (FormattedCharSequence line : lines) {
            pGuiGraphics.drawString(this.minecraft.font, line, xOffset + 80, yOffset + offset, 16777215);
            offset += this.minecraft.font.lineHeight;
        }
    }

    @Override
    public void addWidgets() {

    }
}