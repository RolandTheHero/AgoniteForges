package hero.roland.agoniteforges.recipe;

import hero.roland.agoniteforges.AgoniteForges;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipes {
    public static void initialize() {}

    public static final RecipeSerializer<AgoniteForgeRecipe> FORGING_RECIPE_SERIALIZER = Registry.register(
        BuiltInRegistries.RECIPE_SERIALIZER,
        Identifier.fromNamespaceAndPath(AgoniteForges.MOD_ID, "forging"),
        new RecipeSerializer<>(AgoniteForgeRecipe.CODEC, AgoniteForgeRecipe.STREAM_CODEC)
    );

    public static final RecipeType<AgoniteForgeRecipe> FORGING_RECIPE_TYPE = Registry.register(
        BuiltInRegistries.RECIPE_TYPE,
        Identifier.fromNamespaceAndPath(AgoniteForges.MOD_ID, "forging"),
        new RecipeType<AgoniteForgeRecipe>(){}
    );
}
