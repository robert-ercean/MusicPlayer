package globalwaves.users.listener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import globalwaves.GlobalWaves;
import globalwaves.audiofiles.Album;
import globalwaves.audiofiles.LikedAudioFiles;
import globalwaves.audiofiles.Podcast;
import globalwaves.audiofiles.PodcastState;
import globalwaves.audiofiles.Playlist;
import globalwaves.audiofiles.Song;
import fileio.input.CommandInput;
import fileio.input.UserInput;
import globalwaves.pages.artist.ArtistPage;
import globalwaves.process.ProcessCommand;
import globalwaves.users.artist.Artist;
import globalwaves.users.artist.Merch;
import globalwaves.users.listener.search.SearchContext;
import globalwaves.users.listener.search.SearchStrategy;
import globalwaves.users.User;
import globalwaves.users.listener.player.Player;
import globalwaves.users.statistics.ListenerStats;
import globalwaves.users.statistics.UserStatistics;
import globalwaves.users.statistics.UserStatsObserver;
import lombok.Getter;
import lombok.Setter;
import globalwaves.pages.Page;
import globalwaves.pages.factory.PageFactory;
import globalwaves.pages.user.LikedContentPage;
import output.Output;

import static constants.Constants.CONNECTED;
import static constants.Constants.DISCONNECTED;
import static constants.Constants.HOME_PAGE;
import static constants.Constants.LIKED_CONTENT_PAGE;
import static constants.Constants.MIN_TIME;
import static constants.Constants.NORMAL_USER;
import static constants.Constants.NO_REPEAT;
import static constants.Constants.PRIVATE;
import static constants.Constants.PUBLIC;
import static constants.Constants.REPEAT_ALL;
import static constants.Constants.REPEAT_CURRENT_SONG;
import static constants.Constants.REPEAT_INFINITE;
import static constants.Constants.REPEAT_ONCE;
import static globalwaves.users.listener.player.Player.updatePlayerTime;

@Getter
@Setter
public final class Listener extends User implements UserStatistics {
    private Page currentPage;
    private final Page homePage;
    private final Page likedContentPage;

    private final ListenerStats stats;

    private Boolean isPremium = false;
    private int credits;

    private ArrayList<Merch> boughtMerch;

    private int lastCommandTimestamp;

    private List<Album> searchedAlbums;
    private Album selectedAlbum;

    private List<String> searchedHosts;
    private String selectedHost;

    private List<String> searchedArtists;
    private String selectedArtist;

    private List<Song> searchedSongs;
    private Song selectedSong;

    private List<Podcast> searchedPodcasts;
    private Podcast selectedPodcast;

    private List<Playlist> searchedPlaylists;
    private Playlist selectedPlaylist;

    private String selectedItemType;

    private Player userPlayer;
    private Map<String, PodcastState> podcastStates;

    private Boolean hasSelection = false;
    private String connectionStatus;
    private List<UserStatsObserver> statsObservers;
    @Override
    public void registerObserver(UserStatsObserver o) {
        if (this.statsObservers.contains(o)) {
            return;
        }
        this.statsObservers.add(o);
    }
    @Override
    public void removeObserver(UserStatsObserver o) {
        this.statsObservers.remove(o);
    }
    @Override
    public void notifyObservers(String eventType, Player userPlayer, int idx) {
        for (UserStatsObserver observer : statsObservers) {
            observer.update(eventType, userPlayer, idx);
        }
    }
    @Override
    public Output wrapped(CommandInput command) {
        return this.stats.display(command);
    }
    public String buyMerch(Artist artist, String merchName) {
        ArtistPage artistPage = artist.getArtistpage();
        if (this.currentPage != artistPage) {
            return "Cannot buy merch from this page.";
        }
        for (Merch merch : artistPage.getMerch()) {
            if (merch.getName().equals(merchName)) {
                artist.getStats().getMonetizationStats().merchRevenue += merch.getPrice();
                this.boughtMerch.add(merch);
                return super.getUsername() + " has added new merch successfully.";
            }
        }
        return "The merch " + merchName + " doesn't exist.";
    }
    public Output buyPremium(Output output) {
        updatePlayerTime(this, output.getTimestamp());
        if (this.isPremium) {
            output.setMessage(super.getUsername() + " is already a premium user.");
            return output;
        }
        this.isPremium = true;
        this.credits = 1000000;
        output.setMessage(super.getUsername() + " bought the subscription successfully.");
        return output;
    }
    public Output cancelPremium(Output output) {
        updatePlayerTime(this, output.getTimestamp());
        if (!this.isPremium) {
            output.setMessage(super.getUsername() + " is not a premium user.");
            return output;
        }
        this.isPremium = false;
        this.credits = 0;
        output.setMessage(super.getUsername() + " cancelled the subscription successfully.");
        return output;
    }
    public boolean isPremium() {
        return this.isPremium;
    }
    public LikedContentPage getLikedContentPage() {
        return (LikedContentPage) this.likedContentPage;
    }
    public Listener(final CommandInput command) {
        super.setUsername(command.getUsername());
        super.setAge(command.getAge());
        super.setCity(command.getCity());
        this.podcastStates = new HashMap<>();
        this.connectionStatus = CONNECTED;
        this.homePage = PageFactory.createPage(HOME_PAGE, NORMAL_USER, this.getUsername());
        this.likedContentPage = PageFactory.createPage(
                LIKED_CONTENT_PAGE, NORMAL_USER, this.getUsername());
        this.boughtMerch = new ArrayList<>();
        this.currentPage = this.homePage;
        this.stats = new ListenerStats(super.getUsername());
        this.statsObservers = new ArrayList<>();
        this.registerObserver(this.stats);
    }
    public Listener(final UserInput userInput) {
        super.setUsername(userInput.getUsername());
        super.setAge(userInput.getAge());
        super.setCity(userInput.getCity());
        this.podcastStates = new HashMap<>();
        this.connectionStatus = CONNECTED;
        this.homePage = PageFactory.createPage(HOME_PAGE, NORMAL_USER, this.getUsername());
        this.likedContentPage = PageFactory.createPage(
                LIKED_CONTENT_PAGE, NORMAL_USER, this.getUsername());
        this.currentPage = this.homePage;
        this.boughtMerch = new ArrayList<>();
        this.stats = new ListenerStats(super.getUsername());
        this.statsObservers = new ArrayList<>();
        this.registerObserver(this.stats);
    }
    /**
     * Returns the user's current page as a String
     */
    public String printCurrentPage() {
        return this.currentPage.display();
    }
    /**
     * changes the user's current page (which gets displayed with the printCurrentPage command)
     * @param command for getting the command parameters
     * @return the output message as a String
     */
    public String changePage(final CommandInput command) {
        String nextPage = command.getNextPage();
        switch (nextPage) {
            case "Home":
                this.currentPage = this.homePage;
                break;
            case "LikedContent":
                this.currentPage = this.likedContentPage;
                break;
            default:
                return this.getUsername() + " is trying to access a non-existent page.";
        }
        return this.getUsername() + " accessed " + nextPage + " successfully.";
    }

    /**
     * @param podcastName for id-ing the podcast in the map
     * @param epIdx       for updating the status of the podcast
     * @param timeIdx     for updating the status of the podcast
     */
    public void updatePodcastState(final String podcastName, final int epIdx, final int timeIdx) {
        PodcastState podcastState = new PodcastState(epIdx, timeIdx);
        this.podcastStates.put(podcastName, podcastState);
    }

    /**
     * For setting the player to empty (no audio file playing)
     */
    public void setPlayerToEmpty() {
        this.getUserPlayer().setPaused(true);
        this.getUserPlayer().setRemainedTime(0);
        this.getUserPlayer().setAudioFileName("");
        this.getUserPlayer().setShuffleStatus(false);
        this.getUserPlayer().setRepeatStatus(NO_REPEAT);
    }
    /**
     * switches the current user connection status
     * possible states: connected, disconnected
     */
    public void switchConnectionStatus() {
        if (this.connectionStatus.equals(CONNECTED)) {
            this.connectionStatus = DISCONNECTED;
        } else {
            this.connectionStatus = CONNECTED;
        }
    }

    public boolean isConnected() {
        return this.connectionStatus.equals(CONNECTED);
    }
    @Override
    public boolean isInteracting() {
        for (Listener user : GlobalWaves.getInstance().getUsers().values()) {
            Player player = user.getUserPlayer();
            if (Player.isEmpty(player)) {
                continue;
            }
            if (player.getCurrentPlaylist() != null
                    && player.getCurrentPlaylist().getOwner().equals(this.getUsername())) {
                return true;
            }
        }
        return false;
    }
    /** Searches for a song, podcast, album, artist or playlist.
     * uses the search strategy pattern inside globalwaves.users.listener.search
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output search(final CommandInput command) {
        updatePlayerTime(this, command.getTimestamp());
        if (!this.isConnected()) {
            Output output = Output.getOutputTemplate(command);
            output.setMessage(this.getUsername() + " is offline.");
            output.setResults(new ArrayList<>());
            return output;
        }
        if (this.getUserPlayer() != null) {
            this.setPlayerToEmpty();
        }
        SearchContext context = new SearchContext();
        SearchStrategy.getSearchStrategy(context, command.getType());

        return context.executeSearch(command);
    }
    /**
     * Likes or unlikes a song.
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output like(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (!this.isConnected()) {
            output.setMessage(this.getUsername() + " is offline.");
            return output;
        }
        updatePlayerTime(this, command.getTimestamp());
        if (this.getUserPlayer() == null || this.getUserPlayer().
                getAudioFileName().isEmpty()) {
            output.setMessage("Please load a source before liking or unliking.");
            return output;
        }
        if (this.getSelectedItemType().equals("podcast")) {
            output.setMessage("Loaded source is not a song.");
            return output;
        }
        Song song = this.getUserPlayer().getCurrentSong();
        LikedAudioFiles interactions = GlobalWaves.getInstance().getUserInteractions();
        if (this.getLikedContentPage().getLikedSongs().contains(song)) {
            this.getLikedContentPage().getLikedSongs().remove(song);
            int currentLikeCount = GlobalWaves.getInstance().
                    getUserInteractions().getSongLikeCountMap().getOrDefault(song, 0);
            if (currentLikeCount > 1) {
                interactions.getSongLikeCountMap().put(song, currentLikeCount - 1);
            } else if (currentLikeCount == 1) {
                interactions.getSongLikeCountMap().remove(song);
            }
        } else {
            this.getLikedContentPage().getLikedSongs().add(song);
            int currentLikeCount = interactions.getSongLikeCountMap().
                    getOrDefault(song, 0);
            interactions.getSongLikeCountMap().put(song, currentLikeCount + 1);
        }
        output.setMessage((this.getLikedContentPage().getLikedSongs().contains(song)
                ? "Like " : "Unlike ") + "registered successfully.");
        return output;
    }
    /**
     * Skips forward the current podcast.
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output forward(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        updatePlayerTime(this, command.getTimestamp());
        if (userPlayer == null || userPlayer.getAudioFileName().isEmpty()) {
            output.setMessage("Please load a source before attempting to forward.");
            return output;
        }
        if (userPlayer.getCurrentPodcast() == null) {
            output.setMessage("The loaded source is not a podcast.");
            return output;
        }

        int remainingTime = userPlayer.getRemainedTime();
        Podcast currPodcast = userPlayer.getCurrentPodcast();

        if (remainingTime < MIN_TIME) {
            currPodcast.setIdx(currPodcast.getIdx() + 1);
            userPlayer.setAudioFileName(currPodcast.getEpisodes().get(
                    currPodcast.getIdx()).getName());
            userPlayer.setRemainedTime(currPodcast.getEpisodes().get(
                    currPodcast.getIdx()).getDuration());
        } else {
            userPlayer.setRemainedTime(remainingTime - MIN_TIME);
        }

        output.setMessage("Skipped forward successfully.");
        return output;
    }
    /**
     * Shuffles the current playlist.
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output shuffle(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        updatePlayerTime(this, command.getTimestamp());
        if (userPlayer == null || userPlayer.getAudioFileName().isEmpty()) {
            output.setMessage("Please load a source before using the shuffle function.");
            return output;
        }
        if (userPlayer.getCurrentPlaylist() == null) {
            output.setMessage("The loaded source is not a playlist or an album.");
            return output;
        }
        if (command.getSeed() != null) {
            userPlayer.changeShuffleStatus(!userPlayer.
                    isShuffleStatus(), command.getSeed());
        } else {
            userPlayer.changeShuffleStatus(!userPlayer.isShuffleStatus(), 0);
        }

        output.setMessage("Shuffle function " + (userPlayer.
                isShuffleStatus() ? "activated" : "deactivated") + " successfully.");
        return output;
    }
    /**
     * Rewinds the current podcast.
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output backward(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        updatePlayerTime(this, command.getTimestamp());
        if (userPlayer == null || userPlayer.getAudioFileName().isEmpty()) {
            output.setMessage("Please select a source before rewinding.");
            return output;
        }
        if (userPlayer.getCurrentPodcast() == null) {
            output.setMessage("The loaded source is not a podcast.");
            return output;
        }

        Podcast currPodcast = userPlayer.getCurrentPodcast();

        int remainingTime = userPlayer.getRemainedTime();
        int elapsedTime = currPodcast.getEpisodes().get(currPodcast.
                getIdx()).getDuration() - remainingTime;

        if (elapsedTime < MIN_TIME) {
            userPlayer.setRemainedTime(currPodcast.getEpisodes().get(
                    currPodcast.getIdx()).getDuration());
        } else {
            userPlayer.setRemainedTime(remainingTime + MIN_TIME);
        }

        output.setMessage("Rewound successfully.");
        return output;
    }
    /**
     * Plays the previous track or sets the current track to the beginning.
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output prev(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        updatePlayerTime(this, command.getTimestamp());
        if (userPlayer == null || userPlayer.getAudioFileName().isEmpty()) {
            output.setMessage("Please load a source before returning to the previous track.");
            return output;
        }
        int elapsedTime;
        int idx;

        switch (this.getSelectedItemType()) {
            case "song":
                userPlayer.setRemainedTime(userPlayer.getCurrentSong().getDuration());
                break;
            case "album": case "playlist":
                Playlist currPlaylist = userPlayer.getCurrentPlaylist();
                idx = currPlaylist.getIdx();
                elapsedTime = currPlaylist.getSongsList().get(idx).
                        getDuration() - userPlayer.getRemainedTime();
                if (elapsedTime >= 1 || idx == 0) {
                    userPlayer.setRemainedTime(userPlayer.getCurrentSong().
                            getDuration());
                } else {
                    userPlayer.setRemainedTime(currPlaylist.getSongsList().
                            get(idx - 1).getDuration());
                    userPlayer.setAudioFileName(currPlaylist.getSongsList().
                            get(idx - 1).getName());
                    userPlayer.setCurrentSong(currPlaylist.getSongsList().
                            get(idx - 1));
                    currPlaylist.setIdx(idx - 1);
                }
                break;
            case "podcast":
                Podcast currPodcast = userPlayer.getCurrentPodcast();
                idx = currPodcast.getIdx();
                elapsedTime = currPodcast.getEpisodes().get(idx).getDuration()
                        - userPlayer.getRemainedTime();
                if ((elapsedTime >= 1 || idx == 0) && elapsedTime != 0) {
                    userPlayer.setRemainedTime(currPodcast.getEpisodes().
                            get(idx).getDuration());
                } else {

                    userPlayer.setRemainedTime(currPodcast.getEpisodes().
                            get(idx - 1).getDuration());
                    userPlayer.setAudioFileName(currPodcast.getEpisodes().
                            get(idx - 1).getName());
                    currPodcast.setIdx(idx - 1);
                }
                break;
            default:
                System.out.println("Unknown selected item type");
                break;
        }
        userPlayer.setPaused(false);
        output.setMessage("Returned to previous track successfully. The current "
                + "track is " + userPlayer.getAudioFileName() + '.');
        return output;
    }
    /**
     * Plays the next track.
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output next(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        updatePlayerTime(this, command.getTimestamp());

        if (userPlayer == null || userPlayer.getAudioFileName().isEmpty()) {
            output.setMessage("Please load a source before skipping to the next track.");
            return output;
        }

        switch (this.getSelectedItemType()) {
            case "song": {
                switch (userPlayer.getRepeatStatus()) {
                    case NO_REPEAT:  {
                        this.setPlayerToEmpty();
                        output.setMessage("Please load a source before "
                                + "skipping to the next track.");
                        return output;
                    }
                    case REPEAT_ONCE: {
                        userPlayer.setRemainedTime(userPlayer.getCurrentSong().
                                getDuration());
                        userPlayer.setRepeatStatus(NO_REPEAT);
                        break;
                    }
                    case REPEAT_INFINITE:
                        userPlayer.setRemainedTime(userPlayer.getCurrentSong().
                                getDuration());
                        break;
                    default:
                        System.out.println("Unknown repeat status");
                        break;
                }
                break;
            }
            case "album": case "playlist": {
                Playlist currPlaylist = userPlayer.getCurrentPlaylist();
                switch (userPlayer.getRepeatStatus()) {
                    case NO_REPEAT: {
                        if (currPlaylist.getIdx() + 1 == currPlaylist.
                                getSongsList().size()) {
                            this.setPlayerToEmpty();
                            output.setMessage("Please load a source before "
                                    + "skipping to the next track.");
                            return output;
                        } else {
                            currPlaylist.setIdx(currPlaylist.getIdx() + 1);
                            userPlayer.setCurrentSong(currPlaylist.
                                    getSongsList().get(currPlaylist.getIdx()));
                            userPlayer.setAudioFileName(currPlaylist.
                                    getSongsList().get(currPlaylist.getIdx()).
                                    getName());
                            userPlayer.setRemainedTime(currPlaylist.getSongsList().
                                    get(currPlaylist.getIdx()).getDuration());
                        }
                        break;
                    }
                    case REPEAT_ALL: {
                        if (currPlaylist.getIdx() + 1 == currPlaylist.
                                getSongsList().size()) {
                            currPlaylist.setIdx(0);
                            userPlayer.setCurrentSong(currPlaylist.
                                    getSongsList().get(0));
                            userPlayer.setAudioFileName(currPlaylist.
                                    getSongsList().get(0).getName());
                            userPlayer.setRemainedTime(currPlaylist.
                                    getSongsList().get(0).getDuration());
                        } else {
                            currPlaylist.setIdx(currPlaylist.getIdx() + 1);
                            userPlayer.setCurrentSong(currPlaylist.
                                    getSongsList().get(currPlaylist.getIdx()));
                            userPlayer.setAudioFileName(currPlaylist.
                                    getSongsList().get(currPlaylist.getIdx()).
                                    getName());
                            userPlayer.setRemainedTime(currPlaylist.
                                    getSongsList().get(currPlaylist.getIdx())
                                    .getDuration());
                        }
                        break;
                    }
                    case REPEAT_CURRENT_SONG: {
                        userPlayer.setRemainedTime(userPlayer.getCurrentSong()
                                .getDuration());
                        break;
                    }
                    default:
                        System.out.println("Unknown repeat status");
                        break;
                }
                break;
            }
            case "podcast": {
                switch (userPlayer.getRepeatStatus()) {
                    case NO_REPEAT:
                        if (userPlayer.getCurrentPodcast().getIdx() + 1
                                == userPlayer.getCurrentPodcast().getEpisodes()
                                .size()) {
                            this.setPlayerToEmpty();
                            output.setMessage("Please load a source before "
                                    + "skipping to the next track.");
                            return output;
                        } else {
                            userPlayer.getCurrentPodcast().setIdx(userPlayer.
                                    getCurrentPodcast().getIdx() + 1);
                            userPlayer.setAudioFileName(userPlayer.
                                    getCurrentPodcast().getEpisodes().
                                    get(userPlayer.getCurrentPodcast().
                                            getIdx()).getName());
                            userPlayer.setRemainedTime(userPlayer.
                                    getCurrentPodcast().getEpisodes().get(
                                            userPlayer.getCurrentPodcast().
                                                    getIdx()).getDuration());
                        }
                    case REPEAT_ONCE:
                        userPlayer.setRemainedTime(userPlayer.getCurrentPodcast().
                                getEpisodes().get(userPlayer.getCurrentPodcast().
                                        getIdx()).getDuration());
                        userPlayer.setRepeatStatus(NO_REPEAT);
                        break;
                    case REPEAT_INFINITE:
                        userPlayer.setRemainedTime(userPlayer.
                                getCurrentPodcast().
                                getEpisodes().get(userPlayer.getCurrentPodcast()
                                        .getIdx()).getDuration());
                        break;
                    default:
                        System.out.println("Unknown repeat status");
                        break;
                }
                break;
            }
            default:
                System.out.println("Unknown selected item type");
                break;
        }
        userPlayer.setPaused(false);
        output.setMessage("Skipped to next track successfully. "
                + "The current track is " + userPlayer.getAudioFileName() + '.');
        return output;
    }
    /**
     * Follows or unfollows a playlist.
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output follow(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        updatePlayerTime(this, command.getTimestamp());
        if (!this.getHasSelection()) {
            output.setMessage("Please select a source before"
                    + " following or unfollowing.");
            return output;
        }
        if (!this.getSelectedItemType().equals("playlist")) {
            output.setMessage("The selected source is not a playlist.");
            return output;
        }
        Playlist playlist = this.getSelectedPlaylist();

        if (playlist.getOwner().equals(this.getUsername())) {
            output.setMessage("You cannot follow or unfollow your own playlist.");
            return output;
        }
        if (!playlist.getFollowersList().contains(this.getUsername())) {
            playlist.getFollowersList().add(this.getUsername());
            playlist.setFollowers(playlist.getFollowers() + 1);
            this.getLikedContentPage().getFollowedPlaylists().add(playlist);
        } else {
            playlist.getFollowersList().remove(this.getUsername());
            playlist.setFollowers(playlist.getFollowers() - 1);
            this.getLikedContentPage().getFollowedPlaylists().remove(playlist);
        }
        output.setMessage("Playlist" + (playlist.getFollowersList().contains(
                this.getUsername()) ? " followed" : " unfollowed")
                + " successfully.");
        return output;
    }
    /**
     * Shows the user's playlists
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output showPlaylists(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        output.setResult(new ArrayList<Playlist>());
        for (Playlist playlist : GlobalWaves.getInstance().getUserInteractions().getPlaylists()) {
            if (playlist.getOwner().equals(this.getUsername())) {
                ((List<Playlist>) output.getResult()).add(playlist);
            }
        }
        updatePlayerTime(this, command.getTimestamp());
        return output;
    }
    /**
     * Shows the user's player stats
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output status(final CommandInput command) {
        updatePlayerTime(this, command.getTimestamp());
        return new Output(command.getCommand(), command.getUsername(), command.getTimestamp(),userPlayer);
    }
    /** Changes the repeat status of the current media.
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output repeat(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        updatePlayerTime(this, command.getTimestamp());
        Player currentPlayer = this.getUserPlayer();
        if (currentPlayer == null || currentPlayer.getAudioFileName().isEmpty()) {
            output.setMessage("Please load a source before setting the repeat status.");
            return output;
        }
        currentPlayer.changeRepeatStatus();
        output.setMessage("Repeat mode changed to " + currentPlayer.getRepeatStatus().
                toLowerCase() + '.');
        return output;
    }
    /** Switches the visibility of a playlist.
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output switchVisibility(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        updatePlayerTime(this, command.getTimestamp());

        int idx = command.getPlaylistId() - 1;
        List<Playlist> ownerSpecificPlaylists = GlobalWaves.getInstance().
                getUserInteractions().getOwnerSpecificPlaylists(this.getUsername());
        if (idx >= ownerSpecificPlaylists.size()) {
            output.setMessage("The specified playlist ID is too high.");
            return output;
        }
        Playlist playlist = ownerSpecificPlaylists.get(idx);

        if (playlist.getVisibility().equals(PUBLIC)) {
            playlist.setVisibility(PRIVATE);
        } else {
            playlist.setVisibility(PUBLIC);
        }

        output.setMessage("Visibility status updated successfully to "
                + playlist.getVisibility() + ".");
        return output;
    }
    /**
     * Shows the liked songs of a listener.
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output showPreferredSongs(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (output.getResult() == null) {
            output.setResult(new ArrayList<String>());
        }
        output.setResult(new ArrayList<>());
        for (Song song : this.getLikedContentPage().getLikedSongs()) {
            ((List<String>) output.getResult()).add(song.getName());
        }
        updatePlayerTime(this, command.getTimestamp());
        return output;
    }
    /**
     * Creates a playlist.
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output createPlaylist(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        updatePlayerTime(this, command.getTimestamp());
        for (Playlist playlist : GlobalWaves.getInstance().getUserInteractions().getPlaylists()) {
            if (playlist.getName().equals(command.getPlaylistName())
                    && this.getUsername().equals(playlist.getOwner())) {
                output.setMessage("A playlist with the same name already exists.");
                return output;
            }
        }

        Playlist playlist = new Playlist(command.getPlaylistName());
        playlist.setTimeStampCreated(command.getTimestamp());
        playlist.setOwner(this.getUsername());
        GlobalWaves.getInstance().getUserInteractions().getPlaylists().add(playlist);
        output.setMessage("Playlist created successfully.");
        return output;
    }
    /**
     * Adds or removes a song from a playlist.
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output addRemoveInPlaylist(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (this.getUserPlayer() == null || this.getUserPlayer().getAudioFileName().
                isEmpty()) {
            output.setMessage("Please load a source before adding to or removing "
                    + "from the playlist.");
            return output;
        }
        if (this.getUserPlayer().getCurrentSong() == null) {
            output.setMessage("The loaded source is not a song.");
            return output;
        }
        int idx = command.getPlaylistId() - 1;
        List<Playlist> ownerSpecificPlaylists = GlobalWaves.getInstance().
                getUserInteractions().getOwnerSpecificPlaylists(this.getUsername());
        if (idx >= ownerSpecificPlaylists.size()) {
            output.setMessage("The specified playlist does not exist.");
            return output;
        }
        Playlist playlist = ownerSpecificPlaylists.get(idx);
        // check if the playlist contains the song
        Song song = this.getUserPlayer().getCurrentSong();
        if (playlist.getSongs().contains(song.getName())) {
            playlist.getSongs().remove(song.getName());
            playlist.getSongsList().remove(song);
            output.setMessage("Successfully removed from playlist.");
        } else {
            playlist.getSongs().add(song.getName());
            playlist.getSongsList().add(song);
            output.setMessage("Successfully added to playlist.");
        }
        updatePlayerTime(this, command.getTimestamp());
        return output;
    }
    /**
     * Plays or pauses the current media.
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output playPause(final CommandInput command) {
        updatePlayerTime(this, command.getTimestamp());
        Output output = Output.getOutputTemplate(command);
        if (!this.isConnected()) {
            output.setMessage(this.getUsername() + " is offline.");
            return output;
        }
        if (this.getSelectedItemType() == null || this.getUserPlayer() == null || this.
                getUserPlayer().getAudioFileName().isEmpty()) {
            output.setMessage("Please load a source before attempting to "
                    + "pause or resume playback.");
            return output;
        }
        this.getUserPlayer().setPaused(!this.getUserPlayer().isPaused());
        output.setMessage("Playback " + (this.getUserPlayer().
                isPaused() ? "paused successfully." : "resumed successfully."));
        return output;
    }
    /**
     * Loads a source into the player.
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output load(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (!this.isConnected()) {
            output.setMessage(this.getUsername() + " is offline.");
            return output;
        }
        if (!this.getHasSelection()) {
            output.setMessage("Please select a source before attempting to load.");
            return output;
        }
        switch (this.getSelectedItemType()) {
            case "song":
                this.setUserPlayer(new Player(this.getSelectedSong(), super.getUsername()));
                this.notifyObservers("song", this.userPlayer, 0);
                this.setSearchedSongs(null);
                break;
            case "podcast":
                PodcastState podcastState = this.getPodcastStates().get(
                        this.getSelectedPodcast().getName());
                // check if the podcast has been played before
                if (podcastState == null) {
                    this.setUserPlayer(new Player(this.getSelectedPodcast(), super.getUsername()));
                } else {
                    this.setUserPlayer(new Player(this.
                            getSelectedPodcast(), podcastState, super.getUsername()));
                }
                this.setSearchedPodcasts(null);
                break;
            case "playlist":
                this.setUserPlayer(new Player(this.getSelectedPlaylist(), super.getUsername()));
                this.setSearchedPlaylists(null);
                break;
            case "album":
                this.setUserPlayer(new Player(this.getSelectedAlbum(), super.getUsername()));
                this.setSearchedAlbums(null);
                break;
            default:
                System.out.println("Unknown selected item type");
                break;
        }
        output.setMessage("Playback loaded successfully.");
        this.setLastCommandTimestamp(command.getTimestamp());
        this.setHasSelection(false);
        return output;
    }
    /**
     * Selects an item from the search results.
     * @param command for getting the command parameters
     * @return the output message
     */
    public Output select(final CommandInput command) {
        updatePlayerTime(this, command.getTimestamp());
        Output output = Output.getOutputTemplate(command);
        if (!this.isConnected()) {
            output.setMessage(this.getUsername() + " is offline.");
            return output;
        }
        int resultsSize;
        if (this.getSearchedPodcasts() == null && this.getSearchedSongs()
                == null && this.getSearchedPlaylists() == null && this.getSearchedArtists() == null
                && this.getSearchedAlbums() == null && this.getSearchedHosts() == null) {
            output.setMessage("Please conduct a search before making a selection.");
            return output;
        }
        switch (this.getSelectedItemType()) {
            case "host":
                resultsSize = this.getSearchedHosts().size();
                break;
            case "song":
                resultsSize = this.getSearchedSongs().size();
                break;
            case "podcast":
                resultsSize = this.getSearchedPodcasts().size();
                break;
            case "playlist":
                resultsSize = this.getSearchedPlaylists().size();
                break;
            case "artist":
                resultsSize = this.getSearchedArtists().size();
                break;
            case "album":
                resultsSize = this.getSearchedAlbums().size();
                break;
            default:
                output.setMessage("Error, resultsSize has not been initialized.");
                return output;
        }

        int idx = command.getItemNumber() - 1;
        if (idx >= resultsSize) {
            this.setHasSelection(false);
            output.setMessage("The selected ID is too high.");
            return output;
        }
        switch (this.getSelectedItemType()) {
            case "song":
                this.setSelectedSong(this.getSearchedSongs().get(idx));
                output.setMessage("Successfully selected " + this.
                        getSearchedSongs().get(idx).getName() + '.');
                break;
            case "podcast":
                this.setSelectedPodcast(this.getSearchedPodcasts().get(idx));
                output.setMessage("Successfully selected " + this.
                        getSearchedPodcasts().get(idx).getName() + '.');
                break;
            case "playlist":
                this.setSelectedPlaylist(this.getSearchedPlaylists().get(idx));
                output.setMessage("Successfully selected " + this.
                        getSearchedPlaylists().get(idx).getName() + '.');
                break;
            case "host":
                this.setSelectedHost(this.getSearchedHosts().get(idx));
                output.setMessage("Successfully selected " + this.
                        getSearchedHosts().get(idx) + "'s page.");
                this.setCurrentPage(GlobalWaves.getInstance().getHosts().get(
                        this.getSelectedHost()).getHostpage());
                this.setHasSelection(false);
                return output;
            case "artist":
                this.setSelectedArtist(this.getSearchedArtists().get(idx));
                output.setMessage("Successfully selected " + this.
                        getSearchedArtists().get(idx) + "'s page.");
                this.setCurrentPage(GlobalWaves.getInstance().getArtists().get(
                        this.getSelectedArtist()).getArtistpage());
                this.setHasSelection(false);
                return output;
            case "album":
                this.setSelectedAlbum(this.getSearchedAlbums().get(idx));
                output.setMessage("Successfully selected " + this.
                        getSearchedAlbums().get(idx).getName() + '.');
                break;
            default:
                System.out.println("Unknown selected item type");
                break;
        }
        this.setHasSelection(true);
        return output;
    }
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Listener listener)) {
            return false;
        }
        return this.getUsername().equals(listener.getUsername());
    }
}
