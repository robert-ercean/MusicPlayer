package globalwaves.admin.statistics;

import fileio.input.CommandInput;
import globalwaves.GlobalWaves;
import globalwaves.admin.AdminCommand;
import globalwaves.users.artist.Artist;
import globalwaves.users.host.Host;
import globalwaves.users.listener.Listener;
import output.Output;

import java.util.Map;

import static globalwaves.users.listener.player.Player.updatePlayerTime;

public final class SwitchConnectionStatus implements AdminCommand {
    @Override
    public Output execute(final CommandInput command) {
        updatePlayerTime(GlobalWaves.getInstance().getUsers().
                get(command.getUsername()), command.getTimestamp());
        Map<String, Listener> users = GlobalWaves.getInstance().getUsers();
        Map<String, Artist> artists = GlobalWaves.getInstance().getArtists();
        Map<String, Host> hosts = GlobalWaves.getInstance().getHosts();

        Output output = Output.getOutputTemplate(command);
        if (artists.containsKey(command.getUsername())
                || hosts.containsKey(command.getUsername())) {
            output.setMessage(command.getUsername() + " is not a normal user.");
            return output;
        } else if (!users.containsKey(command.getUsername())) {
            output.setMessage("The username " + command.getUsername() + " doesn't exist.");
            return output;
        }
        Listener user = users.get(command.getUsername());
        user.switchConnectionStatus();
        output.setMessage(user.getUsername() + " has changed status successfully.");
        return output;
    }

}
