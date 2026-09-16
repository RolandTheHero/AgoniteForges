package hero.roland.agoniteforges.provider;

import hero.roland.agoniteforges.block.ModBlocks;
import hero.roland.agoniteforges.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModBlockLootTableProvider extends FabricBlockLootSubProvider {
    public ModBlockLootTableProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        dropSelf(ModBlocks.AGONITE_FORGE);
        add(ModBlocks.DEEPSLATE_AGONITE_ORE, createOreDrop(ModBlocks.DEEPSLATE_AGONITE_ORE, ModItems.RAW_AGONITE));
    }
}