package io.github.kosianodangoo.trialmonolith.common.network.packet;

import io.github.kosianodangoo.trialmonolith.common.item.DimensionalTeleporterItem;
import io.github.kosianodangoo.trialmonolith.common.network.TeleporterSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DeleteWaypointPacket {
    public final TeleporterSlot slot;
    public final int index;

    public DeleteWaypointPacket(TeleporterSlot slot, int index) {
        this.slot = slot;
        this.index = index;
    }

    public static void encode(DeleteWaypointPacket msg, FriendlyByteBuf buf) {
        msg.slot.encode(buf);
        buf.writeVarInt(msg.index);
    }

    public static DeleteWaypointPacket decode(FriendlyByteBuf buf) {
        return new DeleteWaypointPacket(TeleporterSlot.decode(buf), buf.readVarInt());
    }

    public static void handle(DeleteWaypointPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player != null) {
            ItemStack stack = msg.slot.resolve(player);
            if (stack.getItem() instanceof DimensionalTeleporterItem item) {
                item.deleteTarget(stack, msg.index);
            }
        }
        ctx.get().setPacketHandled(true);
    }
}
