package net.raphimc.immediatelyfast.module.modules.misc;

import net.raphimc.immediatelyfast.event.events.TickListener;
import net.raphimc.immediatelyfast.module.Category;
import net.raphimc.immediatelyfast.module.Module;
import net.raphimc.immediatelyfast.module.setting.BooleanSetting;
import net.raphimc.immediatelyfast.module.setting.KeybindSetting;
import net.raphimc.immediatelyfast.module.setting.NumberSetting;
import net.raphimc.immediatelyfast.utils.EncryptedString;
import net.raphimc.immediatelyfast.utils.InventoryUtils;
import net.raphimc.immediatelyfast.utils.KeyUtils;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public final class KeyPearl extends Module implements TickListener {
    private final KeybindSetting activateKey = new KeybindSetting(EncryptedString.of("Activate Key"), -1, false);
    private final NumberSetting delay = new NumberSetting(EncryptedString.of("Delay"), 0, 20, 0, 1);
    private final BooleanSetting switchBack = new BooleanSetting(EncryptedString.of("Switch Back"), true);
    private final NumberSetting switchDelay = new NumberSetting(EncryptedString.of("Switch Delay"), 0, 20, 0, 1)
            .setDescription(EncryptedString.of("Delay after throwing pearl before switching back"));

    private boolean active, hasActivated;
    private int clock, previousSlot, switchClock;

    public KeyPearl() {
        super(EncryptedString.of("Key Pearl"), EncryptedString.of("Switches to an ender pearl and throws it when you press a bind"), -1, Category.MISC);
        addSettings(activateKey, delay, switchBack, switchDelay);
    }

    @Override
    public void onEnable() {
        eventManager.add(TickListener.class, this);
        reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        eventManager.remove(TickListener.class, this);
        super.onDisable();
    }

    @Override
    public void onTick() {
        if(mc.currentScreen != null)
            return;

        if(KeyUtils.isKeyPressed(activateKey.getKey())) {
            active = true;
        }

        if(active) {
            if(previousSlot == -1)
                previousSlot = mc.player.getInventory().selectedSlot;

            InventoryUtils.selectItemFromHotbar(Items.ENDER_PEARL);

            if(clock < delay.getValueInt()) {
                clock++;
                return;
            }

            if(!hasActivated) {
                ActionResult result = mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                if (result.isAccepted())
                    mc.player.swingHand(Hand.MAIN_HAND);

                hasActivated = true;
            }

            if(switchBack.getValue())
                switchBack();
            else reset();
        }
    }

    private void switchBack() {
        if(switchClock < switchDelay.getValueInt()) {
            switchClock++;
            return;
        }

        InventoryUtils.setInvSlot(previousSlot);
        reset();
    }

    private void reset() {
        previousSlot = -1;
        clock = 0;
        switchClock = 0;
        active = false;
        hasActivated = false;
    }
}
