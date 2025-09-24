package net.raphimc.immediatelyfast.event.events;

import net.raphimc.immediatelyfast.event.Event;
import net.raphimc.immediatelyfast.event.Listener;

import java.util.ArrayList;

public interface ButtonListener extends Listener {
    void onButtonPress(ButtonEvent event);

    class ButtonEvent extends Event<ButtonListener> {
        public int button, action;
        public long window;

        public ButtonEvent(int button, long window, int action) {
            this.button = button;
            this.window = window;
            this.action = action;
        }

        @Override
        public void fire(ArrayList<ButtonListener> listeners) {
            listeners.forEach(e -> e.onButtonPress(this));
        }

        @Override
        public Class<ButtonListener> getListenerType() {
            return ButtonListener.class;
        }
    }
}
