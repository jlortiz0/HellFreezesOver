package org.jlortiz.HellFreezesOver.mixin;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalInt;

@Mixin(GeneratorOptions.class)
public class GeneratorOptionsMixin {
	@Shadow @Final private long seed;

	@Shadow @Final private Registry<DimensionOptions> options;

	@Inject(at = @At("RETURN"), method = "<init>(JZZLnet/minecraft/util/registry/Registry;Ljava/util/Optional;)V")
	private void fixSeed(CallbackInfo ci) {
		for (Identifier id : this.options.getIds()) {
			if (!id.getNamespace().equals("minecraft")) {
				continue;
			}
			DimensionOptions dim = this.options.get(id);
			ChunkGenerator g = dim.getChunkGenerator();
			if (g instanceof NoiseChunkGenerator gen) {
				gen = (NoiseChunkGenerator) gen.withSeed(this.seed);
				((MutableRegistry) options).replace(OptionalInt.empty(), RegistryKey.of(options.getKey(), id),
						new DimensionOptions(dim.getDimensionTypeSupplier(), gen), options.getEntryLifecycle(dim));
			}
		}
	}
}
