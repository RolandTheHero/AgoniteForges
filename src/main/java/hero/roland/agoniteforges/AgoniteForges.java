package hero.roland.agoniteforges;

import hero.roland.agoniteforges.block.ModBlockEntities;
import hero.roland.agoniteforges.block.ModBlocks;
import hero.roland.agoniteforges.item.ModItems;
import hero.roland.agoniteforges.recipe.ModRecipes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgoniteForges implements ModInitializer {
    public static final String MOD_ID = "agoniteforges";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ResourceKey<DamageType> AGONY_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "agony"));

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModBlocks.initialize();
        ModBlockEntities.initialize();
        ModRecipes.initialize();

        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            ModWorldPlacedFeatures.AGONITE_ORE_PLACED_KEY
        );

        LOGGER.info("Agonite Forges initialized!");
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
