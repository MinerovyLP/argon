package net.raphimc.immediatelyfast.event.events;

import net.raphimc.immediatelyfast.event.Event;
import net.raphimc.immediatelyfast.event.Listener;

import java.util.ArrayList;

public interface MovementPacketListener extends Listener {
	void onSendMovementPackets();

	class MovementPacketEvent extends Event<MovementPacketListener> {
		@Override
		public void fire(ArrayList<MovementPacketListener> listeners) {
			listeners.forEach(MovementPacketListener::onSendMovementPackets);
		}

		@Override
		public Class<MovementPacketListener> getListenerType() {
			return MovementPacketListener.class;
		}
	}
}
