package com.example.realisticlighting.client;

import com.example.realisticlighting.menu.LampMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class LampScreen extends AbstractContainerScreen<LampMenu> {
    public LampScreen(LampMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 198;
        inventoryLabelY = 104;
        titleLabelX = 8;
        titleLabelY = 6;
    }
    @Override protected void init() {
        super.init();
        String[] names = {"gui.realistic_lighting.on", "gui.realistic_lighting.off", "gui.realistic_lighting.redstone"};
        for (int i = 0; i < 3; i++) {
            final int mode = i;
            addRenderableWidget(Button.builder(Component.translatable(names[i]), button -> {
                if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, mode);
            }).bounds(leftPos + 8 + i * 54, topPos + 79, 52, 20).build());
        }
    }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF252A34);
        graphics.fill(leftPos + 3, topPos + 3, leftPos + imageWidth - 3, topPos + imageHeight - 3, 0xFF3D4656);
        // Bulb slot and player inventory slots
        graphics.fill(leftPos + 148, topPos + 30, leftPos + 166, topPos + 48, 0xFF151A23);
        // Draw a 1-pixel dark-gray border around each 18x18 player inventory slot.
        // The slot positions match LampMenu: (8 + col*18, 116 + row*18)
        // and hotbar (8 + col*18, 174). The inset remains the slot background.
        final int gridLine = 0xFF555A63;
        final int slotBackground = 0xFF202733;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = leftPos + 7 + col * 18;
                int y = topPos + 115 + row * 18;
                graphics.fill(x, y, x + 18, y + 18, gridLine);
                graphics.fill(x + 1, y + 1, x + 17, y + 17, slotBackground);
            }
        }
        for (int col = 0; col < 9; col++) {
            int x = leftPos + 7 + col * 18;
            int y = topPos + 173;
            graphics.fill(x, y, x + 18, y + 18, gridLine);
            graphics.fill(x + 1, y + 1, x + 17, y + 17, slotBackground);
        }
        graphics.fill(leftPos + 8, topPos + 58, leftPos + 168, topPos + 67, 0xFF1B2029);
        int width = (int) Math.min(160L, Math.max(0L, (long) menu.energy() * 160 / Math.max(1, menu.capacity())));
        graphics.fill(leftPos + 8, topPos + 58, leftPos + 8 + width, topPos + 67, 0xFF4BC6A5);
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, Component.translatable("itemGroup.realistic_lighting"), titleLabelX, 5, 0xFFFFFF, false);
        graphics.drawString(font, title, titleLabelX, 15, 0xFFFFFF, false);
        graphics.drawString(font, Component.translatable("gui.realistic_lighting.bulb"), 145, 49, 0xFFFFFF, false);
        graphics.drawString(font, "FE: " + menu.energy() + "/" + menu.capacity(), 8, 27, 0xFFFFFF, false);
        graphics.drawString(font, Component.translatable("gui.realistic_lighting.usage", menu.cost(), Component.translatable(menu.lit() ? "gui.realistic_lighting.lit" : "gui.realistic_lighting.dark")), 8, 37, 0xFFFFFF, false);
        graphics.drawString(font, Component.translatable("gui.realistic_lighting.life", String.format(java.util.Locale.ROOT, "%.1f", Math.max(0, menu.lifespan() - menu.age()) / 24000.0)), 8, 47, 0xFFFFFF, false);
        String[] modes = {"gui.realistic_lighting.on", "gui.realistic_lighting.off", "gui.realistic_lighting.redstone"};
        graphics.drawString(font, Component.translatable("gui.realistic_lighting.mode_signal", Component.translatable(modes[Math.max(0, Math.min(2, menu.mode()))]), Component.translatable(menu.redstone() ? "gui.realistic_lighting.yes" : "gui.realistic_lighting.no")), 8, 69, 0xFFFFFF, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, inventoryLabelY, 0xFFFFFF, false);
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        // Highlight the selected operating mode using the synchronized menu data.
        // Draw after the vanilla buttons so the dark selection remains visible
        // even when the cursor is hovering over the selected button.
        int selected = menu.mode();
        if (selected >= 0 && selected <= 2) {
            int x = leftPos + 8 + selected * 54;
            int y = topPos + 79;
            graphics.fill(x + 1, y + 1, x + 51, y + 19, 0xCC10141C);
            String[] labels = {"gui.realistic_lighting.on", "gui.realistic_lighting.off", "gui.realistic_lighting.redstone"};
            graphics.drawCenteredString(font, Component.translatable(labels[selected]), x + 26, y + 6, 0xFFFFFF);
        }
        renderTooltip(graphics, mouseX, mouseY);
    }
}
