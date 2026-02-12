package io.github.kosianodangoo.trialmonolith.common.init;

import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import io.github.kosianodangoo.trialmonolith.common.entity.HugeBeamEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.SmallBeamEntity;
import io.github.kosianodangoo.trialmonolith.common.entity.TrialMonolithEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class TrialMonolithEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TheTrialMonolith.MOD_ID);

    public static final RegistryObject<EntityType<TrialMonolithEntity>> TRIAL_MONOLITH = register("trial_monolith", () ->
            EntityType.Builder.of(TrialMonolithEntity::new, MobCategory.MONSTER)
                    .sized(1.5F, 5.4F)
                    .clientTrackingRange(10)
                    .canSpawnFarFromPlayer()
    );

    public static final RegistryObject<EntityType<SmallBeamEntity>> SMALL_BEAM = register("small_beam", () ->
            EntityType.Builder.of(SmallBeamEntity::new, MobCategory.MISC)
                    .sized(1F, 1F)
                    .clientTrackingRange(10)
    );

    public static final RegistryObject<EntityType<HugeBeamEntity>> HUGE_BEAM = register("huge_beam", () ->
            EntityType.Builder.of(HugeBeamEntity::new, MobCategory.MISC)
                    .sized(1F, 1F)
                    .clientTrackingRange(10)
    );

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, Supplier<EntityType.Builder<T>> type) {
        return ENTITY_TYPES.register(name, () -> type.get().build(TheTrialMonolith.MOD_ID + ":" + name));
    }

    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }
}
