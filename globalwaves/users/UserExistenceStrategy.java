package globalwaves.users;

import fileio.input.CommandInput;
import globalwaves.users.artist.ArtistExistenceStrategy;
import globalwaves.users.host.HostExistenceStrategy;
import globalwaves.users.listener.ListenerExistenceStrategy;
import output.Output;

public interface UserExistenceStrategy {
    /**
     * Checks if the user exists within the app's database
     */
    boolean exists(CommandInput command, Output output);
    static void getExistenceStrategy(UserExistenceContext context, String type) {
        switch (type) {
            case "listener" -> context.setStrategy(new ListenerExistenceStrategy());
            case "artist" -> context.setStrategy(new ArtistExistenceStrategy());
            case "host" -> context.setStrategy(new HostExistenceStrategy());
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}
