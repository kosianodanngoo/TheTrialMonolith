package io.github.kosianodangoo.trialmonolith.common.entity.invadermonolith.ai;

import io.github.kosianodangoo.trialmonolith.common.entity.DamageCubeEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.invadermonolith.InvaderMonolithEntity;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class OPSummonDamageCubeGoal extends Goal {
    public InvaderMonolithEntity monolith;

    public OPSummonDamageCubeGoal(InvaderMonolithEntity monolith) {
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
        for (Entity entity : monolith.getTargets()) {
            for (int i = 0; i < 2; i++) {
                DamageCubeEntity damageCube = new DamageCubeEntity(TrialMonolithEntities.DAMAGE_CUBE.get(), this.monolith.level());
                damageCube.setOwner(this.monolith);
                RandomSource randomSource = this.monolith.level().getRandom();

                float deflection = randomSource.nextFloat() * 15;

                Vec3 targetPos = entity.getPosition(0)
                        .add(0, entity.getBbHeight() / 2, 0)
                        .add(entity.getDeltaMovement().multiply(deflection, deflection, deflection))
                        .add(randomSource.nextDouble() * 32 - 16, randomSource.nextDouble() * 32 - 16, randomSource.nextDouble() * 32 - 16);

                damageCube.setPos(targetPos);

                monolith.level().addFreshEntity(damageCube);
            }
            DamageCubeEntity damageCube = new DamageCubeEntity(TrialMonolithEntities.DAMAGE_CUBE.get(), this.monolith.level());
            damageCube.setOwner(monolith);
            damageCube.setPos(entity.getPosition(0));
            monolith.level().addFreshEntity(damageCube);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
