package net.raphimc.immediatelyfast.module;

import net.raphimc.immediatelyfast.Argon;
import net.raphimc.immediatelyfast.event.events.ButtonListener;
import net.raphimc.immediatelyfast.module.modules.client.ClickGUI;
import net.raphimc.immediatelyfast.module.modules.client.Friends;
import net.raphimc.immediatelyfast.module.modules.client.SelfDestruct;
import net.raphimc.immediatelyfast.module.modules.combat.*;
import net.raphimc.immediatelyfast.module.modules.misc.*;
import net.raphimc.immediatelyfast.module.modules.render.*;
import net.raphimc.immediatelyfast.module.setting.KeybindSetting;
import net.raphimc.immediatelyfast.utils.EncryptedString;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager implements ButtonListener {
	private final List<Module> modules = new ArrayList<>();

	public ModuleManager() {
		addModules();
		addKeybinds();
	}

	public void addModules() {
		//Combat
		add(new AimAssist());
		add(new AnchorMacro());
		add(new AutoCrystal());
		add(new AutoDoubleHand());
		add(new AutoHitCrystal());
		add(new AutoInventoryTotem());
		add(new TriggerBot());
		add(new AutoPot());
		add(new AutoPotRefill());
		add(new AutoWTap());
		add(new CrystalOptimizer());
		add(new DoubleAnchor());
		add(new HoverTotem());
		add(new NoMissDelay());
		add(new ShieldDisabler());
		add(new TotemOffhand());
		add(new AutoJumpReset());

		//Misc
		add(new Prevent());
		add(new AutoXP());
		add(new NoJumpDelay());
		add(new PingSpoof());
		add(new FakeLag());
		add(new AutoClicker());
		add(new KeyPearl());
		add(new NoBreakDelay());
		add(new Freecam());
		add(new PackSpoof());
		add(new Sprint());

		//Render
		add(new HUD());
		add(new NoBounce());
		add(new PlayerESP());
		add(new StorageEsp());
		add(new TargetHud());

		//Client
		add(new ClickGUI());
		add(new Friends());
		add(new SelfDestruct());
	}

	public List<Module> getEnabledModules() {
		return modules.stream()
				.filter(Module::isEnabled)
				.toList();
	}


	public List<Module> getModules() {
		return modules;
	}

	public void addKeybinds() {
		Argon.INSTANCE.getEventManager().add(ButtonListener.class, this);

		for (Module module : modules)
			module.addSetting(new KeybindSetting(EncryptedString.of("Keybind"), module.getKey(), true).setDescription(EncryptedString.of("Key to enabled the module")));
	}

	public List<Module> getModulesInCategory(Category category) {
		return modules.stream()
				.filter(module -> module.getCategory() == category)
				.toList();
	}

	@SuppressWarnings("unchecked")
	public <T extends Module> T getModule(Class<T> moduleClass) {
		return (T) modules.stream()
				.filter(moduleClass::isInstance)
				.findFirst()
				.orElse(null);
	}

	public void add(Module module) {
		modules.add(module);
	}

	@Override
	public void onButtonPress(ButtonEvent event) {
		if(!SelfDestruct.destruct) {
			modules.forEach(module -> {
				if(module.getKey() == event.button && event.action == GLFW.GLFW_PRESS)
					module.toggle();
			});
		}
	}
}
