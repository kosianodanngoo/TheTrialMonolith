package io.github.kosianodangoo.trialmonolith.common.entity.tesseractbeast.ai;

import io.github.kosianodangoo.trialmonolith.common.entity.tesseractbeast.TesseractBeastController;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;

public class RandomTPTargetGoal extends Goal {
    public TesseractBeastController controller;
    public int nextUsable = 105;
    public LivingEntity target = null;

    public RandomTPTargetGoal(TesseractBeastController controller) {
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
        Level level = controller.level;
        RandomSource randomSource = level.getRandom();
        nextUsable = controller.tickCount + 100 + randomSource.nextInt(50);
        double x = target.getX() + (randomSource.nextDouble() - 0.5D) * 16.0D;
        double y = Mth.clamp(target.getY() + (double) (randomSource.nextInt(16) - 8), level.getMinBuildHeight(), level.getMaxBuildHeight());
        double z = target.getZ() + (randomSource.nextDouble() - 0.5D) * 16.0D;
        Vec3 teleportPos = new Vec3(x, y, z);

        controller.level.gameEvent(GameEvent.TELEPORT, teleportPos, GameEvent.Context.of(target));
        EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(target, x, y, z);
        if (event.isCanceled()) {
            return;
        }
        if (target.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
            controller.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 2, 1);
        }
    }

    @Override
    public void tick() {
    }
}
