package io.github.kosianodangoo.trialmonolith.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface IController {
    Entity getProxyEntity();

    Vec3 getPosition();
}
