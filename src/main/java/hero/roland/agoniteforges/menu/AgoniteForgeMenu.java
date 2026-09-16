package hero.roland.agoniteforges.menu;

import hero.roland.agoniteforges.AgoniteForges;
import hero.roland.agoniteforges.block.AgoniteForgeBlockEntity;
import hero.roland.agoniteforges.block.ModBlocks;
import hero.roland.agoniteforges.recipe.AgoniteForgeRecipe;
import hero.roland.agoniteforges.recipe.AgoniteForgeRecipeInput;
import hero.roland.agoniteforges.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Optional;

public class AgoniteForgeMenu extends AbstractContainerMenu {
    private static final int SLOTS_ROWS = 3;
    private static final int SLOTS_COLUMNS = 3;
    private static final int CRAFTING_SLOTS = SLOTS_ROWS * SLOTS_COLUMNS;
    private static final int CONTAINER_SLOTS = 11;

    private static final int CRAFTING_START = 0;
    private static final int FUEL_SLOT = 9;
    private static final int OUTPUT_SLOT = 10;

    private static final int INVENTORY_START = CONTAINER_SLOTS;
    private static final int INVENTORY_END = INVENTORY_START + Inventory.INVENTORY_SIZE;

    // 3x3 crafting grid
    private static final int CRAFTING_START_X = 30;
    private static final int CRAFTING_START_Y = 17;

    // Fuel below crafting grid
    private static final int FUEL_X = 48;
    private static final int FUEL_Y = 89;

    // Output to the right
    private static final int OUTPUT_X = 124;
    private static final int OUTPUT_Y = 35;

    // Player inventory
    private static final int INVENTORY_START_X = 8;
    private static final int INVENTORY_START_Y = 120;

    private final AgoniteForgeBlockEntity container;
    private final ContainerLevelAccess access;
    private final ContainerData data;

    // Client-side constructor
    public AgoniteForgeMenu(final int containerId, final Inventory inventory) {
        this(containerId, inventory, new AgoniteForgeBlockEntity(BlockPos.ZERO, ModBlocks.AGONITE_FORGE.defaultBlockState()), ContainerLevelAccess.NULL);
    }

    // Server-side constructor
    public AgoniteForgeMenu(
        final int containerId,
        final Inventory inventory,
        final AgoniteForgeBlockEntity container,
        ContainerLevelAccess access
    ) {
        super(ModMenuTypes.AGONITE_FORGE, containerId);
        this.access = access;

        checkContainerSize(container, CONTAINER_SLOTS);
        this.container = container;
        this.data = container.getData();
        this.addDataSlots(this.data);

        //output.startOpen(inventory.player);

        this.add3x3GridSlots();

        // Fuel
        this.addSlot(new Slot(
            this.container,
            FUEL_SLOT,
            FUEL_X,
            FUEL_Y
        ));

        // Output
        this.addSlot(new FurnaceResultSlot(
            inventory.player,
            this.container,
            OUTPUT_SLOT,
            OUTPUT_X,
            OUTPUT_Y
        ) {
            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
                AgoniteForgeMenu.this.container.awardXp(player);
            }
        });

        // Player inventory
        this.addStandardInventorySlots(
            inventory,
            INVENTORY_START_X,
            INVENTORY_START_Y
        );
    }

    private void add3x3GridSlots() {
        for (int y = 0; y < SLOTS_ROWS; y++) {
            for (int x = 0; x < SLOTS_COLUMNS; x++) {
                final int slot = x + y * SLOTS_COLUMNS;

                this.addSlot(new Slot(
                    this.container,
                    slot,
                    CRAFTING_START_X + x * SLOT_SIZE,
                    CRAFTING_START_Y + y * SLOT_SIZE
                ));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack clicked = stack.copy();

        if (slotIndex < CONTAINER_SLOTS) {
            // Machine -> player inventory
            if (!this.moveItemStackTo(
                stack,
                INVENTORY_START,
                INVENTORY_END,
                true
            )) {
                return ItemStack.EMPTY;
            }
        } else {
            // Player inventory -> machine
            if (!this.moveItemStackTo(
                stack,
                CRAFTING_START,
                OUTPUT_SLOT,
                false
            )) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return clicked;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.AGONITE_FORGE);
    }

    public ContainerData getData() { return data; }
}