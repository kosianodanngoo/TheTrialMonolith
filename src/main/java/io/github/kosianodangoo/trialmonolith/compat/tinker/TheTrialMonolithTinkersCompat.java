package io.github.kosianodangoo.trialmonolith.compat.tinker;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TheTrialMonolithTinkersCompat {
    public static void register(FMLJavaModLoadingContext context) {
        TrialMonolithModifiers.register(context.getModEventBus());
    }

}