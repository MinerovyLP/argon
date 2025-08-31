package dev.lvstrng.argon.module.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lvstrng.argon.event.events.GameRenderListener;
import dev.lvstrng.argon.module.Category;
import dev.lvstrng.argon.module.Module;
import dev.lvstrng.argon.module.modules.client.ClickGUI;
import dev.lvstrng.argon.module.setting.BooleanSetting;
import dev.lvstrng.argon.module.setting.ModeSetting;
import dev.lvstrng.argon.module.setting.NumberSetting;
import dev.lvstrng.argon.utils.ColorUtils;
import dev.lvstrng.argon.utils.EncryptedString;
import dev.lvstrng.argon.utils.RenderUtils;
import dev.lvstrng.argon.utils.Utils;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import net.minecraft.util.math.RotationAxis;

import java.awt.*;

public final class PlayerESP extends Module implements GameRenderListener {
	public enum Mode {
		Hitbox, Filled
	}

	public final ModeSetting<Mode> mode = new ModeSetting<>(EncryptedString.of("Mode"), Mode.Filled, Mode.class);
	private final NumberSetting alpha = new NumberSetting(EncryptedString.of("Alpha"), 0, 255, 100, 1);
	private final NumberSetting width = new NumberSetting(EncryptedString.of("Line width"), 1, 10, 1, 1);
	private final BooleanSetting tracers = new BooleanSetting(EncryptedString.of("Tracers"), false)
			.setDescription(EncryptedString.of("Draws a line from your player to the other"));

	public PlayerESP() {
		super(EncryptedString.of("Player ESP"),
				EncryptedString.of("Renders players through walls"),
				-1,
				Category.RENDER);
		addSettings(alpha, mode, width, tracers);
	}

	@Override
	public void onEnable() {
		eventManager.add(GameRenderListener.class, this);
		super.onEnable();
	}

	@Override
	public void onDisable() {
		eventManager.remove(GameRenderListener.class, this);
		super.onDisable();
	}

	@Override
	public void onGameRender(GameRenderEvent event) {
		for (PlayerEntity player : mc.world.getPlayers()) {
			if (mode.isMode(Mode.Filled)) {
				if (player != mc.player) {
					Camera cam = mc.getBlockEntityRenderDispatcher().camera;
					if (cam != null) {
						event.matrices.push();
						Vec3d vec = cam.getPos();
						
            			event.matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(cam.getPitch()));
            			event.matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(cam.getYaw() + 180F));
						event.matrices.translate(-vec.x, -vec.y, -vec.z);
					}

					//double xPos = MathHelper.lerp(RenderTickCounter.ONE.getTickDelta(true), player.prevX, player.getX());
					//double yPos = MathHelper.lerp(RenderTickCounter.ONE.getTickDelta(true), player.prevY, player.getY());
					//double zPos = MathHelper.lerp(RenderTickCounter.ONE.getTickDelta(true), player.prevZ, player.getZ());
                    double xPos = player.getLerpedPos(mc.getRenderTickCounter().getTickDelta(true)).x;
                    double yPos = player.getLerpedPos(mc.getRenderTickCounter().getTickDelta(true)).y;
                    double zPos = player.getLerpedPos(mc.getRenderTickCounter().getTickDelta(true)).z;

					RenderUtils.renderFilledBox(
							event.matrices,
							(float) xPos - player.getWidth() / 2,
							(float) yPos,
							(float) zPos - player.getWidth() / 2,
							(float) xPos + player.getWidth() / 2,
							(float) yPos + player.getHeight(),
							(float) zPos + player.getWidth() / 2,
							Utils.getMainColor(alpha.getValueInt(), 1).brighter());

					if (tracers.getValue())
						RenderUtils.renderLine(event.matrices, Utils.getMainColor(255, 1), mc.crosshairTarget.getPos(), player.getLerpedPos(mc.getRenderTickCounter().getTickDelta(true)));

					event.matrices.pop();
				}
			} else if (mode.isMode(Mode.Hitbox)) {
				if (player != mc.player) {
					var cam = mc.getBlockEntityRenderDispatcher().camera;
					event.matrices.push();
            		event.matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(cam.getPitch()));
            		event.matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(cam.getYaw() + 180F));
					renderOutline(player, getColor(alpha.getValueInt()), event.matrices);

					if (tracers.getValue())
						RenderUtils.renderLine(event.matrices, Utils.getMainColor(255, 1), mc.crosshairTarget.getPos(), player.getLerpedPos(mc.getRenderTickCounter().getTickDelta(true)));

					event.matrices.pop();
				}
			}
		}
	}

	private void renderOutline(PlayerEntity e, Color color, MatrixStack matrices) {
        Camera cam = mc.gameRenderer.getCamera();
        Vec3d camPos = cam.getPos();
        Vec3d playerPos = e.getLerpedPos(mc.getRenderTickCounter().getTickDelta(true)).subtract(camPos);

        float x = (float) playerPos.x;
        float y = (float) playerPos.y;
        float z = (float) playerPos.z;

        float halfWidth = e.getWidth() / 2f;
        float height = e.getHeight();

        float minX = x - halfWidth;
        float maxX = x + halfWidth;
        float minZ = z - halfWidth;
        float maxZ = z + halfWidth;
        float minY = y;
        float maxY = y + height;

        float distance = (float) camPos.distanceTo(e.getPos());
        float pixelScale = getPixelWorldScale(distance);
        float thickness = width.getValueFloat() * pixelScale;

        int argb = color.getRGB();

        // Vertical edges
        RenderUtils.renderQuadAbs(matrices, minX - thickness, minY, minX + thickness, maxY, argb);
        RenderUtils.renderQuadAbs(matrices, maxX - thickness, minY, maxX + thickness, maxY, argb);
        RenderUtils.renderQuadAbs(matrices, minX, minY, minX + thickness, maxY, argb);
        RenderUtils.renderQuadAbs(matrices, maxX - thickness, minY, maxX, maxY, argb);

        // Top edges
        RenderUtils.renderQuadAbs(matrices, minX, maxY - thickness, maxX, maxY + thickness, argb);
        RenderUtils.renderQuadAbs(matrices, minX - thickness, maxY - thickness, minX + thickness, maxY + thickness, argb);
        RenderUtils.renderQuadAbs(matrices, maxX - thickness, maxY - thickness, maxX + thickness, maxY + thickness, argb);

        // Bottom edges
        RenderUtils.renderQuadAbs(matrices, minX, minY - thickness, maxX, minY + thickness, argb);
        RenderUtils.renderQuadAbs(matrices, minX - thickness, minY - thickness, minX + thickness, minY + thickness, argb);
        RenderUtils.renderQuadAbs(matrices, maxX - thickness, minY - thickness, maxX + thickness, minY + thickness, argb);
    }

    private float getPixelWorldScale(float distance) {
        double fov = mc.options.getFov().getValue();
        double viewportHeight = mc.getWindow().getFramebufferHeight();
        return (float) (2 * distance * Math.tan(Math.toRadians(fov / 2)) / viewportHeight);
    }

	private Color getColor(int alpha) {
		int red = ClickGUI.red.getValueInt();
		int green = ClickGUI.green.getValueInt();
		int blue = ClickGUI.blue.getValueInt();

		if (ClickGUI.rainbow.getValue())
			return ColorUtils.getBreathingRGBColor(1, alpha);
		else
			return new Color(red, green, blue, alpha);
	}
}
