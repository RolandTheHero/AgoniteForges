package hero.roland.agoniteforges.block;

import hero.roland.agoniteforges.AgoniteForges;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;

public class ModBlockItemIds {
    private static BlockItemId create(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(AgoniteForges.MOD_ID, name);
        return BlockItemId.create(id, id);
    }
    public static final BlockItemId DEEPSLATE_AGONITE_ORE = create("deepslate_agonite_ore");
    public static final BlockItemId BEDROCK_AGONITE_ORE = create("bedrock_agonite_ore");
    public static final BlockItemId AGONITE_FORGE = create("agonite_forge");
}
