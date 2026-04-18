package io.github.kosianodangoo.trialmonolith.common.network.packet;

import io.github.kosianodangoo.trialmonolith.common.item.DimensionalTeleporterItem;
import io.github.kosianodangoo.trialmonolith.common.network.TeleporterSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SelectWaypointPacket {
    public final TeleporterSlot slot;
    public final int index;

    public SelectWaypointPacket(TeleporterSlot slot, int index) {
        this.slot = slot;
        this.index = index;
    }

    public static void encode(SelectWaypointPacket msg, FriendlyByteBuf buf) {
        msg.slot.encode(buf);
        buf.writeVarInt(msg.index);
    }

    public static SelectWaypointPacket decode(FriendlyByteBuf buf) {
        return new SelectWaypointPacket(TeleporterSlot.decode(buf), buf.readVarInt());
    }

    public static void handle(SelectWaypointPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player != null) {
            ItemStack stack = msg.slot.resolve(player);
            if (stack.getItem() instanceof DimensionalTeleporterItem item) {
                item.setSelected(stack, msg.index);
            }
        }
        ctx.get().setPacketHandled(true);
    }
}
