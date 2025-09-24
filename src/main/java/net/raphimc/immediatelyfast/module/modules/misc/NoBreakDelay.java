package net.raphimc.immediatelyfast.module.modules.misc;

import net.raphimc.immediatelyfast.module.Category;
import net.raphimc.immediatelyfast.module.Module;
import net.raphimc.immediatelyfast.utils.EncryptedString;

public final class NoBreakDelay extends Module {
	public NoBreakDelay() {
		super(EncryptedString.of("No Break Delay"),
				EncryptedString.of("Removes the break delay from mining blocks"),
				-1,
				Category.MISC);
	}
}
