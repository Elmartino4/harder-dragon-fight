package github.elmartino4.dragonfight.mixin;

import github.elmartino4.dragonfight.util.EndermiteEntityAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndermiteEntity.class)
public abstract class EndermiteEntityMixin extends LivingEntity implements EndermiteEntityAccess {
    @Unique boolean explosive = false;

    protected EndermiteEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void injectPassenger(CallbackInfo ci){
        if(isDead()){
            for (Entity passenger : getPassengerList()) {
                if(passenger instanceof EndCrystalEntity){
                    passenger.damage(DamageSource.GENERIC, 1);
                }
            }
        }
    }

    @Override
    public boolean isExplosive(){
        return explosive;
    }

    @Override
    public void setExplosive(){
        explosive = true;
        EndCrystalEntity crystal = new EndCrystalEntity(EntityType.END_CRYSTAL, world);
        crystal.setShowBottom(false);
        crystal.startRiding((Entity)(Object)this, true);
    }
}
