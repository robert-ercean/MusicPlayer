package globalwaves.users.statistics;

import fileio.input.CommandInput;
import globalwaves.GlobalWaves;
import globalwaves.audiofiles.Song;
import globalwaves.users.listener.Listener;
import globalwaves.users.listener.player.Player;
import lombok.Getter;
import output.Output;
import output.WrappedStatsForOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ArtistStats extends UserStatsObserver {
    private final Map<String, Integer> topAlbums;
    private final Map<String, Integer> topSongs;
    private final Map<String, Integer> topFans;
    private final MonetizationStats monetizationStats;
    private int mostProfitableSongValue;
    /**
     * Map used to store the revenue for each song
     * key: song name
     * value: revenue
     */
    public ArtistStats(String username) {
        this.topAlbums = new HashMap<>();
        this.topSongs = new HashMap<>();
        this.topFans = new HashMap<>();
        this.monetizationStats = new MonetizationStats();
        super.username = username;
    }
    @Override
    public Output display(CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (this.isEmpty()) {
            output.setMessage("No data to show for artist " + super.username + ".");
            return output;
        }
        WrappedStatsForOutput result = new WrappedStatsForOutput();
        // add the respective fields and limit each map to 5 entries without ordering
        result.setListeners(this.topFans.keySet().size());
        result.setTopAlbums(getSortedMap(topAlbums));
        result.setTopSongs(getSortedMap(topSongs));
        result.setTopFans(getSortedList(topFans));
        output.setResult(result);
        return output;
    }
    @Override
    public void update(String eventType, Player userPlayer, int idx) {
        switch (eventType) {
            case "song", "playlist" -> {
                Song song = eventType.equals("song") ? userPlayer.getCurrentSong()
                        : userPlayer.getCurrentPlaylist().getSongsList().get(idx);
                if (song.getArtist().equals(super.username)) {
                    topSongs.merge(song.getName(), 1, Integer::sum);
                    topAlbums.merge(song.getAlbum(), 1, Integer::sum);
                    Listener listener = GlobalWaves.getInstance().getUsers().get(userPlayer.getOwner());
                    topFans.merge(listener.getUsername(), 1, Integer::sum);
                }
            }
        }
    }
    @Override
    public boolean isEmpty() {
        return topAlbums.isEmpty() && topSongs.isEmpty() && topFans.isEmpty();
    }
    public MonetizationStats getMonetizationStats() {
        MonetizationStats monetizationStats = this.monetizationStats;
        Map<String, Listener> users = GlobalWaves.getInstance().getUsers();
        for (Listener listener : users.values()) {
            ListenerStats listenerStats = listener.getStats();
            double val = 0.0;
            double songsTotal = listenerStats.getPremiumListensToArtist().values().stream().mapToDouble(Integer::doubleValue).sum();
            double songsArtist = listenerStats.getPremiumListensToArtist().getOrDefault(super.username, 0);
            if (songsArtist == 0) {
                continue;
            }
            val = 1000000 * songsArtist / songsTotal;
            monetizationStats.songRevenue += val;
        }
        monetizationStats.songRevenue = Math.round(monetizationStats.songRevenue * 100.0) / 100.0;
        return monetizationStats;
    }
}
