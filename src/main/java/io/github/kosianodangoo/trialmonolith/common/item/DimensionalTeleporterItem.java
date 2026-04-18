package io.github.kosianodangoo.trialmonolith.common.item;

import io.github.kosianodangoo.trialmonolith.client.screen.DimensionalTeleporterScreen;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityTeleportHelper;
import io.github.kosianodangoo.trialmonolith.common.network.TeleporterSlot;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class DimensionalTeleporterItem extends Item {
    public static final String TARGETS_TAG = "Targets";
    public static final String SELECTED_TAG = "Selected";

    public DimensionalTeleporterItem(Item.Properties properties) {
        super(properties);
    }

    public DimensionalTeleporterItem() {
        this(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (pPlayer.isShiftKeyDown()) {
            if (pLevel.isClientSide) {
                openScreen(stack, pHand);
            }
        } else {
            teleportEntity(stack, pPlayer);
        }
        return InteractionResultHolder.success(stack);
    }

    @OnlyIn(Dist.CLIENT)
    private static void openScreen(ItemStack stack, InteractionHand hand) {
        Minecraft.getInstance().setScreen(new DimensionalTeleporterScreen(stack, new TeleporterSlot.Hand(hand)));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!entity.level.isClientSide) {
            teleportEntity(stack, entity);
        }
        return true;
    }

    public boolean teleportEntity(ItemStack stack, Entity entity) {
        DimensionalTeleportTarget target = getTarget(stack);
        if (target == null) return false;
        MinecraftServer server = entity.level.getServer();
        if (server == null) return false;
        return EntityTeleportHelper.teleport(entity, target.position, server.getLevel(target.dimension));
    }

    public List<DimensionalTeleportTarget> getTargets(ItemStack stack) {
        ListTag targetsTag = stack.getOrCreateTag().getList(TARGETS_TAG, Tag.TAG_COMPOUND);
        return targetsTag.stream()
                .map(tag -> DimensionalTeleportTarget.load((CompoundTag) tag))
                .filter(Objects::nonNull)
                .toList();
    }

    public @Nullable DimensionalTeleportTarget getTarget(ItemStack stack) {
        ListTag targetsTag = stack.getOrCreateTag().getList(TARGETS_TAG, Tag.TAG_COMPOUND);
        int selected = getSelected(stack);
        if (selected < 0 || selected >= targetsTag.size()) return null;
        return DimensionalTeleportTarget.load(targetsTag.getCompound(selected));
    }

    public int getSelected(ItemStack stack) {
        return stack.getOrCreateTag().getInt(SELECTED_TAG);
    }

    public void setSelected(ItemStack stack, int selected) {
        ListTag targetsTag = stack.getOrCreateTag().getList(TARGETS_TAG, Tag.TAG_COMPOUND);
        int clamped = targetsTag.isEmpty() ? 0 : Math.max(0, Math.min(selected, targetsTag.size() - 1));
        stack.getOrCreateTag().putInt(SELECTED_TAG, clamped);
    }

    public void addTarget(ItemStack stack, DimensionalTeleportTarget target) {
        ListTag targetsTag = stack.getOrCreateTag().getList(TARGETS_TAG, Tag.TAG_COMPOUND);
        targetsTag.add(target.save());
        stack.getOrCreateTag().put(TARGETS_TAG, targetsTag);
        stack.getOrCreateTag().putInt(SELECTED_TAG, targetsTag.size() - 1);
    }

    public void updateTarget(ItemStack stack, int index, DimensionalTeleportTarget target) {
        ListTag targetsTag = stack.getOrCreateTag().getList(TARGETS_TAG, Tag.TAG_COMPOUND);
        if (index < 0 || index >= targetsTag.size()) return;
        targetsTag.set(index, target.save());
        stack.getOrCreateTag().put(TARGETS_TAG, targetsTag);
    }

    public void deleteTarget(ItemStack stack, int index) {
        ListTag targetsTag = stack.getOrCreateTag().getList(TARGETS_TAG, Tag.TAG_COMPOUND);
        if (index < 0 || index >= targetsTag.size()) return;
        targetsTag.remove(index);
        stack.getOrCreateTag().put(TARGETS_TAG, targetsTag);
        int selected = getSelected(stack);
        if (selected >= targetsTag.size()) {
            stack.getOrCreateTag().putInt(SELECTED_TAG, Math.max(0, targetsTag.size() - 1));
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        DimensionalTeleportTarget target = this.getTarget(pStack);
        if (target == null) return;
        pTooltipComponents.add(Component.literal(target.name).withStyle(ChatFormatting.AQUA));
        pTooltipComponents.add(Component.literal(String.format("%.2f, %.2f, %.2f", target.position.x, target.position.y, target.position.z)).withStyle(ChatFormatting.YELLOW));
        pTooltipComponents.add(Component.literal(target.dimension.location().toString()).withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    public record DimensionalTeleportTarget(Vec3 position, ResourceKey<Level> dimension, String name) {
        public static final String POSITION_TAG = "Position";
        public static final String DIMENSION_TAG = "Dimension";
        public static final String NAME_TAG = "Name";

        public static @Nullable DimensionalTeleportTarget load(CompoundTag compoundTag) {
            if (compoundTag == null || compoundTag.isEmpty()) return null;
            String dimStr = compoundTag.getString(DIMENSION_TAG);
            if (dimStr.isEmpty()) return null;
            ResourceLocation dimLoc = ResourceLocation.tryParse(dimStr);
            if (dimLoc == null) return null;
            CompoundTag positionTag = compoundTag.getCompound(POSITION_TAG);
            return new DimensionalTeleportTarget(
                    new Vec3(positionTag.getDouble("x"), positionTag.getDouble("y"), positionTag.getDouble("z")),
                    ResourceKey.create(Registries.DIMENSION, dimLoc),
                    compoundTag.getString(NAME_TAG)
            );
        }

        public CompoundTag save() {
            CompoundTag compoundTag = new CompoundTag();
            CompoundTag positionTag = new CompoundTag();
            positionTag.putDouble("x", position.x);
            positionTag.putDouble("y", position.y);
            positionTag.putDouble("z", position.z);
            compoundTag.put(POSITION_TAG, positionTag);
            compoundTag.putString(NAME_TAG, name);
            compoundTag.putString(DIMENSION_TAG, dimension.location().toString());
            return compoundTag;
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeUtf(this.name);
            buf.writeResourceLocation(this.dimension.location());
            buf.writeDouble(this.position.x);
            buf.writeDouble(this.position.y);
            buf.writeDouble(this.position.z);
        }

        public static DimensionalTeleportTarget decode(FriendlyByteBuf buf) {
            String name = buf.readUtf();
            ResourceLocation dim = buf.readResourceLocation();
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            return new DimensionalTeleportTarget(new Vec3(x, y, z), ResourceKey.create(Registries.DIMENSION, dim), name);
        }
    }
}
