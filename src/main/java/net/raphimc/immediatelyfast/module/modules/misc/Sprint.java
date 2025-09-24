package net.raphimc.immediatelyfast.module.modules.misc;

import net.raphimc.immediatelyfast.event.events.TickListener;
import net.raphimc.immediatelyfast.module.Category;
import net.raphimc.immediatelyfast.module.Module;
import net.raphimc.immediatelyfast.utils.EncryptedString;

public final class Sprint extends Module implements TickListener {
    public Sprint() {
        super(EncryptedString.of("Sprint"), EncryptedString.of("Keeps you sprinting at all times"), -1, Category.MISC);
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
        mc.player.setSprinting(mc.player.input.movementForward > 0);
    }
}
