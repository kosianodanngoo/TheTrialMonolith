package io.github.kosianodangoo.trialmonolith.common.entity.ai;

import io.github.kosianodangoo.trialmonolith.common.entity.HugeBeamEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.TrialMonolithEntity;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class ShootHugeBeamGoal extends Goal {
    public TrialMonolithEntity monolith;
    public LivingEntity target = null;
    public int nextEnable = -1;

    public ShootHugeBeamGoal(TrialMonolithEntity monolith) {
        super();
        this.monolith = monolith;
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.monolith.getTarget();
        if (nextEnable == -1) {
            this.nextEnable = this.monolith.getMonolithActiveTime() + getCoolTime();
        }
        if (target != null && target.isAlive() && monolith.getMonolithActiveTime() > nextEnable) {
            this.nextEnable = this.monolith.getMonolithActiveTime() + getCoolTime();
            this.target = target;
            return true;
        } else return false;
    }

    @Override
    public void tick() {
        super.tick();
        HugeBeamEntity hugeBeam = new HugeBeamEntity(TrialMonolithEntities.HUGE_BEAM.get(), this.monolith.level());
        hugeBeam.setOwner(this.monolith);
        RandomSource randomSource = this.monolith.level().getRandom();

        hugeBeam.setPos(monolith.getX(),
                monolith.getEyeY() + 16,
                monolith.getZ());

        float deflection = randomSource.nextFloat() * 15;

        Vec3 targetPos = target.getPosition(0)
                .add(0, target.getBbHeight() / 2, 0)
                .add(target.getDeltaMovement().multiply(deflection, deflection, deflection));

        hugeBeam.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
        monolith.level().addFreshEntity(hugeBeam);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return false;
    }

    public int getCoolTime() {
        return 1200 + this.monolith.level().getRandom().nextInt(1200);
    }
}
