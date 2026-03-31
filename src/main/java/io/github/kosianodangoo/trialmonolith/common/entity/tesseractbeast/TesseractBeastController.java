package io.github.kosianodangoo.trialmonolith.common.entity.tesseractbeast;

import io.github.kosianodangoo.trialmonolith.api.IController;
import io.github.kosianodangoo.trialmonolith.common.entity.tesseractbeast.ai.*;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithDamageTypes;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithItems;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TesseractBeastController implements IController {
    public Level level;
    public TesseractBeastProxyEntity proxyEntity;
    public float soulDamage = 0;
    public Vec3 position;
    public ServerBossEvent bossEvent;
    public int tickCount = 0;
    public int deathTime = 0;
    public int attackCooldown = 0;
    public LivingEntity target;
    public boolean removed = false;
    public boolean teleport = false;

    public GoalSelector goalSelector;

    public TesseractBeastController(Level level, TesseractBeastProxyEntity proxyEntity) {
        this.level = level;
        this.proxyEntity = proxyEntity;
        this.position = proxyEntity.position;
        this.bossEvent = new ServerBossEvent(proxyEntity.getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_20);
        this.goalSelector = new GoalSelector(level.getProfilerSupplier());

        this.goalSelector.addGoal(3, new AreaAttackGoal(this));
        this.goalSelector.addGoal(5, new SurroundingSmallBeamGoal(this));
        this.goalSelector.addGoal(7, new RotateGoal(this));
        this.goalSelector.addGoal(8, new RandomTPTargetGoal(this));
        this.goalSelector.addGoal(10, new ChargeGoal(this));
    }

    public void tick() {
        ++tickCount;
        if (level instanceof ServerLevel serverLevel) {
            if (soulDamage >= 1) {
                if (++deathTime >= 50) {
                    ItemEntity dimensionalCore = new ItemEntity(level, position.x, position.y, position.z, TrialMonolithItems.DIMENSIONAL_CORE.get().getDefaultInstance());
                    dimensionalCore.setNoGravity(true);
                    dimensionalCore.setGlowingTag(true);
                    level.addFreshEntity(dimensionalCore);
                    remove();
                }
                return;
            }
            boolean isNull = proxyEntity == null;
            if (isNull || proxyEntity.isRemoved() || (tickCount >= 20 && serverLevel.getEntity(proxyEntity.id) == null)) {
                if (!isNull) {
                    EntityHelper.setBypassProtection(proxyEntity, true);
                    proxyEntity.discard();
                }
                proxyEntity = new TesseractBeastProxyEntity(TrialMonolithEntities.TESSERACT_BEAST.get(), level, this);
                proxyEntity.setPos(position);
                serverLevel.addFreshEntity(proxyEntity);
            }
            if (!proxyEntity.initialized) {
                return;
            }
            proxyEntity.tickCount = tickCount;
            if ((target == null || target.isRemoved() || target.isDeadOrDying()) && (tickCount & 7) == 0) {
                target = level.getNearestPlayer(position.x, position.y, position.z, 64, false);
            }
            if ((tickCount & 1) != 0) {
                this.goalSelector.tickRunningGoals(false);
            } else {
                this.goalSelector.tick();
            }
            attackCooldown--;

            if (teleport) {
                proxyEntity.teleportToWithTicket(position.x, position.y, position.z);
                teleport = false;
            } else {
                proxyEntity.setPos(position);
            }
            bossEvent.setProgress(1 - soulDamage);

            AABB boundingBox = proxyEntity.getBoundingBox();
            EntityHelper.getEntities(level, proxyEntity.getBoundingBox().inflate(32), (entity -> !entity.equals(proxyEntity) && (entity.getBoundingBox().intersects(boundingBox) || (boundingBox.contains(entity.getPosition(0)))))).forEach((entity -> {
                entity.hurt(TrialMonolithDamageTypes.dimensionalAttack(level, proxyEntity), 100);

                if (EntityHelper.isSoulProtected(entity)) {
                    EntityHelper.addSoulDamageForce(entity, 0.02f);
                } else {
                    EntityHelper.addSoulDamage(entity, 0.1f);
                }
            }));
        }
    }

    public void remove() {
        removed = true;
        if (proxyEntity != null) {
            EntityHelper.setBypassProtection(proxyEntity, true);
            proxyEntity.discard();
        }
        TesseractBeastHandler.getTesseractBeastHandler(level).toRemove.add(this);
    }

    @Override
    public Entity getProxyEntity() {
        return proxyEntity;
    }

    @Override
    public Vec3 getPosition() {
        return position;
    }
}
