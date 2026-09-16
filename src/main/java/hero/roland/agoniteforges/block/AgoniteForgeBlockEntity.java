package hero.roland.agoniteforges.block;

import hero.roland.agoniteforges.ImplementedContainer;
import hero.roland.agoniteforges.provider.ModItemTagProvider;
import hero.roland.agoniteforges.menu.AgoniteForgeMenu;
import hero.roland.agoniteforges.recipe.AgoniteForgeRecipe;
import hero.roland.agoniteforges.recipe.AgoniteForgeRecipeInput;
import hero.roland.agoniteforges.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class AgoniteForgeBlockEntity extends BlockEntity implements ImplementedContainer, MenuProvider {
    private final NonNullList<ItemStack> items = NonNullList.withSize(11, ItemStack.EMPTY);
    private float progressArrow = 0; // 0 - 1
    private float fuelRemaining = 0;
    private int storedXp = 0;
    private ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> (int) (getFuelRemaining() * 100);
                case 1 -> (int) (getProgressArrow() * 100);
                default -> 0;
            };
        }
        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> fuelRemaining = value / 100f;
                case 1 -> progressArrow = value / 100f;
            }
        }
        @Override
        public int getCount() {
            return 2;
        }
    };

    public AgoniteForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AGONITE_FORGE, pos, state);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.agoniteforges.agonite_forge");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new AgoniteForgeMenu(containerId, inventory, this, ContainerLevelAccess.create(level, worldPosition));
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        setChanged();
    }

    public float getProgressArrow() { return progressArrow; }
    public float getFuelRemaining() { return fuelRemaining; }

    private void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        level.setBlock(worldPosition, getBlockState().setValue(AgoniteForgeBlock.LIT, fuelRemaining > 0), AgoniteForgeBlock.UPDATE_ALL);
        fuelRemaining -= 0.001f;
        if (progressArrow < 0) progressArrow = 0;
        if (fuelRemaining < 0) fuelRemaining = 0;
        AgoniteForgeRecipeInput recipeInput = new AgoniteForgeRecipeInput(
            items.get(0), items.get(1), items.get(2),
            items.get(3), items.get(4), items.get(5),
            items.get(6), items.get(7), items.get(8)
        );
        Optional<RecipeHolder<AgoniteForgeRecipe>> maybeRecipe = serverLevel.recipeAccess().getRecipeFor(ModRecipes.FORGING_RECIPE_TYPE, recipeInput, level);
        if (maybeRecipe.isEmpty()) {
            progressArrow = 0;
            return;
        }
        RecipeHolder<AgoniteForgeRecipe> recipeHolder = maybeRecipe.get();
        AgoniteForgeRecipe recipe = recipeHolder.value();
        int cookingTime = recipe.cookingTime();
        if (fuelRemaining <= 0) {
            ItemStack fuel = items.get(9);
            if (!fuel.is(ModItemTagProvider.AGONITE_FORGE_FUEL)) {
                progressArrow -= 2f / cookingTime;
                return;
            }
            items.set(9, new ItemStack(Items.BUCKET));
            fuelRemaining = 1f;
        }
        setChanged();
        progressArrow += 1f / cookingTime;
        if (progressArrow >= 1) {
            progressArrow = 0;
            setItem(10, recipe.assemble(recipeInput));
            storedXp += recipe.experience();
            clearGrid();
        }
    }

    private void clearGrid() {
        items.set(0, ItemStack.EMPTY);
        items.set(1, ItemStack.EMPTY);
        items.set(2, ItemStack.EMPTY);
        items.set(3, ItemStack.EMPTY);
        items.set(4, ItemStack.EMPTY);
        items.set(5, ItemStack.EMPTY);
        items.set(6, ItemStack.EMPTY);
        items.set(7, ItemStack.EMPTY);
        items.set(8, ItemStack.EMPTY);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AgoniteForgeBlockEntity entity) {
        entity.tick(level, pos, state);
    }

    public ContainerData getData() { return data; }

    public void awardXp(Player player) { player.giveExperiencePoints(storedXp); }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        ContainerHelper.loadAllItems(input, this.items);

        this.progressArrow = input.getFloatOr("progress", 0.0f);
        this.fuelRemaining = input.getFloatOr("fuel", 0.0f);
        this.storedXp = input.getIntOr("storedXp", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, this.items);

        output.putFloat("progress", this.progressArrow);
        output.putFloat("fuel", this.fuelRemaining);
        output.putInt("storedXp", this.storedXp);

        super.saveAdditional(output);
    }
}
