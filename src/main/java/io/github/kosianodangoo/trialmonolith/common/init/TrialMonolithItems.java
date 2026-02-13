package io.github.kosianodangoo.trialmonolith.common.init;

import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import io.github.kosianodangoo.trialmonolith.common.item.BeamSummonerItem;
import io.github.kosianodangoo.trialmonolith.common.item.DamageCubeSummonerItem;
import io.github.kosianodangoo.trialmonolith.common.item.SoulProtectorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class TrialMonolithItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TheTrialMonolith.MOD_ID);

    public static final RegistryObject<Item> MONOLITH_FRAGMENT = register("monolith_fragment", () -> new Item(new Item.Properties()), true);
    public static final RegistryObject<Item> MONOLITH_SPAWN_EGG = register("trial_monolith_spawn_egg", () -> new ForgeSpawnEggItem(TrialMonolithEntities.TRIAL_MONOLITH, 0x000000, 0x440044, new Item.Properties()), true);
    public static final RegistryObject<BeamSummonerItem> BEAM_SUMMONER = register("beam_summoner", BeamSummonerItem::new, true);
    public static final RegistryObject<DamageCubeSummonerItem> DAMAGE_CUBE_SUMMONER = register("damage_cube_summoner", DamageCubeSummonerItem::new, true);
    public static final RegistryObject<SoulProtectorItem> SOUL_PROTECTOR = register("soul_protector", SoulProtectorItem::new, true);

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> itemSupplier, boolean isCreativeTab) {
        RegistryObject<T> registryObject = ITEMS.register(name, itemSupplier);
        if (isCreativeTab) {
            TrialMonolithCreativeTabs.ITEMS.add(registryObject);
        }
        return registryObject;
    }


    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
