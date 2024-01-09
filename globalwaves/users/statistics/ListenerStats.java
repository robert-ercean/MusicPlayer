package globalwaves.users.statistics;

import fileio.input.CommandInput;
import fileio.input.EpisodeInput;
import globalwaves.GlobalWaves;
import globalwaves.audiofiles.Song;
import globalwaves.users.listener.Listener;
import globalwaves.users.listener.player.Player;
import lombok.Getter;
import output.Output;
import output.WrappedStatsForOutput;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ListenerStats extends UserStatsObserver {
    private final Map<String, Integer> topArtists;
    private final Map<String, Integer> topGenres;
    private final Map<String, Integer> topSongs;
    private final Map<String, Integer> topAlbums;
    private final Map<String, Integer> topEpisodes;
    /**
     * key: artist name
     * value: number of times the user listened to the artist's songs
     *        while the user had a premium subscription
     */
    private final Map<String, Integer> premiumListensToArtist;
    /**
     * Map used to store the number of times the user listened to a song
     * specific to an artist while the user had a premium subscription
     * key: artist name
     * value: map of songs (key = song name, value = number of listens)
     */
    private final Map<String, Map<String, Integer>> premiumListensToSong;
    public ListenerStats(String username) {
        topArtists = new HashMap<>();
        topGenres = new HashMap<>();
        topSongs = new HashMap<>();
        topAlbums = new HashMap<>();
        topEpisodes = new HashMap<>();
        premiumListensToArtist = new HashMap<>();
        premiumListensToSong = new HashMap<>();
        super.username = username;
    }
    @Override
    public Output display(CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (this.isEmpty()) {
            output.setMessage("No data to show for user " + super.username + ".");
            return output;
        }
        WrappedStatsForOutput result = new WrappedStatsForOutput();
        result.setTopArtists(getSortedMap(topArtists));
        result.setTopGenres(getSortedMap(topGenres));
        result.setTopAlbums(getSortedMap(topAlbums));
        result.setTopEpisodes(getSortedMap(topEpisodes));
        result.setTopSongs(getSortedMap(topSongs));
        output.setResult(result);
        return output;
    }
    @Override
    public void update(String eventType, Player userPlayer, int idx) {
        switch (eventType) {
            case "song", "playlist" -> processMusicEvent(userPlayer, eventType, idx);
            case "episode" -> processPodcastEvent(userPlayer, idx);
        }
    }
    private void processMusicEvent(Player userPlayer, String eventType, int idx) {
        Song song = eventType.equals("song") ? userPlayer.getCurrentSong()
                : userPlayer.getCurrentPlaylist().getSongsList().get(idx);
        updateMusicStatistics(song);
        Listener listener = GlobalWaves.getInstance().getUsers().get(userPlayer.getOwner());
        if (listener.isPremium()) {
            premiumListensToArtist.merge(song.getArtist(), 1, Integer::sum);
            premiumListensToSong.putIfAbsent(song.getArtist(), new HashMap<>());
            premiumListensToSong.get(song.getArtist()).merge(song.getName(), 1, Integer::sum);
        }
    }
    private void processPodcastEvent(Player userPlayer, int idx) {
        EpisodeInput episode = (EpisodeInput) userPlayer.getCurrentPodcast().getEpisodes().get(idx);
        topEpisodes.merge(episode.getName(), 1, Integer::sum);
    }
    private void updateMusicStatistics(Song song) {
        topSongs.merge(song.getName(), 1, Integer::sum);
        topAlbums.merge(song.getAlbum(), 1, Integer::sum);
        topArtists.merge(song.getArtist(), 1, Integer::sum);
        topGenres.merge(song.getGenre(), 1, Integer::sum);
    }
    @Override
    public boolean isEmpty() {
        return topArtists.isEmpty() && topGenres.isEmpty() && topSongs.isEmpty()
                && topAlbums.isEmpty() && topEpisodes.isEmpty();
    }
}
