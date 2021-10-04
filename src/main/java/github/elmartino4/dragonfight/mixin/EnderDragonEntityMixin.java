package github.elmartino4.dragonfight.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(EnderDragonEntity.class)
public class EnderDragonEntityMixin {
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

    @ModifyConstant(method = "tickMovement", constant = @Constant(floatValue = 0.06F))
    private float setVelocityMultiplier(float previous){
        return previous * 1.8F;
    }
}
