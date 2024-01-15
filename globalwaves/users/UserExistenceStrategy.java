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

    /**
     * Gets the existence strategy based on the type of user
     * @param context in which the strategy is set
     * @param type of the strategy
     */
    static void getExistenceStrategy(UserExistenceContext context, String type) {
        switch (type) {
            case "listener" -> context.setStrategy(new ListenerExistenceStrategy());
            case "artist" -> context.setStrategy(new ArtistExistenceStrategy());
            case "host" -> context.setStrategy(new HostExistenceStrategy());
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}
