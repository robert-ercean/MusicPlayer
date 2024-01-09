package globalwaves.pages.user;

import globalwaves.audiofiles.Playlist;
import globalwaves.audiofiles.Song;
import globalwaves.GlobalWaves;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static constants.Constants.MAX;

@Getter
@Setter
public final class HomePage extends LikedContentPage {
    private final List<Song> likedSongs; // top 5 by number of likes
    private final List<Playlist> followedPlaylists; // top 5 by total number of likes per song

    @Override
    public String display() {
        this.updateContent();
        return "Liked songs:\n\t"
                + getSongsToString()
                + "\n\nFollowed playlists:\n\t"
                + getPlaylistsToString();
    }
    public HomePage(final String owner) {
        super(owner);
        this.likedSongs = new ArrayList<>();
        this.followedPlaylists = new ArrayList<>();
    }
    /**
     * Updates the content of the likedSongs and followedPlaylists lists
     */
    private void updateContent() {
        // Clear the existing lists
        this.likedSongs.clear();
        this.followedPlaylists.clear();

        List<Song> allLikedSongs = GlobalWaves.getInstance().getUsers().
                get(super.getOwner()).getLikedContentPage().getLikedSongs();
        List<Playlist> allFollowedPlaylists = GlobalWaves.getInstance().getUsers().
                get(super.getOwner()).getLikedContentPage().getFollowedPlaylists();
        this.likedSongs.addAll(
            allLikedSongs.stream()
            .sorted(Comparator.comparing(song -> GlobalWaves.getInstance().
            getUserInteractions().getSongLikeCountMap().getOrDefault(song, 0),
            Comparator.reverseOrder())).limit(MAX).toList()
        );

        this.followedPlaylists.addAll(allFollowedPlaylists.stream()
            .sorted(Comparator.comparing(Playlist::getTotalLikeCount).reversed()).toList());
    }

    /**
     * only the top 5 songs by number of likes
     */
    @Override
    public List<String> getSongsToString()  {
        return this.likedSongs.stream().map(Song::getName).collect(Collectors.toList());
    }
    /**
     * only the top 5 playlists by total number of likes per song
     */
    @Override
    public List<String> getPlaylistsToString()  {
        return this.followedPlaylists.stream().map(Playlist::getName).collect(Collectors.toList());
    }
}
