package net.raphimc.immediatelyfast.module.modules.misc;

import net.raphimc.immediatelyfast.event.events.PacketReceiveListener;
import net.raphimc.immediatelyfast.module.Category;
import net.raphimc.immediatelyfast.module.Module;
import net.raphimc.immediatelyfast.module.setting.MinMaxSetting;
import net.raphimc.immediatelyfast.module.setting.NumberSetting;
import net.raphimc.immediatelyfast.utils.EncryptedString;
import net.raphimc.immediatelyfast.utils.MathUtils;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;

public final class PingSpoof extends Module implements PacketReceiveListener {
	private final MinMaxSetting ping = new MinMaxSetting(EncryptedString.of("Ping"), 0, 1000, 1, 0, 600)
			.setDescription(EncryptedString.of("The ping you want to achieve"));

	private int delay;
	public PingSpoof() {
		super(EncryptedString.of("Ping Spoof"),
				EncryptedString.of("Holds back packets making the server think your internet connection is bad."), -1, Category.MISC);
		addSettings(ping);
	}

	@Override
	public void onEnable() {
		eventManager.add(PacketReceiveListener.class, this);

		delay = ping.getRandomValueInt();
		super.onEnable();
	}

	@Override
	public void onDisable() {
		eventManager.remove(PacketReceiveListener.class, this);
		super.onDisable();
	}

	@Override
	public void onPacketReceive(PacketReceiveEvent event) {
		if (event.packet instanceof KeepAliveS2CPacket packet) {
			new Thread(() -> {
				try {
					Thread.sleep(delay);
					mc.getNetworkHandler().getConnection().send(new KeepAliveC2SPacket(packet.getId()));
					delay = ping.getRandomValueInt();
				} catch (InterruptedException ignored) {}
			}).start();

			event.cancel();
		}
	}
}
