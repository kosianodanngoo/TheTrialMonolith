package io.github.kosianodangoo.trialmonolith.common.network.packet;

import io.github.kosianodangoo.trialmonolith.common.helper.EntityTeleportHelper;
import io.github.kosianodangoo.trialmonolith.common.item.DimensionalTeleporterItem;
import io.github.kosianodangoo.trialmonolith.common.item.DimensionalTeleporterItem.DimensionalTeleportTarget;
import io.github.kosianodangoo.trialmonolith.common.network.TeleporterSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TeleportWithTeleporterPacket {
    public final TeleporterSlot slot;
    public final DimensionalTeleportTarget target;

    public TeleportWithTeleporterPacket(TeleporterSlot slot, DimensionalTeleportTarget target) {
        this.slot = slot;
        this.target = target;
    }

    public static void encode(TeleportWithTeleporterPacket msg, FriendlyByteBuf buf) {
        msg.slot.encode(buf);
        msg.target.encode(buf);
    }

    public static TeleportWithTeleporterPacket decode(FriendlyByteBuf buf) {
        TeleporterSlot slot = TeleporterSlot.decode(buf);
        DimensionalTeleportTarget target = DimensionalTeleportTarget.decode(buf);
        return new TeleportWithTeleporterPacket(slot, target);
    }

    public static void handle(TeleportWithTeleporterPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player != null) {
            ItemStack stack = msg.slot.resolve(player);
            if (stack.getItem() instanceof DimensionalTeleporterItem) {
                MinecraftServer server = player.getServer();
                if (server != null) {
                    EntityTeleportHelper.teleport(player, msg.target.position(), server.getLevel(msg.target.dimension()));
                }
            }
        }
        ctx.get().setPacketHandled(true);
    }
}
