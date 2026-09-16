package hero.roland.agoniteforges;

import hero.roland.agoniteforges.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class ModWorldConfiguredFeatures {
    public static void configure(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest deepslateReplaceableRule = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest bedrockReplaceableRule = new BlockMatchTest(Blocks.BEDROCK);
        List<OreConfiguration.TargetBlockState> deepslateAgoniteOreConfig = List.of(
            OreConfiguration.target(deepslateReplaceableRule, ModBlocks.DEEPSLATE_AGONITE_ORE.defaultBlockState()),
            OreConfiguration.target(bedrockReplaceableRule, ModBlocks.BEDROCK_AGONITE_ORE.defaultBlockState())
        );
        context.register(AGONITE_ORE_VEIN_CONFIGURED_KEY, new ConfiguredFeature<>(
            Feature.ORE,
            new OreConfiguration(deepslateAgoniteOreConfig, 3)
        ));
    }

    public static final ResourceKey<ConfiguredFeature<?, ?>> AGONITE_ORE_VEIN_CONFIGURED_KEY =
        ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            AgoniteForges.id("agonite_ore_vein")
        );
}
