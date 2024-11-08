package globalwaves.users.listener;

import fileio.input.CommandInput;
import globalwaves.users.UserExistenceStrategy;
import output.Output;

import java.util.Map;

public class ListenerExistenceStrategy implements UserExistenceStrategy {
    /**
     * Checks if the listener exists
     */
    @Override
    public boolean exists(final CommandInput command, final Output output) {
        Map<String, Listener> listeners = globalwaves.GlobalWaves.getInstance().getListeners();
        if (!listeners.containsKey(command.getUsername())) {
            output.setMessage("The username " + command.getUsername() + " doesn't exist.");
            return false;
        }
        return true;
    }
}
