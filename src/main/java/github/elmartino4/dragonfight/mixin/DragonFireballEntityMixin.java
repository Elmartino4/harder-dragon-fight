package github.elmartino4.dragonfight.mixin;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonFireballEntity.class)
public abstract class DragonFireballEntityMixin extends ExplosiveProjectileEntity {
    protected DragonFireballEntityMixin(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onCollision", at = @At(value = "FIELD", target = "net/minecraft/entity/projectile/DragonFireballEntity.world : Lnet/minecraft/world/World;"))
    private void onCheckedCollision(HitResult hitResult, CallbackInfo ci){
        Vec3d pos = hitResult.getPos();
        Explosion.DestructionType type = this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE;
        this.world.createExplosion((Entity) (Object) this, pos.x, pos.y, pos.z, 1.8F, type);
    }

    @ModifyVariable(method = "onCollision", at = @At(value = "INVOKE", target = "net/minecraft/entity/AreaEffectCloudEntity.addEffect (Lnet/minecraft/entity/effect/StatusEffectInstance;)V", shift = At.Shift.AFTER))
    private AreaEffectCloudEntity addEffect(AreaEffectCloudEntity previous){
        previous.addEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 20 * 5, 1));
        return previous;
    }
}
