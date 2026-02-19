package io.github.kosianodangoo.trialmonolith.common.init;

import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import io.github.kosianodangoo.trialmonolith.common.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class TrialMonolithItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TheTrialMonolith.MOD_ID);

    public static final RegistryObject<Item> MONOLITH_FRAGMENT = register("monolith_fragment", () -> new Item(new Item.Properties()), true);
    public static final RegistryObject<Item> MONOLITH_SPAWN_EGG = register("trial_monolith_spawn_egg", () -> new ForgeSpawnEggItem(TrialMonolithEntities.TRIAL_MONOLITH, 0x000000, 0x440044, new Item.Properties()), true);
    public static final RegistryObject<Item> INVADER_MONOLITH_SPAWN_EGG = register("invader_monolith_spawn_egg", () -> new ForgeSpawnEggItem(TrialMonolithEntities.INVADER_MONOLITH, 0x660000, 0x992222, new Item.Properties()) {
        @Override
        public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
            super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
            pTooltipComponents.add(Component.translatable("tooltip.the_trial_monolith.invader_monolith_spawn_egg"));
        }
    }, true);
    public static final RegistryObject<BeamSummonerItem> BEAM_SUMMONER = register("beam_summoner", BeamSummonerItem::new, true);
    public static final RegistryObject<DamageCubeSummonerItem> DAMAGE_CUBE_SUMMONER = register("damage_cube_summoner", DamageCubeSummonerItem::new, true);
    public static final RegistryObject<SoulProtectorItem> SOUL_PROTECTOR = register("soul_protector", SoulProtectorItem::new, true);
    public static final RegistryObject<OverClockerItem> OVER_CLOCKER = register("over_clocker", OverClockerItem::new, true);
    public static final RegistryObject<BottleOfTheSoulItem> BOTTLE_OF_THE_SOUL = register("bottle_of_the_soul", BottleOfTheSoulItem::new, true);

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
