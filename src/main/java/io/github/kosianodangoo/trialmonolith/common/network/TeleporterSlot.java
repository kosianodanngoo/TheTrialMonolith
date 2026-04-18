package io.github.kosianodangoo.trialmonolith.common.network;

import io.github.kosianodangoo.trialmonolith.compat.curios.CuriosCompat;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public sealed interface TeleporterSlot permits TeleporterSlot.Hand, TeleporterSlot.Inventory, TeleporterSlot.Curios {
    ItemStack resolve(Player player);

    void encode(FriendlyByteBuf buf);

    static TeleporterSlot decode(FriendlyByteBuf buf) {
        byte tag = buf.readByte();
        return switch (tag) {
            case 0 -> new Hand(buf.readEnum(InteractionHand.class));
            case 1 -> new Curios(buf.readUtf(), buf.readVarInt());
            case 2 -> new Inventory(buf.readVarInt());
            default -> new Hand(InteractionHand.MAIN_HAND);
        };
    }

    record Hand(InteractionHand hand) implements TeleporterSlot {
        @Override
        public ItemStack resolve(Player player) {
            return player.getItemInHand(this.hand);
        }

        @Override
        public void encode(FriendlyByteBuf buf) {
            buf.writeByte(0);
            buf.writeEnum(this.hand);
        }
    }

    record Curios(String identifier, int index) implements TeleporterSlot {
        @Override
        public ItemStack resolve(Player player) {
            return CuriosCompat.getStack(player, this.identifier, this.index);
        }

        @Override
        public void encode(FriendlyByteBuf buf) {
            buf.writeByte(1);
            buf.writeUtf(this.identifier);
            buf.writeVarInt(this.index);
        }
    }

    record Inventory(int index) implements TeleporterSlot {
        @Override
        public ItemStack resolve(Player player) {
            net.minecraft.world.entity.player.Inventory inv = player.getInventory();
            if (this.index < 0 || this.index >= inv.getContainerSize()) return ItemStack.EMPTY;
            return inv.getItem(this.index);
        }

        @Override
        public void encode(FriendlyByteBuf buf) {
            buf.writeByte(2);
            buf.writeVarInt(this.index);
        }
    }
}
