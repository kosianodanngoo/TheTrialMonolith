package io.github.kosianodangoo.trialmonolith.common.entity.tesseractbeast.ai;

import io.github.kosianodangoo.trialmonolith.common.entity.tesseractbeast.TesseractBeastController;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;

public class RotateGoal extends Goal {
    public TesseractBeastController controller;
    public int nextUsable = 43;
    public Entity target = null;

    public RotateGoal(TesseractBeastController controller) {
        this.controller = controller;
    }

    @Override
    public boolean canUse() {
        target = controller.target;
        return target != null && nextUsable <= controller.tickCount;
    }

    @Override
    public void stop() {
        target = null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        nextUsable = controller.tickCount + 100 + controller.level.random.nextInt(50);
        if (target instanceof ServerPlayer player) {
            player.connection.teleport(player.getX(), player.getY(), player.getZ(), controller.level.getRandom().nextFloat() * 360, player.getXRot());
        } else {
            target.turn(controller.level.getRandom().nextFloat() * 360, 0);
        }
        controller.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.CHICKEN_EGG, SoundSource.HOSTILE, 2, 1);
    }

    @Override
    public void tick() {
    }
}
