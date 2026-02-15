package io.github.kosianodangoo.trialmonolith.common.entity.invadermonolith.ai;

import io.github.kosianodangoo.trialmonolith.common.entity.HugeBeamEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.invadermonolith.InvaderMonolithEntity;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class OPShootHugeBeamGoal extends Goal {
    public InvaderMonolithEntity monolith;
    public LivingEntity target = null;
    public int attackTime = -1;

    public OPShootHugeBeamGoal(InvaderMonolithEntity monolith) {
        super();
        this.monolith = monolith;
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (--attackTime == 0) {
            for (Entity target : monolith.getTargets()) {
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
            attackTime = getCoolTime();
        }
        if (attackTime < 0) {
            attackTime = getCoolTime();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return false;
    }

    public int getCoolTime() {
        return 200;
    }
}
