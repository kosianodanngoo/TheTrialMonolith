package io.github.kosianodangoo.trialmonolith.common.entity.tesseractbeast.ai;

import io.github.kosianodangoo.trialmonolith.common.entity.AreaAttackerEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.tesseractbeast.TesseractBeastController;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;

public class AreaAttackGoal extends Goal {
    public TesseractBeastController controller;
    public int tick = 0;
    public int nextUsable = 432;
    public Entity target = null;
    public AreaAttackerEntity areaAttacker = null;

    public AreaAttackGoal(TesseractBeastController controller) {
        this.controller = controller;
    }

    @Override
    public boolean canUse() {
        target = controller.target;
        return target != null && controller.attackCooldown <= 0 && nextUsable <= controller.tickCount;
    }

    @Override
    public boolean canContinueToUse() {
        return tick < 200;
    }

    @Override
    public void stop() {
        tick = 0;
        target = null;
        areaAttacker.setPastTicks(areaAttacker.getLifeTime() - 20);
        areaAttacker = null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        controller.attackCooldown = 300 + controller.level.random.nextInt(10);
        nextUsable = controller.tickCount + controller.attackCooldown * 3;
        createAreaAttacker();
    }

    @Override
    public void tick() {
        ++tick;
        if (areaAttacker.isRemoved()) {
            createAreaAttacker();
            areaAttacker.setPastTicks(tick);
        }
    }

    public void createAreaAttacker() {
        areaAttacker = new AreaAttackerEntity(TrialMonolithEntities.AREA_ATTACKER.get(), controller.level);
        areaAttacker.setOwnerController(controller);
        areaAttacker.setPos(controller.position);
        areaAttacker.setHighDimensional(true);
        EntityHelper.setSoulProtected(areaAttacker, true);
        controller.level.addFreshEntity(areaAttacker);
    }
}
