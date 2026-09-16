package hero.roland.agoniteforges.client.screen;

import hero.roland.agoniteforges.AgoniteForges;
import hero.roland.agoniteforges.menu.AgoniteForgeMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class AgoniteForgeScreen extends AbstractContainerScreen<AgoniteForgeMenu> {
    private static final Identifier CONTAINER_TEXTURE = Identifier.fromNamespaceAndPath(AgoniteForges.MOD_ID, "textures/gui/agonite_forge.png");
    private static final Identifier BURN_PROGRESS_TEXTURE = Identifier.withDefaultNamespace("textures/gui/sprites/container/blast_furnace/burn_progress.png");
    private static final Identifier LIT_PROGRESS_TEXTURE = Identifier.withDefaultNamespace("textures/gui/sprites/container/blast_furnace/lit_progress.png");
    private AgoniteForgeMenu menu;

    public AgoniteForgeScreen(AgoniteForgeMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component, 176, 202);
        // Center the title
        this.menu = abstractContainerMenu;
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public AgoniteForgeMenu getMenu() { return menu; }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, BACKGROUND_TEXTURE_WIDTH, BACKGROUND_TEXTURE_HEIGHT);

        int fuel = menu.getData().get(0);
        int progress = menu.getData().get(1);
        // --------------------
        // Fuel / fire
        // --------------------
        int fireHeight = (int) (13 * (fuel / 100.0f));

        if (fireHeight > 0) {
            graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                LIT_PROGRESS_TEXTURE,
                this.leftPos + 48,
                this.topPos + 73 + (13 - fireHeight),
                0,
                13 - fireHeight,
                14,
                fireHeight,
                14,
                13
            );
        }

        // --------------------
        // Progress arrow
        // --------------------
        int arrowWidth = (int) (24 * (progress / 100.0f));

        if (arrowWidth > 0) {
            graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BURN_PROGRESS_TEXTURE,
                this.leftPos + 89,
                this.topPos + 34,
                0,
                0,
                arrowWidth,
                16,
                24,
                16
            );
        }
    }
}
