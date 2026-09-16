package hero.roland.agoniteforges.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record AgoniteForgeRecipe (
    ItemStackTemplate result,
    int experience,
    int cookingTime,
    ShapedRecipePattern.Data pattern
) implements Recipe<AgoniteForgeRecipeInput> {

    @Override
    public boolean matches(AgoniteForgeRecipeInput input, Level level) {
        List<String> pattern = this.pattern.pattern();
        Map<Character, Ingredient> key = this.pattern.key();

        for (int y = 0; y < 3; y++) {
            String row = pattern.get(y);

            for (int x = 0; x < 3; x++) {
                char symbol = row.charAt(x);
                ItemStack stack = input.getItem(y * 3 + x);

                if (symbol == ' ') {
                    if (!stack.isEmpty()) {
                        return false;
                    }
                    continue;
                }

                Ingredient ingredient = key.get(symbol);

                if (ingredient == null || !ingredient.test(stack)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(AgoniteForgeRecipeInput input) {
        return result.create();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String group() {
        return "forging";
    }

    @Override
    public RecipeSerializer<? extends Recipe<AgoniteForgeRecipeInput>> getSerializer() {
        return ModRecipes.FORGING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<AgoniteForgeRecipeInput>> getType() {
        return ModRecipes.FORGING_RECIPE_TYPE;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return null;
    }

    public static final MapCodec<AgoniteForgeRecipe> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                ItemStackTemplate.CODEC
                    .fieldOf("result")
                    .forGetter(AgoniteForgeRecipe::result),
                Codec.INT
                    .fieldOf("experience")
                    .forGetter(AgoniteForgeRecipe::experience),
                Codec.INT
                    .fieldOf("cookingtime")
                    .forGetter(AgoniteForgeRecipe::cookingTime),
                ShapedRecipePattern.Data.MAP_CODEC
                    .forGetter(AgoniteForgeRecipe::pattern)
            ).apply(instance, AgoniteForgeRecipe::new)
        );
    public static final StreamCodec<RegistryFriendlyByteBuf, AgoniteForgeRecipe> STREAM_CODEC =
        StreamCodec.of(
            (buf, recipe) -> {
                ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.result());
                buf.writeFloat(recipe.experience());
                buf.writeVarInt(recipe.cookingTime());

                // Pattern
                List<String> pattern = recipe.pattern().pattern();
                buf.writeVarInt(pattern.size());

                for (String row : pattern) {
                    buf.writeUtf(row);
                }

                // Key
                Map<Character, Ingredient> key = recipe.pattern().key();
                buf.writeVarInt(key.size());

                for (Map.Entry<Character, Ingredient> entry : key.entrySet()) {
                    buf.writeChar(entry.getKey());
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, entry.getValue());
                }
            },

            buf -> {
                ItemStackTemplate result =
                    ItemStackTemplate.STREAM_CODEC.decode(buf);

                int experience = buf.readInt();
                int cookingTime = buf.readVarInt();

                // Pattern
                int patternSize = buf.readVarInt();
                List<String> pattern = new ArrayList<>();

                for (int i = 0; i < patternSize; i++) {
                    pattern.add(buf.readUtf());
                }

                // Key
                int keySize = buf.readVarInt();
                Map<Character, Ingredient> key = new HashMap<>();

                for (int i = 0; i < keySize; i++) {
                    char symbol = buf.readChar();

                    Ingredient ingredient =
                        Ingredient.CONTENTS_STREAM_CODEC.decode(buf);

                    key.put(symbol, ingredient);
                }

                return new AgoniteForgeRecipe(
                    result,
                    experience,
                    cookingTime,
                    new ShapedRecipePattern.Data(key, pattern)
                );
            }
        );
}