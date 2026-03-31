package io.github.kosianodangoo.trialmonolith.common.entity.tesseractbeast.ai;

import io.github.kosianodangoo.trialmonolith.common.entity.SmallBeamEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.tesseractbeast.TesseractBeastController;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class SurroundingSmallBeamGoal extends Goal {
    public TesseractBeastController controller;
    public int tick = 0;
    public int nextUsable = 264;
    public Entity target = null;

    public SurroundingSmallBeamGoal(TesseractBeastController controller) {
        this.controller = controller;
    }

    @Override
    public boolean canUse() {
        target = controller.target;
        return target != null && controller.attackCooldown <= 0 && nextUsable <= controller.tickCount;
    }

    @Override
    public boolean canContinueToUse() {
        return tick < 120;
    }

    @Override
    public void stop() {
        tick = 0;
        target = null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        controller.attackCooldown = 150 + controller.level.random.nextInt(10);
        nextUsable = controller.tickCount + controller.attackCooldown * 3;
        controller.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PORTAL_TRIGGER, SoundSource.HOSTILE, 1, 4);
    }

    @Override
    public void tick() {
        if (++tick >= 10) {
            RandomSource randomSource = controller.level.getRandom();

            Vec3 direction = Vec3.directionFromRotation(randomSource.nextFloat() * 180 - 90, randomSource.nextFloat() * 360);

            Vec3 targetPosition = target.getPosition(0);
            Vec3 position = targetPosition
                    .add(direction.scale(16))
                    .add(0, target.getBbHeight() / 2, 0);

            SmallBeamEntity smallBeam = new SmallBeamEntity(TrialMonolithEntities.SMALL_BEAM.get(), controller.level);
            smallBeam.setPos(position);
            smallBeam.setHighDimensional(true);
            smallBeam.setOwnerController(controller);
            smallBeam.lookAt(EntityAnchorArgument.Anchor.EYES, targetPosition);
            EntityHelper.setSoulProtected(smallBeam, true);
            controller.level.addFreshEntity(smallBeam);

            controller.position = position.add(direction.scale(22));
            controller.teleport = true;
        }
    }
}
