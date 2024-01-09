package globalwaves.audiofiles;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public final class LikedAudioFiles {
    private HashMap<Song, Integer> songLikeCountMap = new HashMap<>();
    private List<Playlist> playlists = new ArrayList<>();

    /**
     * @param ownerName - for parsing the owner specific playlists
     * @return the owner specific list of playlists
     */
    public List<Playlist> getOwnerSpecificPlaylists(final String ownerName) {
        List<Playlist> ownerPlaylists = new ArrayList<>();
        for (Playlist playlist : this.getPlaylists()) {
            if (playlist.getOwner().equals(ownerName)) {
                ownerPlaylists.add(playlist);
            }
        }
        return ownerPlaylists;
    }
}
