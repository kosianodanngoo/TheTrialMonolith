package io.github.kosianodangoo.trialmonolith;

import com.mojang.logging.LogUtils;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithCreativeTabs;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithEntities;
import io.github.kosianodangoo.trialmonolith.common.init.TrialMonolithItems;
import io.github.kosianodangoo.trialmonolith.compat.tinker.TheTrialMonolithTinkersCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@SuppressWarnings("removal")
@Mod(TheTrialMonolith.MOD_ID)
public class TheTrialMonolith {
    public static final String MOD_ID = "the_trial_monolith";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation getResourceLocation(String location) {
        return getResourceLocation(MOD_ID, location);
    }

    public static ResourceLocation getResourceLocation(String nameSpace, String location) {
        return new ResourceLocation(nameSpace, location);
    }

    public TheTrialMonolith() {
        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
        IEventBus modEventBus = context.getModEventBus();

        TrialMonolithEntities.register(modEventBus);
        TrialMonolithItems.register(modEventBus);
        TrialMonolithCreativeTabs.register(modEventBus);

        ModList modList = ModList.get();
        if (modList.isLoaded("tconstruct")) {
            TheTrialMonolithTinkersCompat.register(context);
        }

        context.registerConfig(ModConfig.Type.COMMON, TrialMonolithConfig.SPEC);
    }
}
