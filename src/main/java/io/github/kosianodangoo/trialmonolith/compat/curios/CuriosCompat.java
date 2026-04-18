package io.github.kosianodangoo.trialmonolith.compat.curios;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import java.util.Optional;

public class CuriosCompat {
    public static final String MOD_ID = "curios";

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MOD_ID);
    }

    public static ItemStack getStack(Player player, String identifier, int index) {
        if (!isLoaded()) return ItemStack.EMPTY;
        return CuriosHandler.getStack(player, identifier, index);
    }

    public static Optional<SlotRef> findStackSlot(Player player, Item targetItem) {
        if (!isLoaded()) return Optional.empty();
        return CuriosHandler.findStackSlot(player, targetItem);
    }

    public record SlotRef(String identifier, int index, ItemStack stack) {}
}
