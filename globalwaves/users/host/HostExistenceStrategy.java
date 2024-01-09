package globalwaves.users.host;

import fileio.input.CommandInput;
import globalwaves.GlobalWaves;
import globalwaves.users.UserExistenceStrategy;
import globalwaves.users.artist.Artist;
import output.Output;

import java.util.Map;

public class HostExistenceStrategy implements UserExistenceStrategy {
    @Override
    public boolean exists(CommandInput command, Output output) {
        Map<String, Artist> artists = GlobalWaves.getInstance().getArtists();
        Map<String, Host> hosts = GlobalWaves.getInstance().getHosts();
        if (!GlobalWaves.getInstance().getUsers().containsKey(command.getUsername())
                && !artists.containsKey(command.getUsername())
                && !hosts.containsKey(command.getUsername())) {
            output.setMessage("The username " + command.getUsername() + " doesn't exist.");
            return false;
        } else if ((GlobalWaves.getInstance().getUsers().containsKey(command.getUsername())
                || artists.containsKey(command.getUsername()))
                && !hosts.containsKey(command.getUsername())) {
            output.setMessage(command.getUsername() + " is not a host.");
            return false;
        }
        return true;
    }
}
