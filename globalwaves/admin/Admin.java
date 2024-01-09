package globalwaves.admin;

import fileio.input.CommandInput;
import globalwaves.admin.add.AddUserCommand;
import globalwaves.admin.delete.DeleteUserCommand;
import globalwaves.admin.statistics.GetTop5Albums;
import globalwaves.admin.statistics.GetTop5Artists;
import globalwaves.admin.statistics.GetTop5PlaylistsCommand;
import globalwaves.admin.statistics.GetTop5SongsCommand;
import globalwaves.admin.statistics.SwitchConnectionStatus;
import output.Output;

import java.util.HashMap;
import java.util.Map;

public final class Admin {
    private final Map<String, AdminCommand> commands;

    public Admin() {
        commands = new HashMap<>();
        commands.put("getTop5Songs", new GetTop5SongsCommand());
        commands.put("getTop5Albums", new GetTop5Albums());
        commands.put("getTop5Playlists", new GetTop5PlaylistsCommand());
        commands.put("getTop5Artists", new GetTop5Artists());
        commands.put("addUser", new AddUserCommand());
        commands.put("deleteUser", new DeleteUserCommand());
        commands.put("switchConnectionStatus", new SwitchConnectionStatus());
    }

    /**
     * Command design pattern implementation used for executing admin-related commands
     * @return the output of the command
     */
    public Output executeCommand(final CommandInput commandInput) {
        String commandName = commandInput.getCommand(); // Extract command name from CommandInput
        AdminCommand command = commands.get(commandName);
        if (command == null) {
            throw new IllegalArgumentException("Invalid command: " + commandName);
        }
        return command.execute(commandInput);
    }
}
