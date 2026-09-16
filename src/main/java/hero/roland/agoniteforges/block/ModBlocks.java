package hero.roland.agoniteforges.block;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public class ModBlocks {
    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(ModBlocks.DEEPSLATE_AGONITE_ORE.asItem());
            creativeTab.accept(ModBlocks.BEDROCK_AGONITE_ORE.asItem());
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(ModBlocks.AGONITE_FORGE.asItem());
        });
    }
    private static Block register(ResourceKey<Block> id, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties properties) {
        // Create the block instance
        Block block = blockFactory.apply(properties.setId(id));
        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }
    private static Block register(BlockItemId id, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties properties) {
        // Create the block instance
        Block block = register(id.block(), blockFactory, properties);
        // Create the block item instance
        BlockItem blockItem = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(id.item()));
        Registry.register(BuiltInRegistries.ITEM, id.item(), blockItem);
        return block;
    }

    public static final Block DEEPSLATE_AGONITE_ORE = register(
        ModBlockItemIds.DEEPSLATE_AGONITE_ORE,
        Block::new,
        BlockBehaviour.Properties.ofFullCopy(Blocks.ANCIENT_DEBRIS).sound(SoundType.DEEPSLATE)
    );
    public static final Block BEDROCK_AGONITE_ORE = register(
        ModBlockItemIds.BEDROCK_AGONITE_ORE,
        Block::new,
        BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK)
    );
    public static final Block AGONITE_FORGE = register(
        ModBlockItemIds.AGONITE_FORGE,
        AgoniteForgeBlock::new,
        BlockBehaviour.Properties.ofFullCopy(Blocks.ANCIENT_DEBRIS).sound(SoundType.AMETHYST)
    );
}

class AgoniteForgeBlock extends BaseEntityBlock {
    public static final MapCodec<AgoniteForgeBlock> CODEC = simpleCodec(AgoniteForgeBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public AgoniteForgeBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public MapCodec<AgoniteForgeBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new AgoniteForgeBlockEntity(worldPosition, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (!level.isClientSide()) {
            player.openMenu(blockState.getMenuProvider(level, blockPos));
            //player.awardStat(); (you can increment a custom stat here)
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.AGONITE_FORGE, AgoniteForgeBlockEntity::tick);
    }

    public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
        if (!state.getValue(LIT)) return;
        double x = (double) pos.getX() + (double) 0.5F;
        double y = pos.getY();
        double z = (double) pos.getZ() + (double) 0.5F;
        if (random.nextDouble() < 0.1) {
            level.playLocalSound(x, y, z, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }

        Direction direction = state.getValue(FACING);
        Direction.Axis axis = direction.getAxis();
        double r = 0.52;
        double ss = random.nextDouble() * 0.6 - 0.3;
        double dx = axis == Direction.Axis.X ? (double) direction.getStepX() * r : ss;
        double dy = random.nextDouble() * (double) 9.0F / (double) 16.0F;
        double dz = axis == Direction.Axis.Z ? (double) direction.getStepZ() * r : ss;
        level.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, 0.0F, 0.0F, 0.0F);
    }
}
