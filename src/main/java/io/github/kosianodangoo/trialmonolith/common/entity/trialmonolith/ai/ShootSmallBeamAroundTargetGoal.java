package io.github.kosianodangoo.trialmonolith.common.entity.trialmonolith.ai;

import io.github.kosianodangoo.trialmonolith.common.entity.SmallBeamEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.trialmonolith.TrialMonolithEntity;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShootSmallBeamAroundTargetGoal extends Goal {
    public TrialMonolithEntity monolith;
    public LivingEntity target = null;
    public int attackTime = -1;
    public int attackCount = 0;
    public int nextEnable = -1;
    public static final int ATTACK_LIMIT = 60;

    public ShootSmallBeamAroundTargetGoal(TrialMonolithEntity monolith) {
        super();
        this.monolith = monolith;
    }

    @Override
    public void stop() {
        this.target = null;
        this.attackTime = -1;
        this.attackCount = 0;
        this.nextEnable = this.monolith.getMonolithActiveTime() + getInterval();
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.monolith.getTarget();
        if (nextEnable == -1) {
            this.nextEnable = this.monolith.getMonolithActiveTime() + getInterval();
        }
        if (target != null && EntityHelper.getSoulDamage(target) < 10 && attackCount < ATTACK_LIMIT && monolith.getMonolithActiveTime() > nextEnable) {
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

            AABB targetBB = target.getBoundingBox();

            smallBeam.setPos(Mth.lerp(randomSource.nextDouble(), targetBB.minX - 4, targetBB.maxX + 4),
                    Mth.lerp(randomSource.nextDouble(), targetBB.minY - 4, targetBB.maxY + 4),
                    Mth.lerp(randomSource.nextDouble(), targetBB.minZ - 4, targetBB.maxZ + 4));

            float deflection = randomSource.nextFloat() * 15;

            Vec3 targetPos = target.getPosition(0)
                    .add(0, target.getBbHeight() / 2, 0)
                    .add(target.getDeltaMovement().multiply(deflection, deflection, deflection));

            smallBeam.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
            EntityHelper.setSoulProtected(smallBeam, true);
            monolith.level().addFreshEntity(smallBeam);

            attackCount++;

            attackTime = getCoolTime();
        }
        if (attackTime < 0) {
            target.level().playSound(null, target.blockPosition(), SoundEvents.PORTAL_TRAVEL, SoundSource.HOSTILE, 1, 3);
            attackTime = 10;
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public int getCoolTime() {
        return 1;
    }

    public int getInterval() {
        return getCoolTime() * ATTACK_LIMIT * 4 + this.monolith.level().getRandom().nextInt(20);
    }
}
