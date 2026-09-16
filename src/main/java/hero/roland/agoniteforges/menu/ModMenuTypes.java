package hero.roland.agoniteforges.menu;

import hero.roland.agoniteforges.AgoniteForges;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {
    public static final MenuType<AgoniteForgeMenu> AGONITE_FORGE = register("agonite_forge", AgoniteForgeMenu::new);

    public static <T extends AbstractContainerMenu> MenuType<T> register(String name, MenuType.MenuSupplier<T> constructor) {
        return Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(AgoniteForges.MOD_ID, name), new MenuType<>(constructor, FeatureFlagSet.of()));
    }
}
