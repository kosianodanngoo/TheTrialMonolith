package io.github.kosianodangoo.trialmonolith.mixin.client;

import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientGamePacketListenerMixin {
    @Shadow private ClientLevel level;

    @Inject(method = "handleRespawn", at = @At(value = "HEAD"))
    public void handleRespawnBeforeMixin(CallbackInfo ci) {
        EntityHelper.setBypassProtection(Minecraft.getInstance().player, true);
    }

    @Inject(method = "handleRespawn", at = @At(value = "RETURN"))
    public void handleRespawnAfterMixin(CallbackInfo ci) {
        EntityHelper.setBypassProtection(Minecraft.getInstance().player, false);
    }

    @Inject(method = "handleRemoveEntities", at = @At(value = "HEAD"))
    public void handleRemoveBeforeMixin(ClientboundRemoveEntitiesPacket packet, CallbackInfo ci) {
        packet.getEntityIds().forEach((id) ->
            EntityHelper.setBypassProtection(level.getEntity(id), true)
        );
    }

    @Inject(method = "handleRemoveEntities", at = @At(value = "RETURN"))
    public void handleRemoveAfterMixin(ClientboundRemoveEntitiesPacket packet, CallbackInfo ci) {
        packet.getEntityIds().forEach((id) ->
                EntityHelper.setBypassProtection(level.getEntity(id), false)
        );
    }

}
