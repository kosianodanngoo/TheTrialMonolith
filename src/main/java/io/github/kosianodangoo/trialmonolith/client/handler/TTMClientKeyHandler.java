package io.github.kosianodangoo.trialmonolith.client.handler;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import io.github.kosianodangoo.trialmonolith.client.screen.DimensionalTeleporterScreen;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithItems;
import io.github.kosianodangoo.trialmonolith.common.item.DimensionalTeleporterItem;
import io.github.kosianodangoo.trialmonolith.common.network.TeleporterSlot;
import io.github.kosianodangoo.trialmonolith.compat.curios.CuriosCompat;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TheTrialMonolith.MOD_ID, value = Dist.CLIENT)
public class TTMClientKeyHandler {
    public static final String CATEGORY = "key.categories.the_trial_monolith";
    public static final String KEY_OPEN_TELEPORTER = "key.the_trial_monolith.open_dimensional_teleporter";

    public static final KeyMapping OPEN_TELEPORTER = new KeyMapping(
            KEY_OPEN_TELEPORTER,
            KeyConflictContext.IN_GAME,
            InputConstants.UNKNOWN,
            CATEGORY
    );

    @Mod.EventBusSubscriber(modid = TheTrialMonolith.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(OPEN_TELEPORTER);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.screen != null) return;
        while (OPEN_TELEPORTER.consumeClick()) {
            tryOpenTeleporter(player);
        }
    }

    private static void tryOpenTeleporter(LocalPlayer player) {
        DimensionalTeleporterItem target = TrialMonolithItems.DIMENSIONAL_TELEPORTER.get();
        net.minecraft.world.entity.player.Inventory inv = player.getInventory();
        int selectedIdx = inv.selected;

        ItemStack mainStack = inv.getItem(selectedIdx);
        if (mainStack.getItem() == target) {
            openScreen(mainStack, new TeleporterSlot.Hand(InteractionHand.MAIN_HAND));
            return;
        }
        ItemStack offStack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (offStack.getItem() == target) {
            openScreen(offStack, new TeleporterSlot.Hand(InteractionHand.OFF_HAND));
            return;
        }
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (i == selectedIdx || i == net.minecraft.world.entity.player.Inventory.SLOT_OFFHAND) continue;
            ItemStack s = inv.getItem(i);
            if (s.getItem() == target) {
                openScreen(s, new TeleporterSlot.Inventory(i));
                return;
            }
        }
        CuriosCompat.findStackSlot(player, target).ifPresent(ref ->
                openScreen(ref.stack(), new TeleporterSlot.Curios(ref.identifier(), ref.index()))
        );
    }

    private static void openScreen(ItemStack stack, TeleporterSlot slot) {
        Minecraft.getInstance().setScreen(new DimensionalTeleporterScreen(stack, slot));
    }
}
