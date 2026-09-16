package hero.roland.agoniteforges.provider;

import hero.roland.agoniteforges.AgoniteForges;
import hero.roland.agoniteforges.item.ModItemIds;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.ItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        builder(ItemTags.SWORDS)
            .add(ModItemIds.AGONITE_SWORD);
        builder(AGONITE_FORGE_FUEL)
            .add(ItemIds.LAVA_BUCKET);
    }

    public static final TagKey<Item> AGONITE_FORGE_FUEL = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(AgoniteForges.MOD_ID, "forge_fuel"));
}

