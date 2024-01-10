package globalwaves.admin.add;

import fileio.input.CommandInput;
import globalwaves.GlobalWaves;
import globalwaves.admin.AdminCommand;
import globalwaves.users.artist.Artist;
import globalwaves.users.factory.UsersFactory;
import globalwaves.users.host.Host;
import globalwaves.users.listener.Listener;
import output.Output;

import java.util.Map;

public final class AddUserCommand implements AdminCommand {
    @Override
    public Output execute(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);

        final String type = command.getType();
        final String username = command.getUsername();

        Map<String, Listener> users = GlobalWaves.getInstance().getListeners();
        Map<String, Artist> artists = GlobalWaves.getInstance().getArtists();
        Map<String, Host> hosts = GlobalWaves.getInstance().getHosts();


        // Check if username is already taken in any map
        if (users.containsKey(username) || artists.containsKey(username)
                || hosts.containsKey(username)) {
            output.setMessage("The username " + username + " is already taken.");
            return output;
        }
        switch (type) {
            case "user" -> users.put(username, (Listener) UsersFactory.createUser(command));
            case "artist" -> artists.put(username, (Artist) UsersFactory.createUser(command));
            case "host" -> hosts.put(username, (Host) UsersFactory.createUser(command));
            default -> System.out.println("Unknown user type");
        }

        output.setMessage("The username " + username + " has been added successfully.");
        return output;
    }
}
