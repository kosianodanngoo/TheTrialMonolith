package io.github.kosianodangoo.trialmonolith.mixin.client;

import com.mojang.blaze3d.platform.WindowEventHandler;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
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

    public MinecraftMixin(String p_18765_) {
        super(p_18765_);
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void setScreenMixin(Screen pScreen, CallbackInfo ci) {
        if (pScreen instanceof DeathScreen && this.player != null && !this.player.isDeadOrDying() && EntityHelper.isSoulProtected(this.player)) {
            ci.cancel();
        }
    }
}
