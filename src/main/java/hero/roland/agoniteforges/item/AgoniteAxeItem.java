package hero.roland.agoniteforges.item;

import hero.roland.agoniteforges.AgoniteForges;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class AgoniteAxeItem extends AxeItem {

    public AgoniteAxeItem(Properties properties) {
        super(ToolMaterial.NETHERITE, 0, 0, properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        InteractionResult res = super.useOn(context);
        if (!res.equals(InteractionResult.PASS)) return res;
        if (playerHasBlockingItemUseIntent(context)) return InteractionResult.PASS;
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        Optional<BlockState> newBlock = this.evaluateNewBlockState(level, pos, player, level.getBlockState(pos));
        if (newBlock.isEmpty()) return InteractionResult.PASS;

        ItemStack itemInHand = context.getItemInHand();
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, itemInHand);
        }
        level.setBlock(pos, newBlock.get(), 11);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newBlock.get()));
        if (player != null) {
            itemInHand.hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
        }
        DamageSource damageSource = new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).get(AgoniteForges.AGONY_DAMAGE.identifier()).orElseThrow());
        if (!level.isClientSide()) {
            player.hurtServer((ServerLevel) level, damageSource, 2f);
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean playerHasBlockingItemUseIntent(final UseOnContext context) {
        Player player = context.getPlayer();
        return context.getHand().equals(InteractionHand.MAIN_HAND) && player.getOffhandItem().has(DataComponents.BLOCKS_ATTACKS) && !player.isSecondaryUseActive();
    }

    private Optional<BlockState> evaluateNewBlockState(final Level level, final BlockPos pos, final @Nullable Player player, final BlockState oldState) {
        Optional<BlockState> unStrippedBlock = getUnstripped(oldState);
        if (unStrippedBlock.isPresent()) {
            level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            return unStrippedBlock;
        }
        return Optional.empty();
    }

    private Optional<BlockState> getUnstripped(final BlockState state) {
        for (Map.Entry<Block, Block> strippableEntries : STRIPPABLES.entrySet()) {
            if (!strippableEntries.getValue().equals(state.getBlock())) continue;
            return Optional.of(
                strippableEntries.getKey().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS))
            );
        }
        return Optional.empty();
    }
}
