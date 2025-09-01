package dev.lvstrng.argon.module.modules.misc;

import com.google.common.collect.Queues;
import dev.lvstrng.argon.event.events.PacketReceiveListener;
import dev.lvstrng.argon.event.events.PacketSendListener;
import dev.lvstrng.argon.event.events.PlayerTickListener;
import dev.lvstrng.argon.module.Category;
import dev.lvstrng.argon.module.Module;
import dev.lvstrng.argon.module.setting.BooleanSetting;
import dev.lvstrng.argon.module.setting.MinMaxSetting;
import dev.lvstrng.argon.module.setting.NumberSetting;
import dev.lvstrng.argon.utils.EncryptedString;
import dev.lvstrng.argon.utils.TimerUtils;
import dev.lvstrng.argon.utils.SchedulerUtils;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.Queue;

public final class FakePositionLag extends Module implements PacketSendListener {
    private final SchedulerUtils scheduledutils = new SchedulerUtils();
	private final MinMaxSetting lagDelay = new MinMaxSetting(EncryptedString.of("Lag Delay"), 0, 1000, 1, 100, 200);
	private final BooleanSetting cancelOnElytra = new BooleanSetting(EncryptedString.of("Cancel on Elytra"), false)
			.setDescription(EncryptedString.of("Cancel the lagging effect when you're wearing an elytra"));

	private int delay;
	public FakePositionLag() {
		super(EncryptedString.of("Fake Position Lag"),
				EncryptedString.of("Makes it impossible to aim at you by creating a lagging effect (Move Packets Only)"),
				-1,
				Category.MISC);
		addSettings(lagDelay, cancelOnElytra);
	}

	@Override
	public void onEnable() {
		eventManager.add(PacketSendListener.class, this);
        eventManager.add(PlayerTickListener.class, scheduledutils);
		delay = lagDelay.getRandomValueInt();
		super.onEnable();
	}

	@Override
	public void onDisable() {
		eventManager.remove(PacketSendListener.class, this);
        eventManager.remove(PlayerTickListener.class, scheduledutils);
		super.onDisable();
	}

	@Override
	public void onPacketSend(PacketSendEvent event) {
		if (mc.world == null || mc.player.isDead()) return;
		if (!(event.packet instanceof PlayerMoveC2SPacket)) return;
		if (cancelOnElytra.getValue() && mc.player.getInventory().getArmorStack(2).getItem() == Items.ELYTRA) return;

        delay = lagDelay.getRandomValueInt();
        scheduledutils.schedule(() -> mc.getNetworkHandler().getConnection().send(event.packet), delay);
		event.cancel();
	}
}
