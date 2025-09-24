package net.raphimc.immediatelyfast.module.modules.client;

import net.raphimc.immediatelyfast.Argon;
import net.raphimc.immediatelyfast.event.events.PacketReceiveListener;
import net.raphimc.immediatelyfast.gui.ClickGui;
import net.raphimc.immediatelyfast.module.Category;
import net.raphimc.immediatelyfast.module.Module;
import net.raphimc.immediatelyfast.module.setting.BooleanSetting;
import net.raphimc.immediatelyfast.module.setting.MinMaxSetting;
import net.raphimc.immediatelyfast.module.setting.ModeSetting;
import net.raphimc.immediatelyfast.module.setting.NumberSetting;
import net.raphimc.immediatelyfast.utils.EncryptedString;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import org.lwjgl.glfw.GLFW;

public final class ClickGUI extends Module implements PacketReceiveListener {
	public static final NumberSetting red = new NumberSetting(EncryptedString.of("Red"), 0, 255, 255, 1);
	public static final NumberSetting green = new NumberSetting(EncryptedString.of("Green"), 0, 255, 0, 1);
	public static final NumberSetting blue = new NumberSetting(EncryptedString.of("Blue"), 0, 255, 50, 1);

	public static final NumberSetting alphaWindow = new NumberSetting(EncryptedString.of("Window Alpha"), 0, 255, 170, 1);

	public static final BooleanSetting breathing = new BooleanSetting(EncryptedString.of("Breathing"), true)
			.setDescription(EncryptedString.of("Color breathing effect (only with rainbow off)"));
	public static final BooleanSetting rainbow = new BooleanSetting(EncryptedString.of("Rainbow"), true)
			.setDescription(EncryptedString.of("Enables LGBTQ mode"));

	public static final BooleanSetting background = new BooleanSetting(EncryptedString.of("Background"), false).setDescription(EncryptedString.of("Renders the background of the Click Gui"));
	public static final BooleanSetting customFont = new BooleanSetting(EncryptedString.of("Custom Font"), true);

	private final BooleanSetting preventClose = new BooleanSetting(EncryptedString.of("Prevent Close"), true)
			.setDescription(EncryptedString.of("For servers with freeze plugins that don't let you open the GUI"));

	public static final NumberSetting roundQuads = new NumberSetting(EncryptedString.of("Roundness"), 1, 10, 5, 1);
	public static final ModeSetting<AnimationMode> animationMode = new ModeSetting<>(EncryptedString.of("Animations"), AnimationMode.Normal, AnimationMode.class);
	public static final BooleanSetting antiAliasing = new BooleanSetting(EncryptedString.of("MSAA"), true)
			.setDescription(EncryptedString.of("Anti Aliasing | This can impact performance if you're using tracers but gives them a smoother look |"));

	public enum AnimationMode {
		Normal, Positive, Off;
	}

	public ClickGUI() {
		super(EncryptedString.of("Argon"),
				EncryptedString.of("Settings for the client"),
				GLFW.GLFW_KEY_RIGHT_SHIFT,
				Category.CLIENT);

		addSettings(red, green, blue, alphaWindow, breathing, rainbow, background, preventClose, roundQuads, animationMode, antiAliasing);
	}

	@Override
	public void onEnable() {
		eventManager.add(PacketReceiveListener.class, this);
		Argon.INSTANCE.previousScreen = mc.currentScreen;

		if (Argon.INSTANCE.clickGui != null) {
			mc.setScreenAndRender(Argon.INSTANCE.clickGui);
		} else if (mc.currentScreen instanceof InventoryScreen) {
			Argon.INSTANCE.guiInitialized = true;
		}

		super.onEnable();
	}

	@Override
	public void onDisable() {
		eventManager.remove(PacketReceiveListener.class, this);

		if (mc.currentScreen instanceof ClickGui) {
			Argon.INSTANCE.clickGui.close();
			mc.setScreenAndRender(Argon.INSTANCE.previousScreen);
			Argon.INSTANCE.clickGui.onGuiClose();
		} else if (mc.currentScreen instanceof InventoryScreen) {
			Argon.INSTANCE.guiInitialized = false;
		}

		super.onDisable();
	}


	@Override
	public void onPacketReceive(PacketReceiveEvent event) {
		if (Argon.INSTANCE.guiInitialized) {
			if (event.packet instanceof OpenScreenS2CPacket) {
				if (preventClose.getValue())
					event.cancel();
			}
		}
	}
}