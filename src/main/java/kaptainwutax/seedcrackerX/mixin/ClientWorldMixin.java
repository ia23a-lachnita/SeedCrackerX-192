package kaptainwutax.seedcrackerX.mixin;

import kaptainwutax.seedcrackerX.SeedCracker;
import kaptainwutax.seedcrackerX.config.StructureSave;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public abstract class ClientWorldMixin extends Level {

    protected ClientWorldMixin(ClientLevel.ClientLevelData clientLevelData,
                               ResourceKey<Level> dimension,
                               Holder<net.minecraft.world.level.dimension.DimensionType> dimensionType,
                               int viewDistance,
                               int simulationDistance,
                               java.util.function.Supplier<net.minecraft.util.profiling.ProfilerFiller> profiler,
                               boolean isClientSide,
                               boolean isDebug) {
        super(clientLevelData, dimension, dimensionType, profiler, isClientSide, isDebug, viewDistance, simulationDistance);
    }

    @Inject(method = "disconnect", at = @At("HEAD"))
    private void disconnect(CallbackInfo ci) {
        StructureSave.saveStructures(SeedCracker.get().getDataStorage().baseSeedData);
        SeedCracker.get().reset();
    }

    @Inject(method = "getUncachedNoiseBiome", at = @At("HEAD"), cancellable = true)
    private void getGeneratorStoredBiome(int p_205516_, int p_205517_, int p_205518_,
                                         CallbackInfoReturnable<Holder<Biome>> cir) {
        ResourceKey<Biome> voidBiome = ResourceKey.create(Registry.BIOME_REGISTRY, Biomes.THE_VOID.location());
        registryAccess().registry(Registry.BIOME_REGISTRY).ifPresent(registry -> {
            registry.getHolder(voidBiome).ifPresent(cir::setReturnValue);
        });
    }
}