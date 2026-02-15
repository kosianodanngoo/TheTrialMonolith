package io.github.kosianodangoo.trialmonolith.common.entity.trialmonolith.ai;

import io.github.kosianodangoo.trialmonolith.common.entity.DamageCubeEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.trialmonolith.TrialMonolithEntity;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class SummonDamageCubeGoal extends Goal {
    public TrialMonolithEntity monolith;
    public LivingEntity target = null;
    public int attackTime = -1;
    public int attackCount = 0;
    public int nextEnable = -1;
    public static final int ATTACK_LIMIT = 50;
    public static final int SPAWN_COUNT = 7;

    public SummonDamageCubeGoal(TrialMonolithEntity monolith) {
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
        if (target != null && target.isAlive() && attackCount < ATTACK_LIMIT && monolith.getMonolithActiveTime() > nextEnable) {
            this.target = target;
            return true;
        } else return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (--attackTime == 0) {
            for (int i = 0; i < SPAWN_COUNT; i++) {
                DamageCubeEntity damageCube = new DamageCubeEntity(TrialMonolithEntities.DAMAGE_CUBE.get(), this.monolith.level());
                damageCube.setOwner(this.monolith);
                RandomSource randomSource = this.monolith.level().getRandom();

                float deflection = randomSource.nextFloat() * 15;

                Vec3 targetPos = target.getPosition(0)
                        .add(0, target.getBbHeight() / 2, 0)
                        .add(target.getDeltaMovement().multiply(deflection, deflection, deflection))
                        .add(randomSource.nextDouble() * 32 - 16, randomSource.nextDouble() * 32 - 16, randomSource.nextDouble() * 32 - 16);

                damageCube.setPos(targetPos);

                monolith.level().addFreshEntity(damageCube);
            }
            DamageCubeEntity damageCube = new DamageCubeEntity(TrialMonolithEntities.DAMAGE_CUBE.get(), this.monolith.level());
            damageCube.setOwner(monolith);
            damageCube.setPos(target.getPosition(0));
            monolith.level().addFreshEntity(damageCube);

            attackCount++;

            attackTime = getCoolTime();
        }
        if (attackTime < 0) {
            target.level().playSound(null, target.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1, 3);
            attackTime = 10;
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public int getCoolTime() {
        return 2;
    }

    public int getInterval() {
        return getCoolTime() * ATTACK_LIMIT * 6 + this.monolith.level().getRandom().nextInt(40);
    }
}
