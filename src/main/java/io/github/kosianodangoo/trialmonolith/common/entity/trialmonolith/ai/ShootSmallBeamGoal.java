package io.github.kosianodangoo.trialmonolith.common.entity.trialmonolith.ai;

import io.github.kosianodangoo.trialmonolith.common.entity.SmallBeamEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.trialmonolith.TrialMonolithEntity;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class ShootSmallBeamGoal extends Goal {
    public TrialMonolithEntity monolith;
    public LivingEntity target = null;
    public int attackTime = -1;

    public ShootSmallBeamGoal(TrialMonolithEntity monolith) {
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
            EntityHelper.setSoulProtected(smallBeam, true);
            monolith.level().addFreshEntity(smallBeam);
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
        return 1;
    }
}