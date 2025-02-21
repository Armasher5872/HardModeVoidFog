package net.phazoganon.hardmodevoidfog.event;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.phazoganon.hardmodevoidfog.HardModeVoidFog;

@EventBusSubscriber(modid = HardModeVoidFog.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class RenderFogEvent {
    private static boolean active;
    private static final float[] colors = new float[4];
    private static float color = 0F;
    private static float fog = 1F;
    private static float distance = 3F;
    @SubscribeEvent
    private static void onFogRender(ViewportEvent.RenderFog renderFog) {
        if (active || fog < 1F) {
            float f = 6F;
            f = f >= renderFog.getFarPlaneDistance() ? renderFog.getFarPlaneDistance() : Mth.clampedLerp(f, renderFog.getFarPlaneDistance(), fog);
            float shift = (float) ((active ? (fog > 0.5F ? 0.005F : 0.001F) : (fog > 0.25F ? 0.01F : 0.001F))*renderFog.getPartialTick());
            if (active) {
                fog -= shift;
            }
            else {
                fog += shift;
            }
            fog = Mth.clamp(fog, 0F, 1F);
            renderFog.setNearPlaneDistance(distance);
            renderFog.setFarPlaneDistance(f);
            RenderSystem.setShaderFog(new FogParameters(distance, f, FogShape.SPHERE, colors[0], colors[1], colors[2], colors[3]));
            renderFog.setCanceled(true);
        }
    }
    @SubscribeEvent
    private static void onComputeFogColor(ViewportEvent.ComputeFogColor computeFogColor) {
        if (active || color > 0F) {
            final float[] realColor = {computeFogColor.getRed(), computeFogColor.getGreen(), computeFogColor.getBlue(), Mth.clamp((distance)/20, 0.5F, 1.0F)};
            for (int i = 0; i < 4; i++) {
                final float real = realColor[i];
                final float c = 0;
                colors[i] = real == c ? c : Mth.clampedLerp(real, c, color);
            }
            if (active) {
                color += (float) (0.1F*computeFogColor.getPartialTick());
            }
            else {
                color -= (float) (0.005F*computeFogColor.getPartialTick());
            }
            color = Mth.clamp(color, 0F, 1F);
            computeFogColor.setRed(colors[0]);
            computeFogColor.setGreen(colors[1]);
            computeFogColor.setBlue(colors[2]);
        }
    }
    @SubscribeEvent
    private static void onPlayerTickPre(PlayerTickEvent.Pre playerTickEvent) {
        if (playerTickEvent.getEntity() != Minecraft.getInstance().player) {
            return;
        }
        double getY = playerTickEvent.getEntity().getY();
        double minY = playerTickEvent.getEntity().level().dimensionType().minY();
        if (getY <= minY+20.0D) {
            active = !(playerTickEvent.getEntity().hasEffect(MobEffects.NIGHT_VISION)) && playerTickEvent.getEntity().level().getDifficulty() == Difficulty.HARD;
            distance = (float) getY+62;
        }
        else {
            active = false;
        }
    }
}
