package net.raphimc.immediatelyfast.module.modules.render;

import net.raphimc.immediatelyfast.module.Category;
import net.raphimc.immediatelyfast.module.Module;
import net.raphimc.immediatelyfast.utils.EncryptedString;

public final class NoBounce extends Module {
	public NoBounce() {
		super(EncryptedString.of("No Bounce"),
				EncryptedString.of("Removes the crystal bounce"),
				-1,
				Category.RENDER);
	}
}
