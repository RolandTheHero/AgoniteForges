package hero.roland.agoniteforges.item;

import hero.roland.agoniteforges.AgoniteForges;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItemIds {
    public static ResourceKey<Item> create(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(AgoniteForges.MOD_ID, name));
    }

    public static final ResourceKey<Item> RAW_AGONITE = create("raw_agonite");
    public static final ResourceKey<Item> AGONITE_INGOT = create("agonite_ingot");

    public static final ResourceKey<Item> AGONITE_SWORD = create("agonite_sword");
}
