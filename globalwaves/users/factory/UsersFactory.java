package globalwaves.users.factory;

import fileio.input.CommandInput;
import globalwaves.users.User;
import globalwaves.users.listener.Listener;
import globalwaves.users.artist.Artist;
import globalwaves.users.host.Host;
/**
 * Factory method implementation for creating instances of users
 */
public final class UsersFactory {
    /**
     * Utility classes shouldn't have a public or default constructor
     */
    private UsersFactory() {
        throw new IllegalStateException("Utility class");
    }
    /**
     * @param command contains the user's information
     * @return the created user
     */
    public static User createUser(final CommandInput command) {
        String userType = command.getType();
        return switch (userType) {
            case "user" -> new Listener(command);
            case "artist" -> new Artist(command);
            case "host" -> new Host(command);
            default -> {
                System.out.println("Invalid user type");
                yield null;
            }
        };
    }
}
