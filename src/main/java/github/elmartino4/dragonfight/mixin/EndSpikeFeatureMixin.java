package github.elmartino4.dragonfight.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(EndSpikeFeature.class)
public class EndSpikeFeatureMixin {
    @Inject(method = "generateSpike", at = @At(value = "INVOKE", target = "net/minecraft/world/gen/feature/EndSpikeFeature$Spike.isGuarded ()Z"))
    private void putShulkers(ServerWorldAccess world, Random random, EndSpikeFeatureConfig config, EndSpikeFeature.Spike spike, CallbackInfo ci){
        if(random.nextBoolean()){

            int radius = spike.getRadius() + 1;

            BlockPos[] posArr = {
                    new BlockPos(spike.getCenterX() + radius, spike.getHeight() - 1, spike.getCenterZ()),
                    new BlockPos(spike.getCenterX() - radius, spike.getHeight() - 1, spike.getCenterZ()),
                    new BlockPos(spike.getCenterX(), spike.getHeight() - 1, spike.getCenterZ() + radius),
                    new BlockPos(spike.getCenterX(), spike.getHeight() - 1, spike.getCenterZ() - radius)};

            for (BlockPos pos : posArr) {
                Entity shulker = new ShulkerEntity(EntityType.SHULKER, world.toServerWorld());
                shulker.setPosition(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
                shulker.inanimate = true;

                world.spawnEntity(shulker);
            }
        }
    }
}
