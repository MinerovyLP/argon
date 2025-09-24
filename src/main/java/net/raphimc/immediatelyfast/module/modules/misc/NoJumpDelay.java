package net.raphimc.immediatelyfast.module.modules.misc;

import net.raphimc.immediatelyfast.event.events.TickListener;
import net.raphimc.immediatelyfast.module.Category;
import net.raphimc.immediatelyfast.module.Module;
import net.raphimc.immediatelyfast.utils.EncryptedString;
import org.lwjgl.glfw.GLFW;

public final class NoJumpDelay extends Module implements TickListener {
	public NoJumpDelay() {
		super(EncryptedString.of("No Jump Delay"),
				EncryptedString.of("Lets you jump faster, removing the delay"),
				-1,
				Category.MISC);
	}

	@Override
	public void onEnable() {
		eventManager.add(TickListener.class, this);
		super.onEnable();
	}

	@Override
	public void onDisable() {
		eventManager.remove(TickListener.class, this);
		super.onDisable();
	}

	@Override
	public void onTick() {
		if (mc.currentScreen != null)
			return;

		if (!mc.player.isOnGround())
			return;

		if (GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_SPACE) != GLFW.GLFW_PRESS)
			return;

		mc.options.jumpKey.setPressed(false);
		mc.player.jump();
	}
}
