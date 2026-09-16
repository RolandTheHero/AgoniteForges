package hero.roland.agoniteforges.client;

import hero.roland.agoniteforges.client.screen.AgoniteForgeScreen;
import hero.roland.agoniteforges.menu.ModMenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class AgoniteForgesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModMenuTypes.AGONITE_FORGE, AgoniteForgeScreen::new);
    }
}
