package io.github.kosianodangoo.trialmonolith.mixin.client;

import com.mojang.blaze3d.platform.WindowEventHandler;
import io.github.kosianodangoo.trialmonolith.api.mixin.ITickTracker;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraftforge.client.extensions.IForgeMinecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler, IForgeMinecraft {
    @Shadow @Nullable public LocalPlayer player;

    @Shadow
    @Nullable
    public ClientLevel level;

    @Shadow
    private volatile boolean pause;

    public MinecraftMixin(String p_18765_) {
        super(p_18765_);
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void setScreenMixin(Screen pScreen, CallbackInfo ci) {
        if (pScreen instanceof DeathScreen && this.player != null && !this.player.isDeadOrDying() && EntityHelper.isSoulProtected(this.player)) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickMixin(CallbackInfo ci) {
        if (level != null && !pause) {
            level.tickingEntities.forEach(entity -> {
                if (entity instanceof ITickTracker tickTracker) {
                    tickTracker.the_trial_monolith$updateLastTickCount();
                }
            });
        }
    }
}
