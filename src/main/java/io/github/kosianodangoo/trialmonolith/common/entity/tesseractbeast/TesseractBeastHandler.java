package io.github.kosianodangoo.trialmonolith.common.entity.tesseractbeast;

import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = TheTrialMonolith.MOD_ID)
public class TesseractBeastHandler {
    public static final Map<ResourceKey<Level>, TesseractBeastHandler> tesseractBeastHandlers = new Object2ObjectOpenHashMap<>();
    public static final Map<ResourceKey<Level>, TesseractBeastHandler> tickingTesseractBeastHandlers = new Object2ObjectOpenHashMap<>();

    public final Level level;
    public final List<TesseractBeastController> tesseractBeastControllers = new ArrayList<>();
    Collection<TesseractBeastController> toRemove = new ArrayList<>();
    Collection<TesseractBeastController> toAdd = new ArrayList<>();

    public TesseractBeastHandler(Level level) {
        this.level = level;
    }

    public static @NotNull TesseractBeastHandler getTesseractBeastHandler(Level level) {
        return tesseractBeastHandlers.computeIfAbsent(level.dimension(), (key) -> new TesseractBeastHandler(level));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        tickingTesseractBeastHandlers.forEach(((level, tesseractBeastHandler) -> tesseractBeastHandler.tick()));
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        tesseractBeastHandlers.forEach(((levelResourceKey, tesseractBeastHandler) -> tesseractBeastHandler.remove()));
    }

    public void remove() {
        tesseractBeastHandlers.remove(level.dimension());
        tickingTesseractBeastHandlers.remove(level.dimension());
        toAdd.clear();
        tesseractBeastControllers.clear();
    }

    public void addTesseractBeast(TesseractBeastController tesseractBeastController) {
        toAdd.add(tesseractBeastController);
        tickingTesseractBeastHandlers.putIfAbsent(level.dimension(), this);
    }

    public void tick() {
        tesseractBeastControllers.addAll(toAdd);
        toAdd.clear();
        tesseractBeastControllers.forEach(TesseractBeastController::tick);
        tesseractBeastControllers.removeAll(toRemove);
        toRemove.clear();
    }
}
