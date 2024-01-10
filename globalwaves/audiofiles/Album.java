package globalwaves.audiofiles;

import globalwaves.GlobalWaves;
import globalwaves.users.listener.Listener;
import globalwaves.users.listener.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Getter
@Setter
public final class Album {
    private final String owner;
    private final String name;
    private final Integer releaseYear;
    private final String description;
    private final List<Song> songs;
    public Album(final String name, final Integer releaseYear, final String description,
                 final List<Song> songs, final String owner) {
        this.name = name;
        this.releaseYear = releaseYear;
        this.description = description;
        this.songs = songs;
        this.owner = owner;
    }
    /**
     * @return a list of the album's songs' names
     */
    public static List<String> albumsNamesToStringList(final List<Album> albums) {
        List<String> albumsNames = new ArrayList<>();
        for (Album album : albums) {
            albumsNames.add(album.getName());
        }
        return albumsNames;
    }

    /**
     * @return total like count of all songs on the album
     */
    public int getTotalLikeCount() {
        int totalLikeCount = 0;
        for (Song song : this.songs) {
            totalLikeCount += GlobalWaves.getInstance().
                    getUserInteractions().getSongLikeCountMap().getOrDefault(song, 0);
        }
        return totalLikeCount;
    }

    /**
     * Checks to see if the album is loaded, used when removing an album
     */
    public boolean isLoaded() {
        for (Listener user : GlobalWaves.getInstance().getListeners().values()) {
            Player player = user.getUserPlayer();
            if (player != null && player.getCurrentSong() != null
                    && player.getCurrentSong().getAlbum().equals(this.name)) {
                return true;
            }
            // now check if the playlist loaded has a song from the album
            if (player != null && player.getCurrentPlaylist() != null) {
                for (Song song : player.getCurrentPlaylist().getSongsList()) {
                    if (song.getAlbum().equals(this.name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * equals override to compare albums by their name and owner
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Album album)) {
            return false;
        }
        return Objects.equals(getName(), album.getName());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getName(), getOwner());
    }
}
