package globalwaves.users.artist;

import fileio.input.CommandInput;
import globalwaves.GlobalWaves;
import globalwaves.users.UserExistenceStrategy;
import globalwaves.users.host.Host;
import output.Output;

import java.util.Map;

public class ArtistExistenceStrategy implements UserExistenceStrategy {
    @Override
    public boolean exists(CommandInput command, Output output) {
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
