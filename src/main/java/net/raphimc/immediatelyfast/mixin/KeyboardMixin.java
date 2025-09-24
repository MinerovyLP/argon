package net.raphimc.immediatelyfast.mixin;

import net.raphimc.immediatelyfast.Argon;
import net.raphimc.immediatelyfast.event.EventManager;
import net.raphimc.immediatelyfast.event.events.ButtonListener;
import net.raphimc.immediatelyfast.module.Module;
import net.raphimc.immediatelyfast.module.modules.client.SelfDestruct;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "onKey", at = @At("HEAD"))
	private void onPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		EventManager.fire(new ButtonListener.ButtonEvent(key, window, action));
	}
}
