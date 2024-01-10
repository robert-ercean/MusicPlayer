package output;

import fileio.input.CommandInput;
import globalwaves.users.listener.notifications.Notification;
import lombok.Getter;
import lombok.Setter;
import globalwaves.users.listener.player.Player;

import java.util.List;

@Getter
@Setter
public class Output {
    private String command;
    private String user;
    private Integer timestamp;
    private String message;
    private List<String> results;
    private PlayerStatsForOutput stats;
    public Object result; // result Object list for showPlaylists, showLikedSongs, showPodcasts
    private List<Notification> notifications;
    public Output(final String commandName) {
        this.command = commandName;
    }
    public Output(final String commandName, final int timestamp) {
        this.command = commandName;
        this.timestamp = timestamp;
    }
    public Output(final String commandName, final String username,
                  final Integer timestamp, final String message,
                  final List<String> results) {
        this.command = commandName;
        this.user = username;
        this.timestamp = timestamp;
        this.message = message;
        this.results = results;
    }
    public Output(final String commandName, final String username, final Integer timestamp) {
        this.command = commandName;
        this.user = username;
        this.timestamp = timestamp;
    }
    public Output(final String commandName, final String username,
                  final Integer timestamp, final Player userPlayer) {
        this.command = commandName;
        this.user = username;
        this.timestamp = timestamp;
        this.stats = new PlayerStatsForOutput(userPlayer);
    }
    /**
     * @return basic output template for commands, other fields will be added later
     */
    public static Output getOutputTemplate(final CommandInput commandInput) {
        return new Output(commandInput.getCommand(), commandInput.getUsername(),
                commandInput.getTimestamp());
    }
}
