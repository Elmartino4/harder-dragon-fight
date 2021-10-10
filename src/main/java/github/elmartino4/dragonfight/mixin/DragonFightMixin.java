package github.elmartino4.dragonfight.mixin;

import github.elmartino4.dragonfight.DragonFight;
import github.elmartino4.dragonfight.util.DragonFightAccess;
import github.elmartino4.dragonfight.util.EnderDragonEntityAccess;
import github.elmartino4.dragonfight.util.EndermiteEntityAccess;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.EnderDragonSpawnState;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.feature.EndSpikeFeature;
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
public abstract class DragonFightMixin implements DragonFightAccess {
    @Shadow @Final private ServerWorld world;
    @Shadow @Nullable protected abstract BlockPattern.Result findEndPortal();
    @Shadow private EnderDragonSpawnState dragonSpawnState;
    @Shadow private UUID dragonUuid;

    @Shadow public abstract void resetEndCrystals();

    @Unique double summonTimer;
    @Unique int crystalTimer;
    @Unique Random random = new Random();
    @Unique boolean phaseActive;
    @Unique int tower = 0;

    @Override
    public boolean isLowHealth(){
        return phaseActive;
    }

    @Inject(method = "respawnDragon()V", at = @At("HEAD"))
    private void setPhaseInactive(CallbackInfo ci){
        phaseActive = false;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void doEndermiteTick(CallbackInfo ci){
        if(!phaseActive){
            EnderDragonEntity ent = (EnderDragonEntity)world.getEntity(dragonUuid);

            if(ent != null){
                phaseActive = ent.getDataTracker().get(DragonFight.LOW_HEALTH);
            }
        }

        if(summonTimer-- < 0){
            System.out.print("tried summon");

            EnderDragonEntity ent = (EnderDragonEntity)world.getEntity(dragonUuid);

            summonTimer = 20.0 * 2 + random.nextDouble() * 20.0 * (10 - 2);

            double[][] posList = {
                    {0, -2, -3},
                    {-3, -2, 0},
                    {-6, -2, -3},
                    {-3, -2, -6}
            };

            if(ent != null){
                System.out.print(", found dragon");
                if(findEndPortal() != null && ent.getHealth() > 50){
                    System.out.print(", found portal");
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
                System.out.println();

                if(ent.getHealth() < ent.getMaxHealth() / 2 && !phaseActive){
                    phaseActive = true;
                    ent.getDataTracker().set(DragonFight.LOW_HEALTH, true);
                    //EnderDragonSpawnState.SUMMONING_PILLARS.run(world, (EnderDragonFight) (Object)this, );

                    System.out.println("made phase active");

                    tower = EndSpikeFeature.getSpikes((StructureWorldAccess)this.world).size();
                }
            }
        }

        if(phaseActive && tower > 0 && crystalTimer-- < 0){
            tower--;

            crystalTimer = 40;

            EndSpikeFeature.Spike spike = EndSpikeFeature.getSpikes((StructureWorldAccess)this.world).get(tower);
            spike.getHeight();
            world.createExplosion(null, (spike.getCenterX() + 0.5F), spike.getHeight(), (spike.getCenterZ() + 0.5F), 5.0F, Explosion.DestructionType.DESTROY);

            EndCrystalEntity lv4 = (EndCrystalEntity)EntityType.END_CRYSTAL.create((World)world.toServerWorld());
            lv4.refreshPositionAndAngles(spike.getCenterX() + 0.5D, (spike.getHeight() + 1), spike.getCenterZ() + 0.5D, random.nextFloat() * 360.0F, 0.0F);
            world.spawnEntity((Entity)lv4);

            world.setBlockState(new BlockPos(spike.getCenterX(), spike.getHeight(), spike.getCenterZ()), Blocks.BEDROCK.getDefaultState());
        }
    }
}
