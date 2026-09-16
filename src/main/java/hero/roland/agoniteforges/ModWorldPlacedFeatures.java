package hero.roland.agoniteforges;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ModWorldPlacedFeatures {
    public static void configure(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        List<PlacementModifier> agoniteOreVeinModifiers = List.of(
            RarityFilter.onAverageOnceEvery(2),
            BiomeFilter.biome(),
            InSquarePlacement.spread(),
            HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.BOTTOM, VerticalAnchor.aboveBottom(6)))
        );
        context.register(
            AGONITE_ORE_PLACED_KEY,
            new PlacedFeature(
                configuredFeatures.getOrThrow(ModWorldConfiguredFeatures.AGONITE_ORE_VEIN_CONFIGURED_KEY),
                agoniteOreVeinModifiers
            )
        );
    }

    public static final ResourceKey<PlacedFeature> AGONITE_ORE_PLACED_KEY =
        ResourceKey.create(
            Registries.PLACED_FEATURE,
            AgoniteForges.id("agonite_ore_placed")
        );
}
