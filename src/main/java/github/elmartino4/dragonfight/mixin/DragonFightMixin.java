package github.elmartino4.dragonfight.mixin;

import github.elmartino4.dragonfight.DragonFight;
import github.elmartino4.dragonfight.util.EndermiteEntityAccess;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.EnderDragonSpawnState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;
import java.util.UUID;

@Mixin(EnderDragonFight.class)
public abstract class DragonFightMixin {
    @Shadow @Final private ServerWorld world;
    @Shadow @Nullable protected abstract BlockPattern.Result findEndPortal();
    @Shadow private EnderDragonSpawnState dragonSpawnState;
    @Shadow private UUID dragonUuid;

    @Shadow public abstract void resetEndCrystals();

    @Unique double summonTimer;
    @Unique Random random = new Random();
    @Unique boolean phaseActive;

    @Inject(method = "respawnDragon()V", at = @At("HEAD"))
    private void setPhaseInactive(CallbackInfo ci){
        phaseActive = false;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void doEndermiteTick(CallbackInfo ci){

        if(summonTimer-- < 0){
            EnderDragonEntity ent = (EnderDragonEntity)world.getEntity(dragonUuid);

            summonTimer = 20.0 * 2 + random.nextDouble() * 20.0 * (10 - 2);

            double[][] posList = {
                    {0, -2, -3},
                    {-3, -2, 0},
                    {-6, -2, -3},
                    {-3, -2, -6}
            };

            if(ent != null){
                if(findEndPortal() != null && ent.getHealth() > 50){
                    for (int i = 0; i < 4; i++) {
                        EndermiteEntity entity = new EndermiteEntity(EntityType.ENDERMITE, world);
                        ((EndermiteEntityAccess)entity).setExplosive();
                        entity.setPosition(Vec3d.ofCenter(findEndPortal().getFrontTopLeft().add(posList[i][0], posList[i][1], posList[i][2])));
                        StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 2000, 5);
                        effect.setPermanent(true);
                        entity.addStatusEffect(effect);
                        world.spawnEntityAndPassengers(entity);

                        //System.out.println("tried summon");
                    }
                }

                if(ent.getHealth() < ent.getMaxHealth() / 2 && !phaseActive){
                    phaseActive = true;
                    resetEndCrystals();
                }
            }
        }
    }
}
