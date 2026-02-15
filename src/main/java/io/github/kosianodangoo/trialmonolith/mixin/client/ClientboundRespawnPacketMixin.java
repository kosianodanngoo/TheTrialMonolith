package io.github.kosianodangoo.trialmonolith.mixin.client;

import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundRespawnPacket.class)
public class ClientboundRespawnPacketMixin {
    @Inject(method = "handle(Lnet/minecraft/network/protocol/game/ClientGamePacketListener;)V", at = @At(value = "HEAD"))
    protected void handleBeforeMixin(ClientGamePacketListener p_132951_, CallbackInfo ci) {
        EntityHelper.setBypassProtection(Minecraft.getInstance().player, true);
    }

    @Inject(method = "handle(Lnet/minecraft/network/protocol/game/ClientGamePacketListener;)V", at = @At(value = "RETURN"))
    protected void handleAfterMixin(ClientGamePacketListener p_132951_, CallbackInfo ci) {
        EntityHelper.setBypassProtection(Minecraft.getInstance().player, false);
    }

}
