package net.raphimc.immediatelyfast.event.events;

import net.raphimc.immediatelyfast.event.Event;
import net.raphimc.immediatelyfast.event.Listener;

import java.util.ArrayList;

public interface PlayerTickListener extends Listener {
	void onPlayerTick();

	class PlayerTickEvent extends Event<PlayerTickListener> {
		@Override
		public void fire(ArrayList<PlayerTickListener> listeners) {
			listeners.forEach(PlayerTickListener::onPlayerTick);
		}

		@Override
		public Class<PlayerTickListener> getListenerType() {
			return PlayerTickListener.class;
		}
	}
}
