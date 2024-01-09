package globalwaves.audiofiles;

import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Objects;

@Getter
@Setter
public final class Song {
    private String name;
    private Integer duration;
    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private Integer releaseYear;
    private String artist;

    public Song() {
    }

    public Song(final SongInput songInput) {
        this.name = songInput.getName();
        this.duration = songInput.getDuration();
        this.album = songInput.getAlbum();
        this.tags = songInput.getTags();
        this.lyrics = songInput.getLyrics();
        this.genre = songInput.getGenre();
        this.releaseYear = songInput.getReleaseYear();
        this.artist = songInput.getArtist();
    }
    public Song(final Song song) {
        this.name = song.name;
        this.duration = song.duration;
        this.album = song.album;
        this.tags = song.tags;
        this.lyrics = song.lyrics;
        this.genre = song.genre;
        this.releaseYear = song.releaseYear;
        this.artist = song.artist;
    }
    @Override
    public String toString() {
        return this.name + " - " + this.artist;
    }
    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj The object to compare with.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true; // the same instance
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; // null or different class
        }
        Song song = (Song) obj;
        // compare name, artist and album
        return Objects.equals(name, song.name)
                && Objects.equals(artist, song.artist)
                && Objects.equals(album, song.album);
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit
     * of hash tables such as those provided by {@link java.util.HashMap}.
     *
     * @return A hash code value for this object based on its name and artist fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, artist, album);
    }
}
