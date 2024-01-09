package globalwaves.audiofiles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import globalwaves.GlobalWaves;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static constants.Constants.PUBLIC;
@Getter
@Setter
public final class Playlist {
    private String name;
    @JsonIgnore private String owner;
    private List<String> songs;   // list for songs names
    @JsonIgnore private List<Song> songsList;
    private String visibility;
    private Integer followers = 0;
    @JsonIgnore private Integer idx = 0;
    @JsonIgnore private List<String> followersList = new ArrayList<>();
    @JsonIgnore private Integer timeStampCreated;

    public Playlist(final Album album) {
        this.name = album.getName();
        this.owner = album.getOwner();
        this.visibility = PUBLIC;
        this.songs = new ArrayList<>();
        this.songsList = new ArrayList<>();
        this.songsList.addAll(album.getSongs());
        for (Song song : this.songsList) {
            this.songs.add(song.getName());
        }
    }
    public Playlist(final String name) {
        this.name = name;
        this.visibility = PUBLIC;
        this.songs = new ArrayList<>();
        this.songsList = new ArrayList<>();
    }
    public Playlist(final Playlist playlist, final int seed) {
        this.name = playlist.name;
        this.owner = playlist.owner;
        this.visibility = playlist.visibility;
        this.followers = playlist.followers;

        this.songs = new ArrayList<>();
        this.songsList = new ArrayList<>();

        for (Song song : playlist.songsList) {
            this.songsList.add(new Song(song));
        }

        Collections.shuffle(this.songsList, new java.util.Random(seed));

        for (Song song : this.songsList) {
            this.songs.add(song.getName());
        }
    }
    /**
     * Apparently need this annotation, otherwise a "totalLikeCount"
     * field is added to the json
     * wasn't aware that methods are also serialized
     */
    @JsonIgnore
    public int getTotalLikeCount() {
        int totalLikeCount = 0;
        for (Song song : this.songsList) {
            String songName = song.getName();
            totalLikeCount += GlobalWaves.getInstance().
                    getUserInteractions().getSongLikeCountMap().getOrDefault(songName, 0);

        }
        return totalLikeCount;
    }
    /**
     * String format @Override for Playlist
     * used in LikedContentPage
     * @return String
     */
    @Override
    public String toString() {
        return this.name + " - " + this.owner;
    }
    /**
     *
     *
     * @param songName - song name to be checked
     * @return the index of the song in the playlist
     */
    public int getSongIdxByName(final String songName) {
        for (int i = 0; i < this.songsList.size(); i++) {
            if (this.songsList.get(i).getName().equals(songName)) {
                return i;
            }
        }
        return -1;
    }
    /**
     *
     * @param songIdx - song index
     * @return time until playlist ends from given song index
     */
    public int getTotalRemainingDurationFromIdx(final int songIdx) {
        int totalDuration = 0;
        for (int i = songIdx; i < this.songsList.size(); i++) {
            totalDuration += this.songsList.get(i).getDuration();
        }
        return totalDuration;
    }
    /**
     *
     * @param elapsedTime - elapsed time after updating the player timestamps
     * @param songIdx - song index
     * @return current playing song index in the playlist after a period of time
     */
    public int getSongIdxByElapsedTime(final int elapsedTime, final int songIdx) {
        int totalTime = 0;
        for (int i = songIdx; i < this.songsList.size(); i++) {
            totalTime += this.songsList.get(i).getDuration();
            if (totalTime > elapsedTime) {
                return i; // current song index
            }
        }
        // finished the playlist if it gets here
        return -1;
    }
    /**
     *
     * @param elapsedTime - elapsed time after updating the player timestamps
     * @param songIdx - song index
     * @return current timestamp in the current playing song after a period of time
     */
    public int getSongTimeStampByElapsedTime(final int elapsedTime, final int songIdx) {
        int totalTime = 0;
        for (int i = songIdx; i < this.songsList.size(); i++) {
            totalTime += this.songsList.get(i).getDuration();
            if (elapsedTime <= totalTime) {
                return elapsedTime - (totalTime - this.songsList.get(i).getDuration());
            }
        }
        return -1; // shouldn't get here
    }
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Playlist playlist) {
            return this.name.equals(playlist.name);
        }
        return false;
    }
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
