package io.github.kosianodangoo.trialmonolith;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = TheTrialMonolith.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TrialMonolithConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.DoubleValue TRIAL_MONOLITH_HEALTH;
    private static final ForgeConfigSpec.DoubleValue TRIAL_MONOLITH_DAMAGE_CAP;
    public static final ForgeConfigSpec.BooleanValue TRIAL_MONOLITH_SHOULD_DIE_FROM_KILL;

    public static final ForgeConfigSpec.BooleanValue INVADER_MONOLITH_SHOULD_DIE_FROM_KILL;

    private static final ForgeConfigSpec.DoubleValue SMALL_BEAM_SOUL_DAMAGE;
    private static final ForgeConfigSpec.DoubleValue HUGE_BEAM_SOUL_DAMAGE;

    private static final ForgeConfigSpec.DoubleValue DAMAGE_CUBE_SOUL_DAMAGE;

    static {
        BUILDER.push("trialMonolith");

        TRIAL_MONOLITH_HEALTH = BUILDER.comment("Health of The Trial Monolith").defineInRange("trialMonolithHealth", Integer.MAX_VALUE, 0, Double.POSITIVE_INFINITY);
        TRIAL_MONOLITH_DAMAGE_CAP = BUILDER.comment("Damage Cap of The Trial Monolith").defineInRange("trialMonolithDamageCap", 1_000_000, 0, Double.POSITIVE_INFINITY);
        TRIAL_MONOLITH_SHOULD_DIE_FROM_KILL = BUILDER.comment("Whether The Trial Monolith should die from /kill").define("trialMonolithShouldDieFromKill", false);

        BUILDER.pop();

        BUILDER.push("invaderMonolith");

        INVADER_MONOLITH_SHOULD_DIE_FROM_KILL = BUILDER.comment("Whether The Invaderr Monolith should die from /kill").define("trialMonolithShouldDieFromKill", false);

        BUILDER.pop();

        BUILDER.push("attacks");

        SMALL_BEAM_SOUL_DAMAGE = BUILDER.comment("SoulDamage of Small Beams").defineInRange("smallBeamSoulDamage", 0.01, 0, Double.POSITIVE_INFINITY);
        HUGE_BEAM_SOUL_DAMAGE = BUILDER.comment("SoulDamage of Huge Beams").defineInRange("hugeBeamSoulDamage", 0.10, 0, Double.POSITIVE_INFINITY);

        DAMAGE_CUBE_SOUL_DAMAGE = BUILDER.comment("SoulDamage of Damage Cube").defineInRange("damageCubeSoulDamage", 0.03, 0, Double.POSITIVE_INFINITY);

        BUILDER.pop();
    }

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static float trialMonolithHealth = Integer.MAX_VALUE;
    public static float trialMonolithDamageCap = 1000000;

    public static float smallBeamSoulDamage = 0.01f;
    public static float hugeBeamSoulDamage = 0.10f;

    public static float damageCubeSoulDamage = 0.03f;

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent event) {
        trialMonolithHealth = TRIAL_MONOLITH_HEALTH.get().floatValue();
        trialMonolithDamageCap = TRIAL_MONOLITH_DAMAGE_CAP.get().floatValue();

        smallBeamSoulDamage = SMALL_BEAM_SOUL_DAMAGE.get().floatValue();
        hugeBeamSoulDamage = HUGE_BEAM_SOUL_DAMAGE.get().floatValue();

        damageCubeSoulDamage = DAMAGE_CUBE_SOUL_DAMAGE.get().floatValue();
    }
}
