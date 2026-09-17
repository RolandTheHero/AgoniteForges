package hero.roland.agoniteforges.item;

import hero.roland.agoniteforges.AgoniteForges;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AgonitePickaxeItem extends Item {
    public AgonitePickaxeItem(Properties properties) {
        super(properties);
    }
    public static int useDuration = 60;

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return useDuration;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.getCooldowns().isOnCooldown(this.getDefaultInstance())) {
            return InteractionResult.FAIL;
        }
        player.getCooldowns().addCooldown(this.getDefaultInstance(), useDuration);
        super.use(level, player, hand);
        player.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (remainingUseDuration <= 0) return;

        if (remainingUseDuration % 5 == 0) {
            BlockPos center = entity.blockPosition();
            for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-6, -6, -6),
                center.offset(6, 6, 6)
            )) {
                BlockState state = level.getBlockState(pos);

                if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                    level.playLocalSound(pos, SoundEvents.CHAIN_STEP, SoundSource.PLAYERS, 1f, 1f, true);
                } else if (state.is(BlockTags.NEEDS_IRON_TOOL)) {
                    level.playLocalSound(pos, SoundEvents.CHAIN_STEP, SoundSource.PLAYERS, 0.5f, 0f, true);
                }
            }
        }

        if (level.isClientSide()) return;
        if (remainingUseDuration == useDuration) {
            DamageSource damageSource = new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).get(AgoniteForges.AGONY_DAMAGE.identifier()).orElseThrow());
            entity.hurtServer((ServerLevel) level, damageSource, 2f);
        }
    }
}
