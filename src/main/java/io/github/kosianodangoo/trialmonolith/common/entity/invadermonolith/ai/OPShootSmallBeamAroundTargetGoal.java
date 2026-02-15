package io.github.kosianodangoo.trialmonolith.common.entity.invadermonolith.ai;

import io.github.kosianodangoo.trialmonolith.common.entity.SmallBeamEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.invadermonolith.InvaderMonolithEntity;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class OPShootSmallBeamAroundTargetGoal extends Goal {
    public InvaderMonolithEntity monolith;

    public OPShootSmallBeamAroundTargetGoal(InvaderMonolithEntity monolith) {
        super();
        this.monolith = monolith;
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        for (Entity target : monolith.getTargets()) {
            SmallBeamEntity smallBeam = new SmallBeamEntity(TrialMonolithEntities.SMALL_BEAM.get(), this.monolith.level());
            smallBeam.setOwner(this.monolith);
            RandomSource randomSource = this.monolith.level().getRandom();

            AABB targetBB = target.getBoundingBox();

            smallBeam.setPos(Mth.lerp(randomSource.nextDouble(), targetBB.minX - 4, targetBB.maxX + 4),
                    Mth.lerp(randomSource.nextDouble(), targetBB.minY - 4, targetBB.maxY + 4),
                    Mth.lerp(randomSource.nextDouble(), targetBB.minZ - 4, targetBB.maxZ + 4));

            float deflection = randomSource.nextFloat() * 15;

            Vec3 targetPos = target.getPosition(0)
                    .add(0, target.getBbHeight() / 2, 0)
                    .add(target.getDeltaMovement().multiply(deflection, deflection, deflection));

            smallBeam.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
            monolith.level().addFreshEntity(smallBeam);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
