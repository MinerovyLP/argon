package net.raphimc.immediatelyfast.module.modules.misc;

import net.raphimc.immediatelyfast.event.events.ItemUseListener;
import net.raphimc.immediatelyfast.event.events.TickListener;
import net.raphimc.immediatelyfast.module.Category;
import net.raphimc.immediatelyfast.module.Module;
import net.raphimc.immediatelyfast.module.setting.BooleanSetting;
import net.raphimc.immediatelyfast.module.setting.NumberSetting;
import net.raphimc.immediatelyfast.utils.EncryptedString;
import net.raphimc.immediatelyfast.utils.MathUtils;
import net.raphimc.immediatelyfast.utils.MouseSimulation;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public final class AutoXP extends Module implements TickListener, ItemUseListener {
	private final NumberSetting delay = new NumberSetting(EncryptedString.of("Delay"), 0, 20, 0, 1);
	private final NumberSetting chance = new NumberSetting(EncryptedString.of("Chance"), 0, 100, 100, 1)
			.setDescription(EncryptedString.of("Randomization"));
	private final BooleanSetting clickSimulation = new BooleanSetting(EncryptedString.of("Click Simulation"), false)
			.setDescription(EncryptedString.of("Makes the CPS hud think you're legit"));
	int clock;

	public AutoXP() {
		super(EncryptedString.of("Auto XP"),
				EncryptedString.of("Automatically throws XP bottles for you"),
				-1,
				Category.MISC);
		addSettings(delay, chance, clickSimulation);
	}

	@Override
	public void onEnable() {
		eventManager.add(TickListener.class, this);
		eventManager.add(ItemUseListener.class, this);

		clock = 0;
		super.onEnable();
	}

	@Override
	public void onDisable() {
		eventManager.remove(TickListener.class, this);
		eventManager.remove(ItemUseListener.class, this);
		super.onDisable();
	}

	@Override
	public void onTick() {
		if (mc.currentScreen != null)
			return;

		boolean dontThrow = clock != 0;

		int randomInt = MathUtils.randomInt(1, 100);

		if (mc.player.getMainHandStack().getItem() != Items.EXPERIENCE_BOTTLE)
			return;

		if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) != GLFW.GLFW_PRESS)
			return;

		if (dontThrow)
			clock--;

		if (!dontThrow && randomInt <= chance.getValueInt()) {
			if (clickSimulation.getValue())
				MouseSimulation.mouseClick(GLFW.GLFW_MOUSE_BUTTON_RIGHT);

			//ActionResult result = mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
			//if (result.isAccepted()) mc.player.swingHand(Hand.MAIN_HAND);
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);

			clock = delay.getValueInt();
		}
	}

	@Override
	public void onItemUse(ItemUseEvent event) {
		if (mc.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE) {
			event.cancel();
		}
	}

}
