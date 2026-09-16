package hero.roland.agoniteforges.client;

import hero.roland.agoniteforges.ModWorldConfiguredFeatures;
import hero.roland.agoniteforges.ModWorldPlacedFeatures;
import hero.roland.agoniteforges.provider.ModBlockLootTableProvider;
import hero.roland.agoniteforges.provider.ModItemTagProvider;
import hero.roland.agoniteforges.provider.ModWorldgenProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class AgoniteForgesDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModItemTagProvider::new);
        pack.addProvider(ModBlockLootTableProvider::new);
        pack.addProvider(ModWorldgenProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, ModWorldConfiguredFeatures::configure);
        registryBuilder.add(Registries.PLACED_FEATURE, ModWorldPlacedFeatures::configure);
    }
}
