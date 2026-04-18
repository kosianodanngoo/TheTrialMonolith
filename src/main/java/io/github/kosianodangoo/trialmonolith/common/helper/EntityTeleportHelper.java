package io.github.kosianodangoo.trialmonolith.common.helper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class EntityTeleportHelper {
    public static boolean teleport(Entity entity, Vec3 target, ServerLevel level) {
        if (level == null) return false;
        boolean sameDimension = entity.level.dimension() == level.dimension();
        if (sameDimension) {
            return teleport(entity, target);
        }
        return entity.changeDimension(level, new TargetedTeleporter(target)) != null;
    }

    public static boolean teleport(Entity entity, Vec3 target) {
        entity.teleportTo(target.x, target.y, target.z);
        return true;
    }

    public static class TargetedTeleporter implements ITeleporter {
        public final Vec3 position;

        public TargetedTeleporter(Vec3 position) {
            this.position = position;
        }

        @Override
        public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
            return repositionEntity.apply(false);
        }

        @Override
        public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
            return new PortalInfo(position, Vec3.ZERO, entity.getYRot(), entity.getXRot());
        }
    }
}
