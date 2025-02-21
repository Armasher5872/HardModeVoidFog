package net.phazoganon.hardmodevoidfog.mixin;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow @Nullable public ClientLevel level;
    @Shadow
    @Nullable public LocalPlayer player;
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;animateTick(III)V"))
    private void tick(CallbackInfo ci) {
        voidFog(level, player);
    }
    @Unique
    private void voidFog(ClientLevel level, AbstractClientPlayer player) {
        byte var4 = 16;
        for (int var6 = 0; var6 < 1000; var6++) {
            int x = Mth.floor(player.getX())+level.random.nextInt(var4)-level.random.nextInt(var4);
            int y = Mth.floor(player.getY())+level.random.nextInt(var4)-level.random.nextInt(var4);
            int z = Mth.floor(player.getZ())+level.random.nextInt(var4)-level.random.nextInt(var4);
            BlockPos pos = new BlockPos(x, y, z);
            BlockState blockState = level.getBlockState(pos);
            if (blockState.isAir()) {
                int bedrockLevel = level.dimensionType().minY();
                if (level.random.nextInt(bedrockLevel, bedrockLevel+8) > y && level.getLevelData().getDifficulty() == Difficulty.HARD) {
                    level.addParticle(ParticleTypes.ASH, (float)x+level.random.nextFloat(), (float)y+level.random.nextFloat(), (float)z+level.random.nextFloat(), 0.0D, 0.0D, 0.0D);
                }
            }
            else {
                blockState.getBlock().animateTick(blockState, level, pos, level.random);
            }
        }
    }
}