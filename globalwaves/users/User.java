package globalwaves.users;

import fileio.input.CommandInput;
import lombok.Getter;
import lombok.Setter;
import output.Output;

/**
 * This class is the base class for all types of users (listeners, hosts, artists)
 */
@Getter
@Setter
public abstract class User {
    private String username;
    private int age;
    private String city;
    /**
     * Determines if the user is interacting with the app, with checks varying by user role:
     *
     * - Normal listeners: Verifies if a playlist they have is loaded by others.
     * - Hosts: Checks for podcasts loaded by listeners or selection of their host page.
     * - Artists: Assesses if a song/album they have is loaded by a listener or if their
     *   artist page is selected.
     *
     * @return boolean indicating active interaction with the app in the described contexts.
     */
    public abstract boolean isInteracting();
    public abstract Output wrapped(CommandInput command);
}
