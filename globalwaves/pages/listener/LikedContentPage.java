package globalwaves.pages.listener;

import globalwaves.GlobalWaves;
import globalwaves.audiofiles.Playlist;
import globalwaves.audiofiles.Song;
import globalwaves.users.User;
import lombok.Getter;
 import lombok.Setter;
import globalwaves.pages.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class LikedContentPage extends Page {
    private List<Playlist> followedPlaylists;
    private List<Song> likedSongs;
    public LikedContentPage(final String owner) {
        super(owner);
        this.followedPlaylists = new ArrayList<>();
        this.likedSongs = new ArrayList<>();
    }
    /**
     * @return "User" class instance of the owner of this page
     */
    public User getOwner() {
        return GlobalWaves.getInstance().getListeners().get(super.owner);
    }
    /**
     * @return a string containing the liked songs and followed playlists
     */
    @Override
    public String display() {
        return "Liked songs:\n\t"
                + this.getSongsToString()
                + "\n\nFollowed playlists:\n\t"
                + this.getPlaylistsToString();
    }
    /**
     * @return a list of strings containing the liked songs' names and artists
     */
    public List<String> getSongsToString()  {
        return this.likedSongs.stream().map(Song::toString).collect(Collectors.toList());
    }
    /**
     * @return a list of strings containing the followed playlists' names and creators
     */
    public List<String> getPlaylistsToString()  {
        return this.followedPlaylists.stream().map(Playlist::toString).collect(Collectors.toList());
    }
}
