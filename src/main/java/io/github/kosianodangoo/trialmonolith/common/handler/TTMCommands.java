package io.github.kosianodangoo.trialmonolith.common.handler;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = TheTrialMonolith.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TTMCommands {
    public static ArgumentBuilder<CommandSourceStack, ?> SOUL_PROTECTION_COMMAND;
    public static ArgumentBuilder<CommandSourceStack, ?> OVERCLOCK_COMMAND;
    public static ArgumentBuilder<CommandSourceStack, ?> HIGH_DIMENSIONAL_BARRIER_COMMAND;
    public static ArgumentBuilder<CommandSourceStack, ?> SOUL_DAMAGE_COMMAND;

    public static Component ENABLED_MESSAGE = Component.translatable("commands.the_trial_monolith.enabled");
    public static Component DISABLED_MESSAGE = Component.translatable("commands.the_trial_monolith.disabled");

    static {
        SOUL_PROTECTION_COMMAND = Commands.literal("soulProtection").then(
                Commands.argument("entities", EntityArgument.entities()).then(
                        Commands.argument("protect", BoolArgumentType.bool()).executes(ctx -> {
                            boolean protect = BoolArgumentType.getBool(ctx, "protect");
                            int count = 0;
                            Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "entities");
                            for (Entity entity : entities) {
                                EntityHelper.setSoulProtected(entity, protect);
                                count++;
                            }
                            Component state = protect ? ENABLED_MESSAGE : DISABLED_MESSAGE;
                            if (count == 1) {
                                ctx.getSource().sendSuccess(() -> Component.translatable("commands.the_trial_monolith.soul_protection.success.multiple", entities.iterator().next().getDisplayName(), state), true);
                            } else {
                                int finalCount = count;
                                ctx.getSource().sendSuccess(() -> Component.translatable("commands.the_trial_monolith.soul_protection.success.multiple", finalCount, state), true);
                            }
                            return count;
                        })
                ).executes(ctx -> {
                    Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "entities");
                    if (entities.size() != 1) {
                        ctx.getSource().sendFailure(Component.translatable("commands.the_trial_monolith.failure.target_must_be_single"));
                        return 0;
                    }
                    Entity entity = entities.iterator().next();
                    Component state = EntityHelper.isSoulProtected(entity) ? ENABLED_MESSAGE : DISABLED_MESSAGE;
                    ctx.getSource().sendSuccess(() -> Component.translatable("commands.the_trial_monolith.soul_protection.success.get", entity.getDisplayName(), state), false);
                    return 1;
                })
        );

        OVERCLOCK_COMMAND = Commands.literal("overclock").then(
                Commands.argument("entities", EntityArgument.entities()).then(
                        Commands.argument("overclock", BoolArgumentType.bool()).executes(ctx -> {
                            boolean overclock = BoolArgumentType.getBool(ctx, "overclock");
                            int count = 0;
                            Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "entities");
                            for (Entity entity : entities) {
                                EntityHelper.setOverClocked(entity, overclock);
                                count++;
                            }
                            Component state = overclock ? ENABLED_MESSAGE : DISABLED_MESSAGE;
                            if (count == 1) {
                                ctx.getSource().sendSuccess(() -> Component.translatable("commands.the_trial_monolith.overclock.success.multiple", entities.iterator().next().getDisplayName(), state), true);
                            } else {
                                int finalCount = count;
                                ctx.getSource().sendSuccess(() -> Component.translatable("commands.the_trial_monolith.overclock.success.multiple", finalCount, state), true);
                            }
                            return count;
                        })
                ).executes(ctx -> {
                    Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "entities");
                    if (entities.size() != 1) {
                        ctx.getSource().sendFailure(Component.translatable("commands.the_trial_monolith.failure.target_must_be_single"));
                        return 0;
                    }
                    Entity entity = entities.iterator().next();
                    Component state = EntityHelper.isOverClocked(entity) ? ENABLED_MESSAGE : DISABLED_MESSAGE;
                    ctx.getSource().sendSuccess(() -> Component.translatable("commands.the_trial_monolith.overclock.success.get", entity.getDisplayName(), state), false);
                    return 1;
                })
        );

        HIGH_DIMENSIONAL_BARRIER_COMMAND = Commands.literal("highDimensionalBarrier").then(
                Commands.argument("entities", EntityArgument.entities()).then(
                        Commands.argument("highDimensionalBarrier", BoolArgumentType.bool()).executes(ctx -> {
                            boolean highDimensionalBarrier = BoolArgumentType.getBool(ctx, "highDimensionalBarrier");
                            int count = 0;
                            Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "entities");
                            for (Entity entity : entities) {
                                EntityHelper.setHighDimensionalBarrier(entity, highDimensionalBarrier);
                                count++;
                            }
                            Component state = highDimensionalBarrier ? ENABLED_MESSAGE : DISABLED_MESSAGE;
                            if (count == 1) {
                                ctx.getSource().sendSuccess(() -> Component.translatable("commands.the_trial_monolith.high_dimensional_barrier.success.multiple", entities.iterator().next().getDisplayName(), state), true);
                            } else {
                                int finalCount = count;
                                ctx.getSource().sendSuccess(() -> Component.translatable("commands.the_trial_monolith.high_dimensional_barrier.success.multiple", finalCount, state), true);
                            }
                            return count;
                        })
                ).executes(ctx -> {
                    Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "entities");
                    if (entities.size() != 1) {
                        ctx.getSource().sendFailure(Component.translatable("commands.the_trial_monolith.failure.target_must_be_single"));
                        return 0;
                    }
                    Entity entity = entities.iterator().next();
                    Component state = EntityHelper.hasHighDimensionalBarrier(entity) ? ENABLED_MESSAGE : DISABLED_MESSAGE;
                    ctx.getSource().sendSuccess(() -> Component.translatable("commands.the_trial_monolith.high_dimensional_barrier.success.get", entity.getDisplayName(), state), false);
                    return 1;
                })
        );

        SOUL_DAMAGE_COMMAND = Commands.literal("soulDamage").then(
                Commands.argument("entities", EntityArgument.entities()).then(
                        Commands.argument("soulDamage", FloatArgumentType.floatArg()).executes(ctx -> {
                            float soulDamage = FloatArgumentType.getFloat(ctx, "soulDamage");
                            int count = 0;
                            Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "entities");
                            for (Entity entity : entities) {
                                EntityHelper.setSoulDamage(entity, soulDamage);
                                count++;
                            }
                            if (count == 1) {
                                ctx.getSource().sendSuccess(() -> Component.translatable("commands.the_trial_monolith.soul_damage.success.multiple", entities.iterator().next().getDisplayName(), soulDamage), true);
                            } else {
                                int finalCount = count;
                                ctx.getSource().sendSuccess(() -> Component.translatable("commands.the_trial_monolith.soul_damage.success.multiple", finalCount, soulDamage), true);
                            }
                            return count;
                        })
                ).executes(ctx -> {
                    Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "entities");
                    if (entities.size() != 1) {
                        ctx.getSource().sendFailure(Component.translatable("commands.the_trial_monolith.failure.target_must_be_single"));
                        return 0;
                    }
                    Entity entity = entities.iterator().next();
                    ctx.getSource().sendSuccess(() -> Component.translatable("commands.the_trial_monolith.soul_damage.success.get", entity.getDisplayName(), EntityHelper.getSoulDamage(entity)), false);
                    return 1;
                })
        );
    }

    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("the_trial_monolith").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(SOUL_PROTECTION_COMMAND)
                .then(OVERCLOCK_COMMAND)
                .then(HIGH_DIMENSIONAL_BARRIER_COMMAND)
                .then(SOUL_DAMAGE_COMMAND)
        );
    }
}
