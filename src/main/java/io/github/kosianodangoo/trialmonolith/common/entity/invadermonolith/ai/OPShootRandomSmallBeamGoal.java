package io.github.kosianodangoo.trialmonolith.common.entity.invadermonolith.ai;

import io.github.kosianodangoo.trialmonolith.common.entity.SmallBeamEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.invadermonolith.InvaderMonolithEntity;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;

public class OPShootRandomSmallBeamGoal extends Goal {
    public InvaderMonolithEntity monolith;
    public static final int SPAWN_COUNT = 2;

    public OPShootRandomSmallBeamGoal(InvaderMonolithEntity monolith) {
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
        for (int i = 0; i < SPAWN_COUNT; i++) {
            SmallBeamEntity smallBeam = new SmallBeamEntity(TrialMonolithEntities.SMALL_BEAM.get(), this.monolith.level());
            smallBeam.setOwner(this.monolith);
            RandomSource randomSource = this.monolith.level().getRandom();
            smallBeam.setPos(monolith.getX() + randomSource.nextDouble() * 32 - 16,
                    monolith.getEyeY() + randomSource.nextDouble() * 32 - 16,
                    monolith.getZ() + randomSource.nextDouble() * 32 - 16);

            smallBeam.setXRot(randomSource.nextFloat() * 180 - 90);
            smallBeam.setYRot(randomSource.nextFloat() * 360);

            EntityHelper.setSoulProtected(smallBeam, true);
            monolith.level().addFreshEntity(smallBeam);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}