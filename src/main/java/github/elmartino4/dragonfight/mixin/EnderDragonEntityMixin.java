package github.elmartino4.dragonfight.mixin;

import github.elmartino4.dragonfight.DragonFight;
import github.elmartino4.dragonfight.util.EnderDragonEntityAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends MobEntity implements EnderDragonEntityAccess {
    protected EnderDragonEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void addToInit(CallbackInfo ci){
        getDataTracker().startTracking(DragonFight.LOW_HEALTH, false);
    }

    @ModifyConstant(method = "createEnderDragonAttributes", constant = @Constant(doubleValue = 200.0D))
    private static double setHealth(double previous){
        return previous + 150.0D;
    }

    @Inject(method = "damageLivingEntities", at = @At("HEAD"))
    private void onDamage(List<Entity> entities, CallbackInfo ci){
        for (Entity ent : entities) {
            if(ent instanceof LivingEntity){
                ((LivingEntity)ent).addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 20 / 6, 8));
            }
        }
    }

    @Redirect(method = "crystalDestroyed", at = @At(value = "INVOKE", target = "net/minecraft/entity/boss/dragon/EnderDragonEntity.damagePart (Lnet/minecraft/entity/boss/dragon/EnderDragonPart;Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean redirectDamage(EnderDragonEntity that, EnderDragonPart part, DamageSource source, float damage){
        heal(12.0F);
        return true;
    }

    @Override
    public <T> void putData(TrackedData<T> tag, Object data){
        dataTracker.set(tag, (T)data);
    }

    @Override
    public Object getData(TrackedData tag){
        return dataTracker.get(tag);
    }

    /*@ModifyConstant(method = "tickMovement", constant = @Constant(floatValue = 0.06F))
    private float setVelocityMultiplier(float previous){
        return previous * 1.8F;
    }*/
}
