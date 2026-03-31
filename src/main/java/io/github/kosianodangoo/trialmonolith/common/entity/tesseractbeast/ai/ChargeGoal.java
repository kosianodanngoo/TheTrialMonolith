package io.github.kosianodangoo.trialmonolith.common.entity.tesseractbeast.ai;

import io.github.kosianodangoo.trialmonolith.common.entity.tesseractbeast.TesseractBeastController;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class ChargeGoal extends Goal {
    public TesseractBeastController controller;
    public Vec3 deltaMovement = null;
    public int chargeTick = 0;
    public int chargeCount = 0;
    public Entity target = null;

    public ChargeGoal(TesseractBeastController controller) {
        this.controller = controller;
    }

    @Override
    public boolean canUse() {
        target = controller.target;
        return target != null && controller.attackCooldown <= 0;
    }

    @Override
    public boolean canContinueToUse() {
        return chargeCount < 3;
    }

    @Override
    public void stop() {
        deltaMovement = null;
        chargeTick = 0;
        chargeCount = 0;
        target = null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        controller.attackCooldown = 150 + controller.level.random.nextInt(10);
    }

    @Override
    public void tick() {
        if (++chargeTick <= 10) {
            if (deltaMovement == null) {
                deltaMovement = target.getPosition(0).subtract(controller.position).normalize().scale(2);
            }
            controller.position = controller.position.add(deltaMovement.scale(-0.5));
        } else if (chargeTick <= 40) {
            controller.position = controller.position.add(deltaMovement);
        } else {
            chargeCount++;
            chargeTick = 0;
            deltaMovement = null;
        }
    }
}
