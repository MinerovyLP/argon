package net.raphimc.immediatelyfast.gui.components.settings;

import net.raphimc.immediatelyfast.gui.components.ModuleButton;
import net.raphimc.immediatelyfast.module.setting.BooleanSetting;
import net.raphimc.immediatelyfast.module.setting.Setting;
import net.raphimc.immediatelyfast.utils.ColorUtils;
import net.raphimc.immediatelyfast.utils.TextRenderer;
import net.raphimc.immediatelyfast.utils.Utils;
import net.raphimc.immediatelyfast.utils.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public final class CheckBox extends RenderableSetting {
	private final BooleanSetting setting;
	private Color currentAlpha;

	public CheckBox(ModuleButton parent, Setting<?> setting, int offset) {
		super(parent, setting, offset);
		this.setting = (BooleanSetting) setting;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		int nameOffset = parentX() + 31;
		CharSequence chars = setting.getName();

		TextRenderer.drawString(chars, context, nameOffset, (parentY() + parentOffset() + offset) + 9, new Color(245, 245, 245, 255).getRGB());

		RenderUtils.renderQuadGradient(context.getMatrices(), (parentX() + 5), (parentY() + parentOffset() + offset) + 5, (parentX() + 25), (parentY() + parentOffset() + offset + parentHeight()) - 5, Utils.getMainColor(255, parent.settings.indexOf(this)).getRGB(), Utils.getMainColor(255, parent.settings.indexOf(this) + 1).getRGB());
		RenderUtils.renderQuadAbs(context.getMatrices(), (parentX() + 7), (parentY() + parentOffset() + offset) + 7, (parentX() + 23), (parentY() + parentOffset() + offset + parentHeight()) - 7, Color.darkGray.getRGB());
		RenderUtils.renderQuadGradient(context.getMatrices(), (parentX() + 9), (parentY() + parentOffset() + offset) + 9, (parentX() + 21), (parentY() + parentOffset() + offset + parentHeight()) - 9, setting.getValue() ? Utils.getMainColor(255, parent.settings.indexOf(this)).getRGB() : Color.darkGray.getRGB(), setting.getValue() ? Utils.getMainColor(255, parent.settings.indexOf(this) + 1).getRGB() : Color.darkGray.getRGB());

		if (!parent.parent.dragging) {
			int toHoverAlpha = isHovered(mouseX, mouseY) ? 15 : 0;

			if (currentAlpha == null)
				currentAlpha = new Color(255, 255, 255, toHoverAlpha);
			else currentAlpha = new Color(255, 255, 255, currentAlpha.getAlpha());

			if (currentAlpha.getAlpha() != toHoverAlpha)
				currentAlpha = ColorUtils.smoothAlphaTransition(0.05F, toHoverAlpha, currentAlpha);

			RenderUtils.renderQuadAbs(context.getMatrices(), parentX(), parentY() + parentOffset() + offset, parentX() + parentWidth(), parentY() + parentOffset() + offset + parentHeight(), currentAlpha.getRGB());
		}
	}

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		if(mouseOver && parent.extended) {
			if(keyCode == GLFW.GLFW_KEY_BACKSPACE)
				setting.setValue(setting.getOriginalValue());
		}

		super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (isHovered(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
			setting.toggle();

		super.mouseClicked(mouseX, mouseY, button);
	}
}
