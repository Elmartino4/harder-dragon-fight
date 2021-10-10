package github.elmartino4.dragonfight.mixin;

import github.elmartino4.dragonfight.util.DragonFightAccess;
import github.elmartino4.dragonfight.util.EnderDragonEntityAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EndermanEntity.class)
public abstract class EndermanEntityMixin extends MobEntity {

    protected EndermanEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "mobTick", at = @At("HEAD"))
    private void tickInject(CallbackInfo ci){
        //doesnt work yet due to no tick
        if(this.world instanceof ServerWorld && this.getTarget() == null){
            EnderDragonFight fight = ((ServerWorld)this.world).getEnderDragonFight();
            if(fight != null){
                if(((DragonFightAccess)fight).isLowHealth()){
                    List<PlayerEntity> list = this.world.getPlayers(
                            TargetPredicate.createAttackable().setBaseMaxDistance(96.0D),
                            (LivingEntity)(Object)this,
                            this.getBoundingBox().expand(96, 10, 96));


                    if (list.size() > 0) {
                        this.setTarget(list.get(random.nextInt(list.size())));
                    }
                }
            }
        }
    }
}
