package io.github.kosianodangoo.trialmonolith.compat.tinker;

import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import net.minecraftforge.eventbus.api.IEventBus;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

public class TrialMonolithModifiers {
    public static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(TheTrialMonolith.MOD_ID);

    public static final StaticModifier<Modifier> SOUL_SLAYER_MODIFIER = MODIFIERS.register("soul_slayer", SoulSlayerModifier::new);

    public static void register(IEventBus modEventBus) {
        MODIFIERS.register(modEventBus);
    }
}
