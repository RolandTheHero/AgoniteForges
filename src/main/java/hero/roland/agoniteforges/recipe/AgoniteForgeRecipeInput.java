package hero.roland.agoniteforges.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record AgoniteForgeRecipeInput(
    ItemStack slot1, ItemStack slot2, ItemStack slot3,
    ItemStack slot4, ItemStack slot5, ItemStack slot6,
    ItemStack slot7, ItemStack slot8, ItemStack slot9
) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> this.slot1;
            case 1 -> this.slot2;
            case 2 -> this.slot3;
            case 3 -> this.slot4;
            case 4 -> this.slot5;
            case 5 -> this.slot6;
            case 6 -> this.slot7;
            case 7 -> this.slot8;
            case 8 -> this.slot9;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 9;
    }
}
