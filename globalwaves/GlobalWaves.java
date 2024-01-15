package globalwaves;

import fileio.input.CommandInput;
import fileio.input.Library;
import fileio.input.UserInput;
import globalwaves.admin.Admin;
import globalwaves.audiofiles.Album;
import globalwaves.audiofiles.LikedAudioFiles;
import globalwaves.users.User;
import globalwaves.users.UserExistenceContext;
import globalwaves.users.UserExistenceStrategy;
import globalwaves.users.listener.Listener;
import globalwaves.users.listener.player.Player;
import globalwaves.users.statistics.ArtistStats;
import lombok.Getter;
import lombok.Setter;
import globalwaves.users.statistics.MonetizationStats;
import output.Output;
import globalwaves.users.artist.Artist;
import globalwaves.users.host.Host;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static constants.Constants.CONNECTED;
import static globalwaves.users.listener.player.Player.updatePlayerTime;

/**
 * All user-related commands, including listeners, artists and hosts
 * go through here where the administrative things like username validity
 * is checked and then the command is executed inside the corresponding
 * user class
 * Admin commands are executed through an "admin" instance to allow future
 * admin commands to be added without changing the code here
 */
@Getter
@Setter
public final class GlobalWaves {
    private static GlobalWaves instance;

    private Admin admin;
    private Library library;
    private LikedAudioFiles userInteractions; // liked songs, playlists (listeners interactions)
    private Map<String, Listener> listeners; // listeners
    private Map<String, Artist> artists;
    private Map<String, Host> hosts;

    private GlobalWaves() {
    }
    /**
     * Singleton lazy initialization
     * @return GlobalWaves instance
     */
    public static GlobalWaves getInstance() {
        if (instance == null) {
            instance = new GlobalWaves();
        }
        return instance;
    }

    /**
     * @param library for inputting the library files into my global waves instance
     */
    public static void initialize(final Library library) {
        GlobalWaves.getInstance().listeners = new LinkedHashMap<>();
        for (UserInput userInput : library.getUsers()) {
            Listener user = new Listener(userInput);
            GlobalWaves.getInstance().getListeners().put(userInput.getUsername(), user);
        }
        GlobalWaves.getInstance().library = library;
        GlobalWaves.getInstance().userInteractions = new LikedAudioFiles();
        GlobalWaves.getInstance().artists = new LinkedHashMap<>();
        GlobalWaves.getInstance().hosts = new LinkedHashMap<>();
        GlobalWaves.getInstance().admin = new Admin();
    }

    /**
     * method through each admin command is executed
     * @return Output class instance for serialization
     */
    public Output executeAdminCommand(final CommandInput command) {
        return this.admin.executeCommand(command);
    }

    /**
     * method through each listener command is executed
     * @return Output class instance for serialization with a listener's
     * merch as the message
     */
    public Output seeMerch(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (!userExists(command, output, "listener")) {
            return output;
        }
        Listener user = this.listeners.get(command.getUsername());
        output.setResult(user.seeMerch());
        return output;
    }
    /**
     * calls the listener instance to buy merch
     */
    public Output buyMerch(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (!userExists(command, output, "listener")) {
            return output;
        }
        Listener user = this.listeners.get(command.getUsername());
        Artist artist = parseMerchDescription(command);
        if (artist == null) {
            output.setMessage("The artist associated with "
                    + command.getName() + " doesn't exist.");
            return output;
        }
        String merchName = command.getName();
        output.setMessage(user.buyMerch(artist, merchName));
        return output;
    }

    /**
     * Method that parses the name of an artist's merch
     * so its name can be extracted
     * @return the artist instance whose merch is being bought
     */
    private Artist parseMerchDescription(final CommandInput command) {
        List<String> artistNames = this.artists.keySet().stream().toList();
        String commandDescription = command.getName();
        for (String artistName : artistNames) {
            if (commandDescription.contains(artistName)) {
                return this.artists.get(artistName);
            }
        }
        return null;
    }

    /**
     * calls the listener instance to get its notifications
     */
    public Output getNotifications(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        Listener user = this.listeners.get(command.getUsername());
        output.setNotifications(user.getNotifications());
        return output;
    }
    /**
     * calls the listener instance to cancel its premium subscription
     */
    public Output cancelPremium(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        Output output = Output.getOutputTemplate(command);
        if (!userExists(command, output, "listener")) {
            return output;
        }
        return user.cancelPremium(output);
    }
    /**
     * calls the listener instance to buy a premium subscription
     */
    public Output buyPremium(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        Output output = Output.getOutputTemplate(command);
        if (!userExists(command, output, "listener")) {
            return output;
        }
        return user.buyPremium(output);
    }
    /**
     * @return all monetization statistics
     */
    public Output endProgram() {
        Output output = new Output("endProgram");
        output.setResult(new HashMap<String, MonetizationStats>());
        Map<String, MonetizationStats> resultMap =
                (Map<String, MonetizationStats>) output.getResult();
        /* Update the monetization from each listener that still has a premium subscription */
        for (Listener listener : this.listeners.values()) {
            if (listener.isPremium()) {
                listener.getStats().updateMonetization();
            }
        }
        for (Artist artist : this.artists.values()) {
            ArtistStats stats = artist.getStats();
            MonetizationStats monetizationStats = stats.getMonetizationStats();
            if (!stats.isEmpty() || monetizationStats.getMerchRevenue() != 0.0) {
                resultMap.put(artist.getUsername(), monetizationStats);
            }
        }
        sortMap(resultMap);
        return output;
    }

    /**
     * Sorts the artists' monetization statistics by their total revenue
     * then by their username (lexicographically)
     * @param map map that will be sorted and added to the output result
     */
    public void sortMap(final Map<String, MonetizationStats> map) {
        AtomicInteger ranking = new AtomicInteger(1);
        Map<String, MonetizationStats> sortedMap = map.entrySet().stream()
                .sorted((entry1, entry2) -> {
                    double revenue1 = entry1.getValue().getSongRevenue()
                            + entry1.getValue().getMerchRevenue();
                    double revenue2 = entry2.getValue().getSongRevenue()
                            + entry2.getValue().getMerchRevenue();
                    if (revenue1 == revenue2) {
                        return entry1.getKey().compareTo(entry2.getKey());
                    }
                    return Double.compare(revenue2, revenue1);
                })
                .collect(LinkedHashMap::new, (map1, entry) -> {
                    entry.getValue().setRanking(ranking.getAndIncrement());
                    map1.put(entry.getKey(), entry.getValue());
                }, LinkedHashMap::putAll);
        map.clear();
        map.putAll(sortedMap);
    }

    /**
     * @return all the albums in the system
     */
    public List<Album> getAllAlbums() {
        List<Album> albums = new ArrayList<>();
        for (Artist artist : this.artists.values()) {
            albums.addAll((artist.getArtistpage()).getAlbums());
        }
        return albums;
    }

    /**
     * @return shows wrapped stats of a certain user
     */
    public Output wrapped(final CommandInput command) {
        updateTimestamps(command.getTimestamp());
        Output output = Output.getOutputTemplate(command);
        User user = getUser(command.getUsername());
        assert user != null;
        return user.wrapped(command);
    }

    /**
     * Updates the timestamps of all the listeners' players
     */
    private void updateTimestamps(final int timestamp) {
        for (Listener user : this.listeners.values()) {
            Player player = user.getUserPlayer();
            if (player != null) {
                updatePlayerTime(user, timestamp);
            }
        }
    }

    /**
     * might produce
     * @return the user instance based on the username
     */
    public User getUser(final String username) {
        if (this.listeners.containsKey(username)) {
            return this.listeners.get(username);
        } else if (this.artists.containsKey(username)) {
            return this.artists.get(username);
        } else if (this.hosts.containsKey(username)) {
            return this.hosts.get(username);
        }
        return null;
    }
    /**
     * @return output containing all the users in the system
     */
    public Output getAllUsers(final CommandInput command) {
        Output output = new Output(command.getCommand(), command.getTimestamp());
        List<String> result = new ArrayList<>();
        List<String> listenersNames = new ArrayList<>(this.listeners.keySet());
        List<String> artistsNames = new ArrayList<>(this.artists.keySet());
        List<String> hostsNames = new ArrayList<>(this.hosts.keySet());

        result.addAll(listenersNames);
        result.addAll(artistsNames);
        result.addAll(hostsNames);

        output.setResult(result);
        return output;
    }
    /**
     * @return output containing all the connected users in the system
     */
    public Output getOnlineUsers(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (output.getResult() == null) {
            output.setResult(new ArrayList<String>());
        }
        for (Listener user : this.listeners.values()) {
            if (user.getConnectionStatus().equals(CONNECTED)) {
                ((List<String>) output.getResult()).add(user.getUsername());
            }
        }
        return output;
    }
    /**
     * Calls the user instance to print the current page of the user
     */
    public Output printUserCurrentPage(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        Listener user = this.listeners.get(command.getUsername());
        if (!user.isConnected()) {
            output.setMessage(user.getUsername() + " is offline.");
            return output;
        }
        output.setMessage(user.printCurrentPage());
        return output;
    }
    /**
     * Calls the user instance to change the current page of the user
     */
    public Output changePage(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        Listener user = this.listeners.get(command.getUsername());
        output.setMessage(user.changePage(command));
        return output;
    }
    /**
     * Calls the user instance to subscribe to an artist/host
     */
    public Output subscribe(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (!userExists(command, output, "listener")) {
            return output;
        }
        Listener user = this.listeners.get(command.getUsername());
        output.setMessage(user.subscribe(command));
        return output;
    }
    /**
     * Calls the user instance to load the selection
     */
    public Output load(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.load(command);
    }
    /**
     * Calls the user instance to select a media
     */
    public Output select(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.select(command);
    }
    /**
     * Calls the user instance to unpause or pause a media
     */
    public Output playPause(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.playPause(command);
    }
    /**
     * Retrieves the status of a user's account based on the input command.
     */
    public Output status(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.status(command);
    }
    /**
     * Calls the user instance to get the user's created playlists.
     */
    public Output showPlaylists(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.showPlaylists(command);
    }
    /**
     * Calls the user instance to add or remove a song in a playlist.
     */
    public Output addRemoveInPlaylist(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.addRemoveInPlaylist(command);
    }

    /**
     * Calls the user instance to create a new playlist.
     */
    public Output createPlaylist(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.createPlaylist(command);
    }
    /**
     * Calls the user instance to get the user's favorite songs.
     */
    public Output showPreferredSongs(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.showPreferredSongs(command);
    }
    /**
     * Calls the user instance to change the user's player repeat mode.
     */
    public Output repeat(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.repeat(command);
    }
    /**
     * Calls the user instance to like a song.
     */
    public Output like(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.like(command);
    }
    /**
     * Calls the user instance to switch the visibility of one of its playlists.
     */
    public Output switchVisibility(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.switchVisibility(command);
    }
    /**
     * Enables a user to follow another user or artist.
     */
    public Output follow(final CommandInput command) {
        return this.listeners.get(command.getUsername()).follow(command);
    }
    /**
     * Plays the previous song in the user's playlist.
     */
    public Output prev(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.prev(command);
    }
    /**
     * Plays the next song in the user's playlist.
     */
    public Output next(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.next(command);
    }
    /**
     * Rewinds the current song being played.
     */
    public Output backward(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.backward(command);
    }
    /**
     * Fast forwards the current song being played.
     */
    public Output forward(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.forward(command);
    }
    /**
     * Toggles the shuffle mode of the user's player.
     */
    public Output shuffle(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.shuffle(command);
    }
    /**
     * Allows a host to add a new podcast.
     */
    public Output addPodcast(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (!userExists(command, output, "host")) {
            return output;
        }
        Host host = this.hosts.get(command.getUsername());
        output.setMessage(host.addPodcast(command));
        return output;
    }
    /**
     * Allows a host to remove an existing podcast.
     */
    public Output removePodcast(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (!userExists(command, output, "host")) {
            return output;
        }
        Host host = this.hosts.get(command.getUsername());
        output.setMessage(host.removePodcast(command));
        return output;
    }
    /**
     * Displays the podcasts managed by a host.
     */
    public Output showPodcasts(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        Host host = this.hosts.get(command.getUsername());
        output.setResult(host.showPodcasts());
        return output;
    }
    /**
     * Allows an artist to add a new album.
     */
    public Output addAlbum(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (!userExists(command, output, "artist")) {
            return output;
        }
        output.setMessage(this.artists.get(command.getUsername()).addAlbum(command));
        return output;
    }

    /**
     * Allows an artist to remove an existing album.
     */
    public Output removeAlbum(final CommandInput command) {
        for (Listener user : this.listeners.values()) {
            Player player = user.getUserPlayer();
            if (player != null) {
                updatePlayerTime(user, command.getTimestamp());
            }
        }
        Output output = Output.getOutputTemplate(command);
        if (!userExists(command, output, "artist")) {
            return output;
        }
        Artist artist = this.artists.get(command.getUsername());
        output.setMessage(artist.removeAlbum(command));
        return output;
    }

    /**
     * Displays the albums created by an artist.
     */
    public Output showAlbums(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        Artist artist = this.artists.get(command.getUsername());
        output.setResult(artist.showAlbums());
        return output;
    }

    /**
     * Allows a host to add an announcement.
     */
    public Output addHostAnnouncement(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (!userExists(command, output, "host")) {
            return output;
        }
        Host host = this.hosts.get(command.getUsername());
        output.setMessage(host.addAnnouncement(command));
        return output;
    }

    /**
     * Allows a host to remove an existing announcement.
     */
    public Output removeAnnouncement(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (!userExists(command, output, "host")) {
            return output;
        }
        Host host = this.hosts.get(command.getUsername());
        output.setMessage(host.removeAnnouncement(command));
        return output;
    }

    /**
     * Allows an artist to add a new event.
     */
    public Output addArtistEvent(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (!userExists(command, output, "artist")) {
            return output;
        }
        Artist artist = this.artists.get(command.getUsername());
        output.setMessage(artist.addEvent(command));
        return output;
    }

    /**
     * Allows an artist to remove an existing event.
     */
    public Output removeEvent(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (!userExists(command, output, "artist")) {
            return output;
        }
        Artist artist = this.artists.get(command.getUsername());
        output.setMessage(artist.removeEvent(command));
        return output;
    }

    /**
     * Allows an artist to add merchandise for sale.
     */
    public Output addArtistMerch(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (!userExists(command, output, "artist")) {
            return output;
        }
        Artist artist = this.artists.get(command.getUsername());
        output.setMessage(artist.addMerch(command));
        return output;
    }
    /**
     * Method for checking if a user exists within the program's database.
     * @param command for getting the username
     * @param output for setting the correct error/success message
     * @param type to set the right checking strategy
     * @return true if the user exists, false otherwise
     */
    private boolean userExists(final CommandInput command, final Output output, final String type) {
        UserExistenceContext context = new UserExistenceContext();
        UserExistenceStrategy.getExistenceStrategy(context, type);
        return context.exists(command, output);
    }

    /**
     * calls the listener instance to use the search bar
     * @param command to parse search filters / user
     * @return output indicating success or failure
     */
    public Output search(final CommandInput command) {
        Listener user = this.listeners.get(command.getUsername());
        return user.search(command);
    }

    /**
     * checks to see if a user (including listeners, artists and hosts)
     * is interacting with the app
     * used primarily in deleting users - related commands
     * @param username user to be checked
     * @return boolean indicating if the user is interacting with the app
     */
    public boolean isInteracting(final String username) {
        User user;
        if (this.listeners.containsKey(username)) {
            user = this.listeners.get(username);
        } else if (this.artists.containsKey(username)) {
            user = this.artists.get(username);
        } else if (this.hosts.containsKey(username)) {
            user = this.hosts.get(username);
        } else {
            return false;
        }
        return user.isInteracting();
    }
}
