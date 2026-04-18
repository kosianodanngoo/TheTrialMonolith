package io.github.kosianodangoo.trialmonolith.common.network.packet;

import io.github.kosianodangoo.trialmonolith.common.item.DimensionalTeleporterItem;
import io.github.kosianodangoo.trialmonolith.common.item.DimensionalTeleporterItem.DimensionalTeleportTarget;
import io.github.kosianodangoo.trialmonolith.common.network.TeleporterSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AddWaypointPacket {
    public final TeleporterSlot slot;
    public final DimensionalTeleportTarget target;

    public AddWaypointPacket(TeleporterSlot slot, DimensionalTeleportTarget target) {
        this.slot = slot;
        this.target = target;
    }

    public static void encode(AddWaypointPacket msg, FriendlyByteBuf buf) {
        msg.slot.encode(buf);
        msg.target.encode(buf);
    }

    public static AddWaypointPacket decode(FriendlyByteBuf buf) {
        TeleporterSlot slot = TeleporterSlot.decode(buf);
        DimensionalTeleportTarget target = DimensionalTeleportTarget.decode(buf);
        return new AddWaypointPacket(slot, target);
    }

    public static void handle(AddWaypointPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player != null) {
            ItemStack stack = msg.slot.resolve(player);
            if (stack.getItem() instanceof DimensionalTeleporterItem item) {
                item.addTarget(stack, msg.target);
            }
        }
        ctx.get().setPacketHandled(true);
    }
}
