package io.github.kosianodangoo.trialmonolith.common.entity.trialmonolith.ai;

import io.github.kosianodangoo.trialmonolith.common.entity.SmallBeamEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.trialmonolith.TrialMonolithEntity;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class ShootRandomSmallBeamGoal extends Goal {
    public TrialMonolithEntity monolith;
    public LivingEntity target = null;
    public int attackTime = -1;
    public static final int SPAWN_COUNT = 3;

    public ShootRandomSmallBeamGoal(TrialMonolithEntity monolith) {
        super();
        this.monolith = monolith;
    }

    @Override
    public void stop() {
        this.target = null;
        this.attackTime = -1;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.monolith.getTarget();
        if (target != null && EntityHelper.getSoulDamage(target) < 10) {
            this.target = target;
            return true;
        } else return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (--attackTime == 0) {
            for (int i = 0; i < SPAWN_COUNT; i++) {
                SmallBeamEntity smallBeam = new SmallBeamEntity(TrialMonolithEntities.SMALL_BEAM.get(), this.monolith.level());
                smallBeam.setOwner(this.monolith);
                RandomSource randomSource = this.monolith.level().getRandom();
                smallBeam.setPos(monolith.getX() + randomSource.nextDouble() * 32 - 16,
                        monolith.getEyeY() + randomSource.nextDouble() * 32 - 16,
                        monolith.getZ() + randomSource.nextDouble() * 32 - 16);

                smallBeam.setXRot(randomSource.nextFloat() * 180 - 90);
                smallBeam.setYRot(randomSource.nextFloat() * 360);

                monolith.level().addFreshEntity(smallBeam);
            }
            attackTime = getCoolTime();
        }
        if (attackTime < 0) {
            attackTime = 20;
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public int getCoolTime() {
        return 2;
    }
}