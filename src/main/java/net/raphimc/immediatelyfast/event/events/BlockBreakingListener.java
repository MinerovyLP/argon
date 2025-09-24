package net.raphimc.immediatelyfast.event.events;

import net.raphimc.immediatelyfast.event.CancellableEvent;
import net.raphimc.immediatelyfast.event.Listener;

import java.util.ArrayList;

public interface BlockBreakingListener extends Listener {
	void onBlockBreaking(BlockBreakingEvent event);

	class BlockBreakingEvent extends CancellableEvent<BlockBreakingListener> {

		@Override
		public void fire(ArrayList<BlockBreakingListener> listeners) {
			listeners.forEach(e -> e.onBlockBreaking(this));
		}

		@Override
		public Class<BlockBreakingListener> getListenerType() {
			return BlockBreakingListener.class;
		}
	}
}
