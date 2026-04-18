package io.github.kosianodangoo.trialmonolith.client.screen;

import io.github.kosianodangoo.trialmonolith.client.screen.widget.InputWidget;
import io.github.kosianodangoo.trialmonolith.common.item.DimensionalTeleporterItem;
import io.github.kosianodangoo.trialmonolith.common.item.DimensionalTeleporterItem.DimensionalTeleportTarget;
import io.github.kosianodangoo.trialmonolith.common.network.TeleporterSlot;
import io.github.kosianodangoo.trialmonolith.common.network.TrialMonolithNetwork;
import io.github.kosianodangoo.trialmonolith.common.network.packet.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class DimensionalTeleporterScreen extends Screen {
    public static final Component TITLE = Component.translatable("item.the_trial_monolith.dimensional_teleporter");
    public static final Component NAME_MESSAGE = Component.translatable("gui.the_trial_monolith.dimensional_teleporter.name");
    public static final Component X_MESSAGE = Component.translatable("gui.the_trial_monolith.dimensional_teleporter.x");
    public static final Component Y_MESSAGE = Component.translatable("gui.the_trial_monolith.dimensional_teleporter.y");
    public static final Component Z_MESSAGE = Component.translatable("gui.the_trial_monolith.dimensional_teleporter.z");
    public static final Component DIMENSION_MESSAGE = Component.translatable("gui.the_trial_monolith.dimensional_teleporter.dimension");
    public static final Component USE_HERE_MESSAGE = Component.translatable("gui.the_trial_monolith.dimensional_teleporter.use_here");
    public static final Component ADD_MESSAGE = Component.translatable("gui.the_trial_monolith.dimensional_teleporter.add");
    public static final Component UPDATE_MESSAGE = Component.translatable("gui.the_trial_monolith.dimensional_teleporter.update");
    public static final Component DELETE_MESSAGE = Component.translatable("gui.the_trial_monolith.dimensional_teleporter.delete");
    public static final Component TELEPORT_MESSAGE = Component.translatable("gui.the_trial_monolith.dimensional_teleporter.teleport");

    private final TeleporterSlot slot;
    private final List<DimensionalTeleportTarget> targets;
    private int selected;

    public InputWidget nameInput;
    public InputWidget xPosInput;
    public InputWidget yPosInput;
    public InputWidget zPosInput;
    public InputWidget dimensionInput;
    private WaypointList waypointList;

    public DimensionalTeleporterScreen(ItemStack stack, TeleporterSlot slot) {
        super(TITLE);
        this.slot = slot;
        DimensionalTeleporterItem item = (DimensionalTeleporterItem) stack.getItem();
        this.targets = new ArrayList<>(item.getTargets(stack));
        this.selected = item.getSelected(stack);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_DELETE && !(getFocused() instanceof InputWidget)) {
            deleteWaypoint();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void init() {
        super.init();

        int panelWidth = Math.min(260, this.width - 20);
        int panelX = (this.width - panelWidth) / 2;
        int inputHeight = 14;
        int rowGap = 4;
        int buttonHeight = 20;
        int buttonGap = 4;

        int inputsHeight = inputHeight * 3 + rowGap * 2;
        int buttonsHeight = buttonHeight * 2 + buttonGap;
        int bottomY = this.height - 10;
        int buttonsY = bottomY - buttonsHeight;
        int inputsY = buttonsY - inputsHeight - 8;
        int listTop = 24;
        int listBottom = inputsY - 6;

        waypointList = new WaypointList(panelX, listTop, panelWidth, listBottom - listTop);
        addWidget(waypointList);

        int ix = panelX;
        int iw = panelWidth;
        int coordWidth = (iw - rowGap * 2) / 3;
        int iy = inputsY;

        nameInput = makeInputWidget(ix, iy, iw, inputHeight, NAME_MESSAGE);
        nameInput.setMaxLength(64);
        addRenderableWidget(nameInput);

        iy += inputHeight + rowGap;
        xPosInput = makeInputWidget(ix, iy, coordWidth, inputHeight, X_MESSAGE);
        xPosInput.setFilter(DimensionalTeleporterScreen::isValidNumericInput);
        addRenderableWidget(xPosInput);

        yPosInput = makeInputWidget(ix + coordWidth + rowGap, iy, coordWidth, inputHeight, Y_MESSAGE);
        yPosInput.setFilter(DimensionalTeleporterScreen::isValidNumericInput);
        addRenderableWidget(yPosInput);

        zPosInput = makeInputWidget(ix + (coordWidth + rowGap) * 2, iy, coordWidth, inputHeight, Z_MESSAGE);
        zPosInput.setFilter(DimensionalTeleporterScreen::isValidNumericInput);
        addRenderableWidget(zPosInput);

        iy += inputHeight + rowGap;
        dimensionInput = makeInputWidget(ix, iy, iw, inputHeight, DIMENSION_MESSAGE);
        dimensionInput.setMaxLength(256);
        addRenderableWidget(dimensionInput);

        int row1Y = buttonsY;
        int row1ButtonCount = 4;
        int row1ButtonWidth = (panelWidth - buttonGap * (row1ButtonCount - 1)) / row1ButtonCount;
        int bx = panelX;
        addRenderableWidget(Button.builder(USE_HERE_MESSAGE, b -> fillFromPlayer()).bounds(bx, row1Y, row1ButtonWidth, buttonHeight).build());
        bx += row1ButtonWidth + buttonGap;
        addRenderableWidget(Button.builder(ADD_MESSAGE, b -> addWaypoint()).bounds(bx, row1Y, row1ButtonWidth, buttonHeight).build());
        bx += row1ButtonWidth + buttonGap;
        addRenderableWidget(Button.builder(UPDATE_MESSAGE, b -> updateWaypoint()).bounds(bx, row1Y, row1ButtonWidth, buttonHeight).build());
        bx += row1ButtonWidth + buttonGap;
        addRenderableWidget(Button.builder(DELETE_MESSAGE, b -> deleteWaypoint()).bounds(bx, row1Y, row1ButtonWidth, buttonHeight).build());

        int row2Y = row1Y + buttonHeight + buttonGap;
        addRenderableWidget(Button.builder(TELEPORT_MESSAGE, b -> teleportAndClose()).bounds(panelX, row2Y, panelWidth, buttonHeight).build());

        loadSelectedIntoInputs();
    }

    public InputWidget makeInputWidget(int x, int y, int w, int h, Component message) {
        InputWidget inputWidget = new InputWidget(this.font, x, y, w, h, message);
        inputWidget.innerColor = 0x60000000;
        inputWidget.borderColor = 0xA0FFFFFF;
        inputWidget.setHint(message);
        return inputWidget;
    }

    private static boolean isValidNumericInput(String s) {
        if (s.isEmpty() || s.equals("-") || s.equals(".") || s.equals("-.")) return true;
        return s.matches("-?\\d*\\.?\\d*");
    }

    private void loadSelectedIntoInputs() {
        if (selected < 0 || selected >= targets.size()) return;
        DimensionalTeleportTarget t = targets.get(selected);
        nameInput.setValue(t.name());
        xPosInput.setValue(formatCoord(t.position().x));
        yPosInput.setValue(formatCoord(t.position().y));
        zPosInput.setValue(formatCoord(t.position().z));
        dimensionInput.setValue(t.dimension().location().toString());
    }

    private static String formatCoord(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return String.valueOf(v);
    }

    private static String formatCoordTruncated(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "0";
        double truncated = (long) (v * 10000.0) / 10000.0;
        if (truncated == Math.floor(truncated)) {
            return String.valueOf((long) truncated);
        }
        String s = String.format("%.4f", truncated);
        if (s.contains(".")) {
            s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        }
        return s;
    }

    private void fillFromPlayer() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        Vec3 pos = player.position();
        xPosInput.setValue(formatCoordTruncated(pos.x));
        yPosInput.setValue(formatCoordTruncated(pos.y));
        zPosInput.setValue(formatCoordTruncated(pos.z));
        dimensionInput.setValue(player.level.dimension().location().toString());
    }

    private DimensionalTeleportTarget buildTargetFromInputs() {
        double x = parseDouble(xPosInput.getValue());
        double y = parseDouble(yPosInput.getValue());
        double z = parseDouble(zPosInput.getValue());
        ResourceKey<Level> dim;
        if (dimensionInput.getValue().isEmpty()) {
            dim = Minecraft.getInstance().level.dimension();
        } else {
            ResourceLocation dimLoc = ResourceLocation.tryParse(dimensionInput.getValue());
            if (dimLoc == null) return null;
            dim = ResourceKey.create(Registries.DIMENSION, dimLoc);
        }
        String name = nameInput.getValue().isEmpty() ? dim.location().getPath() : nameInput.getValue();
        return new DimensionalTeleportTarget(new Vec3(x, y, z), dim, name);
    }

    private static double parseDouble(String s) {
        try {
            if (s == null || s.isEmpty() || s.equals("-") || s.equals(".") || s.equals("-.")) return 0.0;
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void addWaypoint() {
        DimensionalTeleportTarget t = buildTargetFromInputs();
        if (t == null) return;
        TrialMonolithNetwork.INSTANCE.sendToServer(new AddWaypointPacket(slot, t));
        targets.add(t);
        selected = targets.size() - 1;
        waypointList.refresh();
    }

    private void updateWaypoint() {
        if (selected < 0 || selected >= targets.size()) return;
        DimensionalTeleportTarget t = buildTargetFromInputs();
        if (t == null) return;
        TrialMonolithNetwork.INSTANCE.sendToServer(new UpdateWaypointPacket(slot, selected, t));
        targets.set(selected, t);
        waypointList.refresh();
    }

    private void deleteWaypoint() {
        if (selected < 0 || selected >= targets.size()) return;
        int toDelete = selected;
        TrialMonolithNetwork.INSTANCE.sendToServer(new DeleteWaypointPacket(slot, toDelete));
        targets.remove(toDelete);
        if (selected >= targets.size()) selected = Math.max(0, targets.size() - 1);
        waypointList.refresh();
        loadSelectedIntoInputs();
    }

    private void selectWaypoint(int index) {
        if (index < 0 || index >= targets.size()) return;
        if (selected == index) return;
        selected = index;
        TrialMonolithNetwork.INSTANCE.sendToServer(new SelectWaypointPacket(slot, index));
        loadSelectedIntoInputs();
    }

    private void teleportAndClose() {
        DimensionalTeleportTarget t = buildTargetFromInputs();
        if (t == null) return;
        TrialMonolithNetwork.INSTANCE.sendToServer(new TeleportWithTeleporterPacket(slot, t));
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics);
        if (waypointList != null) {
            waypointList.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private class WaypointList extends ObjectSelectionList<WaypointEntry> {
        public WaypointList(int x, int y, int width, int height) {
            super(Minecraft.getInstance(), width, DimensionalTeleporterScreen.this.height, y, y + height, 14);
            this.setLeftPos(x);
            this.setRenderBackground(false);
            this.setRenderTopAndBottom(false);
            refresh();
        }

        public void refresh() {
            this.clearEntries();
            for (int i = 0; i < targets.size(); i++) {
                WaypointEntry entry = new WaypointEntry(i);
                this.addEntry(entry);
                if (i == selected) this.setSelected(entry);
            }
        }

        @Override
        public int getRowWidth() {
            return this.width - 8;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getRight() - 6;
        }
    }

    private class WaypointEntry extends ObjectSelectionList.Entry<WaypointEntry> {
        private final int index;

        public WaypointEntry(int index) {
            this.index = index;
        }

        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            if (this.index < 0 || this.index >= targets.size()) return;
            DimensionalTeleportTarget t = targets.get(this.index);
            String label = t.name() + "  [" + t.dimension().location() + "]";
            guiGraphics.drawString(DimensionalTeleporterScreen.this.font, label, left + 2, top + 3, this.index == selected ? 0xFFFF55 : 0xFFFFFF);
        }

        @Override
        public @NotNull Component getNarration() {
            if (this.index < 0 || this.index >= targets.size()) return Component.empty();
            return Component.literal(targets.get(this.index).name());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                selectWaypoint(this.index);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}
