package io.github.kosianodangoo.trialmonolith.common.network.packet;

import io.github.kosianodangoo.trialmonolith.common.item.DimensionalTeleporterItem;
import io.github.kosianodangoo.trialmonolith.common.item.DimensionalTeleporterItem.DimensionalTeleportTarget;
import io.github.kosianodangoo.trialmonolith.common.network.TeleporterSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateWaypointPacket {
    public final TeleporterSlot slot;
    public final int index;
    public final DimensionalTeleportTarget target;

    public UpdateWaypointPacket(TeleporterSlot slot, int index, DimensionalTeleportTarget target) {
        this.slot = slot;
        this.index = index;
        this.target = target;
    }

    public static void encode(UpdateWaypointPacket msg, FriendlyByteBuf buf) {
        msg.slot.encode(buf);
        buf.writeVarInt(msg.index);
        msg.target.encode(buf);
    }

    public static UpdateWaypointPacket decode(FriendlyByteBuf buf) {
        TeleporterSlot slot = TeleporterSlot.decode(buf);
        int index = buf.readVarInt();
        DimensionalTeleportTarget target = DimensionalTeleportTarget.decode(buf);
        return new UpdateWaypointPacket(slot, index, target);
    }

    public static void handle(UpdateWaypointPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player != null) {
            ItemStack stack = msg.slot.resolve(player);
            if (stack.getItem() instanceof DimensionalTeleporterItem item) {
                item.updateTarget(stack, msg.index, msg.target);
            }
        }
        ctx.get().setPacketHandled(true);
    }
}
