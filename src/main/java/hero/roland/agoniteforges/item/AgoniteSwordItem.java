package hero.roland.agoniteforges.item;

import hero.roland.agoniteforges.AgoniteForges;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class AgoniteSwordItem extends Item {
    public AgoniteSwordItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        super.use(level, player, hand);
        player.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        if (remainingUseDuration > 0) {
            DamageSource damageSource = new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).get(AgoniteForges.AGONY_DAMAGE.identifier()).orElseThrow());
            entity.hurtServer(serverLevel, damageSource, 0.5f);
            Vec3 look = entity.getLookAngle();
            double spread = 0.15;
            Vec3 direction = new Vec3(
                look.x + (serverLevel.getRandom().nextDouble() - 0.5) * spread,
                look.y + (serverLevel.getRandom().nextDouble() - 0.5) * spread,
                look.z + (serverLevel.getRandom().nextDouble() - 0.5) * spread
            ).normalize();

            SmallFireball fireball = createFireball(level, entity, direction);
            level.playSound(
                null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                SoundEvents.FIRECHARGE_USE,
                SoundSource.PLAYERS,
                0.5f,
                1.0f
            );
            level.addFreshEntity(fireball);
        }
    }

    private static @NonNull SmallFireball createFireball(Level level, LivingEntity entity, Vec3 direction) {
        SmallFireball fireball = new SmallFireball(level, entity, direction) {
            @Override
            public void tick() {
                super.tick();
                if (this.tickCount >= 60) {
                    this.discard();
                }
            }
        };
        fireball.setPos(entity.getX(), entity.getEyeY(), entity.getZ());
        return fireball;
    }
}
