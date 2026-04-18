package io.github.kosianodangoo.trialmonolith.compat.curios;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Map;
import java.util.Optional;

class CuriosHandler {

    static ItemStack getStack(Player player, String identifier, int index) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inv -> inv.getStacksHandler(identifier))
                .map(h -> {
                    IItemHandlerModifiable stacks = h.getStacks();
                    if (index < 0 || index >= stacks.getSlots()) return ItemStack.EMPTY;
                    return stacks.getStackInSlot(index);
                })
                .orElse(ItemStack.EMPTY);
    }

    static Optional<CuriosCompat.SlotRef> findStackSlot(Player player, Item targetItem) {
        Optional<ICuriosItemHandler> optInv = CuriosApi.getCuriosInventory(player).resolve();
        if (optInv.isEmpty()) return Optional.empty();
        ICuriosItemHandler inv = optInv.get();
        for (Map.Entry<String, ICurioStacksHandler> e : inv.getCurios().entrySet()) {
            IItemHandlerModifiable stacks = e.getValue().getStacks();
            for (int i = 0; i < stacks.getSlots(); i++) {
                ItemStack s = stacks.getStackInSlot(i);
                if (!s.isEmpty() && s.getItem() == targetItem) {
                    return Optional.of(new CuriosCompat.SlotRef(e.getKey(), i, s));
                }
            }
        }
        return Optional.empty();
    }
}
