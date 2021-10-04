package github.elmartino4.dragonfight.mixin;

import github.elmartino4.dragonfight.util.EndermiteEntityAccess;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
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

@Mixin(EnderDragonFight.class)
public abstract class DragonFightMixin {
    @Shadow @Final private ServerWorld world;

    @Shadow @Nullable protected abstract BlockPattern.Result findEndPortal();

    @Unique double summonTimer;
    @Unique Random random = new Random();

    @Inject(method = "tick", at = @At("HEAD"))
    private void doEndermiteTick(CallbackInfo ci){

        if(summonTimer-- < 0){
            summonTimer = 20.0 * 2 + random.nextDouble() * 20.0 * (10 - 2);

            int[][] posList = {
                    {0, -4, -3},
                    {-3, -4, 0},
                    {-7, -4, -3},
                    {-3, -4, -7}
            };

            if(findEndPortal() != null)
                for (int i = 0; i < 4; i++) {
                    EndermiteEntity entity = new EndermiteEntity(EntityType.ENDERMITE, world);
                    ((EndermiteEntityAccess)entity).setExplosive();
                    entity.setPosition(Vec3d.ofCenter(findEndPortal().getFrontTopLeft().add(posList[i][0], posList[i][1], posList[i][2])));
                    world.spawnEntityAndPassengers(entity);

                    //System.out.println("tried summon");
                }
        }
    }
}
