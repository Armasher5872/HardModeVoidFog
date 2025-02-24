package net.phazoganon.hardmodevoidfog.event;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.phazoganon.hardmodevoidfog.HardModeVoidFog;

@EventBusSubscriber(modid = HardModeVoidFog.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class RenderFogEvent {
    private static boolean active;
    private static float distance = 3F;
    @SubscribeEvent
    private static void onFogRender(ViewportEvent.RenderFog renderFog) {
        if (active) {
            renderFog.setNearPlaneDistance(distance);
            renderFog.setFarPlaneDistance(distance+10F);
            renderFog.setCanceled(true);
        }
    }
    @SubscribeEvent
    private static void onComputeFogColor(ViewportEvent.ComputeFogColor computeFogColor) {
        if (active) {
            computeFogColor.setRed(0F);
            computeFogColor.setGreen(0F);
            computeFogColor.setBlue(0F);
        }
    }
    @SubscribeEvent
    private static void onPlayerTickPre(PlayerTickEvent.Pre playerTickEvent) {
        if (playerTickEvent.getEntity() != Minecraft.getInstance().player) {
            return;
        }
        Player player = playerTickEvent.getEntity();
        double getY = player.getY();
        double minY = player.level().dimensionType().minY();
        double adjustedY = getY+62;
        BlockPos blockpos = player.blockPosition();
        active = !(playerTickEvent.getEntity().hasEffect(MobEffects.NIGHT_VISION)) && playerTickEvent.getEntity().level().getDifficulty() == Difficulty.HARD && !player.level().canSeeSky(blockpos);
        if (getY <= minY+20.0D) {
            distance = (float) adjustedY;
        }
        else {
            distance = 0.00000954F*(float) Math.pow(adjustedY, 5);
        }
        System.out.println(distance);
    }
}
