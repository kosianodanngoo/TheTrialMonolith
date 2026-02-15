package io.github.kosianodangoo.trialmonolith.common.entity.invadermonolith.ai;

import io.github.kosianodangoo.trialmonolith.common.entity.SmallBeamEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.invadermonolith.InvaderMonolithEntity;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class OPShootSmallBeamGoal extends Goal {
    public InvaderMonolithEntity monolith;

    public OPShootSmallBeamGoal(InvaderMonolithEntity monolith) {
        super();
        this.monolith = monolith;
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
            smallBeam.setPos(monolith.getX() + randomSource.nextDouble() * 8 - 4,
                    monolith.getEyeY() + randomSource.nextDouble() * 3,
                    monolith.getZ() + randomSource.nextDouble() * 8 - 4);

            float deflection = randomSource.nextFloat() * 15;

            Vec3 targetPos = target.getPosition(0)
                    .add(0, target.getBbHeight() / 2, 0)
                    .add(target.getDeltaMovement().multiply(deflection, deflection, deflection))
                    .add(randomSource.nextDouble() * 2 - 1, randomSource.nextDouble() * 2 - 1, randomSource.nextDouble() * 2 - 1);

            smallBeam.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
            monolith.level().addFreshEntity(smallBeam);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}