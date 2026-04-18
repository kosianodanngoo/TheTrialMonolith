package io.github.kosianodangoo.trialmonolith.common.network;

import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import io.github.kosianodangoo.trialmonolith.common.network.packet.AddWaypointPacket;
import io.github.kosianodangoo.trialmonolith.common.network.packet.DeleteWaypointPacket;
import io.github.kosianodangoo.trialmonolith.common.network.packet.SelectWaypointPacket;
import io.github.kosianodangoo.trialmonolith.common.network.packet.TeleportWithTeleporterPacket;
import io.github.kosianodangoo.trialmonolith.common.network.packet.UpdateWaypointPacket;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class TrialMonolithNetwork {
    public static final String VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(TheTrialMonolith.getResourceLocation("main"))
            .clientAcceptedVersions(VERSION::equals)
            .serverAcceptedVersions(VERSION::equals)
            .networkProtocolVersion(() -> VERSION)
            .simpleChannel();

    private static int id = 0;

    public static void register() {
        INSTANCE.messageBuilder(AddWaypointPacket.class, id++)
                .encoder(AddWaypointPacket::encode)
                .decoder(AddWaypointPacket::decode)
                .consumerMainThread(AddWaypointPacket::handle)
                .add();

        INSTANCE.messageBuilder(UpdateWaypointPacket.class, id++)
                .encoder(UpdateWaypointPacket::encode)
                .decoder(UpdateWaypointPacket::decode)
                .consumerMainThread(UpdateWaypointPacket::handle)
                .add();

        INSTANCE.messageBuilder(DeleteWaypointPacket.class, id++)
                .encoder(DeleteWaypointPacket::encode)
                .decoder(DeleteWaypointPacket::decode)
                .consumerMainThread(DeleteWaypointPacket::handle)
                .add();

        INSTANCE.messageBuilder(SelectWaypointPacket.class, id++)
                .encoder(SelectWaypointPacket::encode)
                .decoder(SelectWaypointPacket::decode)
                .consumerMainThread(SelectWaypointPacket::handle)
                .add();

        INSTANCE.messageBuilder(TeleportWithTeleporterPacket.class, id++)
                .encoder(TeleportWithTeleporterPacket::encode)
                .decoder(TeleportWithTeleporterPacket::decode)
                .consumerMainThread(TeleportWithTeleporterPacket::handle)
                .add();
    }
}
