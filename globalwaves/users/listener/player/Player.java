package globalwaves.users.listener.player;

import globalwaves.GlobalWaves;
import globalwaves.audiofiles.Album;
import globalwaves.audiofiles.Podcast;
import globalwaves.audiofiles.PodcastState;
import globalwaves.audiofiles.Playlist;
import globalwaves.audiofiles.Song;
import globalwaves.users.artist.Artist;
import globalwaves.users.host.Host;
import globalwaves.users.listener.Listener;
import lombok.Getter;
import lombok.Setter;

import static constants.Constants.NO_REPEAT;
import static constants.Constants.REPEAT_ALL;
import static constants.Constants.REPEAT_CURRENT_SONG;
import static constants.Constants.REPEAT_INFINITE;
import static constants.Constants.REPEAT_ONCE;
import static globalwaves.users.listener.player.PlayerTimeUpdateStrategy.getTimeUpdateStrategy;


@Getter
@Setter
public class Player {
    private String owner;
    private Podcast currentPodcast;
    private Song currentSong;
    private String audioFileName;
    private Playlist currentPlaylist;
    private int remainedTime; // Time remaining for the current playing media
    private boolean paused; // Playback status
    private String repeatStatus = NO_REPEAT; // Repeat status
    private boolean shuffleStatus = false; // Shuffle status
    private Playlist shuffledPlaylist; // Shuffled playlist
    /**
     * For changing the shuffle status, swap the current playlist with the
     * shuffled playlist and vice versa. This way, i don't have to change the way i play the
     * media, I just have to change the playlist i'm playing from.
     */
    public void changeShuffleStatus(final boolean newShuffleStatus, final int seed) {
        this.shuffleStatus = newShuffleStatus;
        if (this.shuffleStatus && this.currentPlaylist != null) {
            this.shuffledPlaylist = new Playlist(this.currentPlaylist, seed);
            this.shuffledPlaylist.setIdx(this.shuffledPlaylist.
                    getSongIdxByName(this.currentSong.getName()));

            Playlist tmp = this.currentPlaylist;
            this.currentPlaylist = this.shuffledPlaylist;
            this.shuffledPlaylist = tmp;

        } else if (!this.shuffleStatus && this.shuffledPlaylist != null) {
            this.currentPlaylist = this.shuffledPlaylist;
            this.currentPlaylist.setIdx(this.currentPlaylist.
                    getSongIdxByName(this.currentSong.getName()));
            this.shuffledPlaylist = null;
        }
    }
    public Player(final Album album, final String owner) {
        this.owner = owner;
        this.currentPlaylist = new Playlist(album);
        this.audioFileName = album.getSongs().get(0).getName();
        this.remainedTime = album.getSongs().get(0).getDuration();
        this.currentSong = album.getSongs().get(0);
        Listener user = GlobalWaves.getInstance().getListeners().get(owner);
        Artist artist = GlobalWaves.getInstance().getArtists().get(album.getOwner());
        user.registerStatsObserver(artist.getStats());
    }
    public Player(final Song song, final String owner) {
        this.owner = owner;
        this.currentSong = song;
        this.remainedTime = song.getDuration();
        this.paused = false;
        this.audioFileName = song.getName();
        Listener user = GlobalWaves.getInstance().getListeners().get(owner);
        Artist artist = GlobalWaves.getInstance().getArtists().get(song.getArtist());
        user.registerStatsObserver(artist.getStats());
    }
    public Player(final Podcast podcast, final String owner) {
        this.owner = owner;
        this.currentPodcast = podcast;
        this.remainedTime = podcast.getEpisodes().get(0).getDuration();
        this.audioFileName = podcast.getEpisodes().get(0).getName();
        this.paused = false;
        Listener user = GlobalWaves.getInstance().getListeners().get(owner);
        Host host = GlobalWaves.getInstance().getHosts().get(podcast.getOwner());
        user.registerStatsObserver(host.getStats());
    }
    /** Don't register any observer here, because if we have an existing podcast state,
     *  it means that the user has already registered the host's stats as an observer
     */
    public Player(final Podcast podcast, final PodcastState podcastState, final String owner) {
        this.owner = owner;
        this.currentPodcast = podcast;
        this.audioFileName = podcast.getEpisodes().get(podcastState.getLastEpisodeIdx()).getName();
        this.remainedTime = podcastState.getRemainingTime();
        this.currentPodcast.setIdx(podcastState.getLastEpisodeIdx());
    }
    public Player(final Playlist playlist, final String owner) {
        this.owner = owner;
        this.currentPlaylist = playlist;
        this.audioFileName = playlist.getSongsList().get(0).getName();
        this.remainedTime = playlist.getSongsList().get(0).getDuration();
        this.currentSong = playlist.getSongsList().get(0);
        Listener user = GlobalWaves.getInstance().getListeners().get(owner);
        Artist artist = GlobalWaves.getInstance().getArtists().get(playlist.getOwner());
        user.registerStatsObserver(artist.getStats());
    }
    /**
     * Changes the repeat status of the current media, depending on the type of media and
     * the current repeat status.
     */
    public void changeRepeatStatus() {
        switch (this.repeatStatus) {
            case NO_REPEAT:
                this.repeatStatus = this.currentPlaylist != null ? REPEAT_ALL : REPEAT_ONCE;
                break;
            case REPEAT_ALL:
            case REPEAT_ONCE:
                this.repeatStatus = this.currentPlaylist != null
                        ? REPEAT_CURRENT_SONG : REPEAT_INFINITE;
                break;
            case REPEAT_CURRENT_SONG:
            case REPEAT_INFINITE:
                this.repeatStatus = NO_REPEAT;
                break;
            default:
                System.out.println("Unknown repeat status");
        }
    }
    /**
     * Checks to see if the given player has any audio file playing
     * if not, we consider it "empty"
     * @param player - player to be checked
     */
    public static boolean isEmpty(final Player player) {
        if (player == null) {
            return true;
        }
        return player.getAudioFileName().isEmpty();
    }
    /**
     * Updates the player time for the given listener
     * @param user - user to update the player time for
     * @param currentTimestamp - current timestamp
     */
    public static void updatePlayerTime(final Listener user, final int currentTimestamp) {
        if (user == null) {
            return;
        }
        Player userPlayer = user.getUserPlayer();
        if (!user.isConnected() || userPlayer == null || userPlayer.isPaused()) {
            user.setLastCommandTimestamp(currentTimestamp);
            return;
        }
        int elapsedTime = currentTimestamp - user.getLastCommandTimestamp();
        user.setLastCommandTimestamp(currentTimestamp);

        userPlayer.setRemainedTime(user.getUserPlayer().getRemainedTime()
                - elapsedTime);
        if (userPlayer.getRemainedTime() <= 0) {
            PlayerTimeUpdateStrategy timeUpdateStrategy =
                    getTimeUpdateStrategy(user.getSelectedItemType());
            if (timeUpdateStrategy != null) {
                timeUpdateStrategy.updateTime(user, elapsedTime);
            } else {
                System.out.println("Unknown selected item type");
            }
        }
        // Update the user's podcast state if needed
        if (user.getUserPlayer() != null && user.getUserPlayer().getCurrentPodcast()
                != null && user.getSelectedItemType().equals("podcast")) {
            int epIdx = user.getUserPlayer().getCurrentPodcast().getIdx();
            int timeIdx = user.getUserPlayer().getRemainedTime();
            user.updatePodcastState(user.getUserPlayer().getCurrentPodcast().
                    getName(), epIdx, timeIdx);
        }
    }
}
