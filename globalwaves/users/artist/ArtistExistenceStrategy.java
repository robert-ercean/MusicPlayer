package globalwaves.users.artist;

import fileio.input.CommandInput;
import globalwaves.GlobalWaves;
import globalwaves.users.UserExistenceStrategy;
import globalwaves.users.host.Host;
import output.Output;

import java.util.Map;

public class ArtistExistenceStrategy implements UserExistenceStrategy {
    /**
     * artist specific existence strategy
     * used for checking if the user exists and if it is an artist
     * and setting the output string message accordingly
     */
    @Override
    public boolean exists(final CommandInput command, final Output output) {
        Map<String, Artist> artists = GlobalWaves.getInstance().getArtists();
        Map<String, Host> hosts = GlobalWaves.getInstance().getHosts();
        if (!GlobalWaves.getInstance().getListeners().containsKey(command.getUsername())
                && !hosts.containsKey(command.getUsername())
                && !artists.containsKey(command.getUsername())) {
            output.setMessage("The username " + command.getUsername() + " doesn't exist.");
            return false;
        } else if ((GlobalWaves.getInstance().getListeners().containsKey(command.getUsername())
                || hosts.containsKey(command.getUsername()))
                && !artists.containsKey(command.getUsername())) {
            output.setMessage(command.getUsername() + " is not an artist.");
            return false;
        }
        return true;
    }
}
