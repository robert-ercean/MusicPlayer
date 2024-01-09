package output;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
/**
 * Helper class to display the "result" field of the output variable
 * as shown in the homework statement.
 * Contains all the possible fields of all the statistics classes.
 * (Stats for listeners, hosts and artists)
 * The fields are set in the respective classes and then added to the output.
 * Additional fields (not corresponding to the command) are ignored.
 */
@Setter
@Getter
public class WrappedStatsForOutput {
    private Integer listeners;
    private Map<String, Integer> topAlbums;
    private List<String> topFans;
    private Map<String, Integer> topSongs;
    private Map<String, Integer> topArtists;
    private Map<String, Integer> topEpisodes;
    private Map<String, Integer> topGenres;

    public WrappedStatsForOutput() {
    }

}
