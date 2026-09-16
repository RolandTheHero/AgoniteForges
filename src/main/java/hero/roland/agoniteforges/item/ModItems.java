package hero.roland.agoniteforges.item;

import hero.roland.agoniteforges.AgoniteForges;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public class ModItems {
    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
            .register((creativeTab) -> {
                creativeTab.accept(ModItems.RAW_AGONITE);
                creativeTab.accept(ModItems.AGONITE_INGOT);
            });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
            .register((creativeTab) -> creativeTab.accept(ModItems.AGONITE_SWORD));
    }
    public static Item register(ResourceKey<Item> itemKey, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        // Create the item instance.
        Item item = itemFactory.apply(settings.setId(itemKey));
        // Register the item.
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static final Item RAW_AGONITE = register(ModItemIds.RAW_AGONITE, PainfulItem::new, new Item.Properties());
    public static final Item AGONITE_INGOT = register(ModItemIds.AGONITE_INGOT, PainfulItem::new, new Item.Properties());

    public static final Item AGONITE_SWORD = register(ModItemIds.AGONITE_SWORD, Item::new, new Item.Properties().sword(ToolMaterial.NETHERITE, 0f, 0f));
}

class PainfulItem extends Item {
    public PainfulItem(Properties properties) { super(properties); }

    @Override
    public void inventoryTick(final ItemStack itemStack, final ServerLevel level, final Entity owner, final @Nullable EquipmentSlot slot) {
        DamageSource damageSource = new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).get(AgoniteForges.AGONY_DAMAGE.identifier()).orElseThrow());
        if (owner.tickCount % 20 == 0) owner.hurtServer(level, damageSource, 0.5f * itemStack.count());
        super.inventoryTick(itemStack, level, owner, slot);
    }
}