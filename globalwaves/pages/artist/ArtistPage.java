package globalwaves.pages.artist;

import globalwaves.GlobalWaves;
import globalwaves.audiofiles.Album;
import globalwaves.users.artist.Artist;
import globalwaves.users.artist.Event;
import globalwaves.users.artist.Merch;
import lombok.Getter;
import lombok.Setter;
import globalwaves.pages.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public final class ArtistPage extends Page {
    private final List<Merch> merch;
    private final List<Event> events;
    private final List<Album> albums;
    public ArtistPage(final String owner) {
        super(owner);
        this.events = new ArrayList<>();
        this.merch = new ArrayList<>();
        this.albums = new ArrayList<>();
    }

    /**
     * @return "User" class instance of the owner of this page
     */
    @Override
    public Artist getOwner() {
        return GlobalWaves.getInstance().getArtists().get(super.owner);
    }
    /**
     * @return the total number of likes for all the albums
     */
    public int getTotalLikeCount() {
        int totalLikeCount = 0;
        for (Album album : this.albums) {
            totalLikeCount += album.getTotalLikeCount();
        }
        return totalLikeCount;
    }
    /**
     * @return a string formatted suitable for an artist page
     */
    @Override
    public String display() {
        return "Albums:\n\t["
                + albumsToString() + ']'
                + "\n\nMerch:\n\t["
                + merchToString() + ']'
                + "\n\nEvents:\n\t["
                + eventsToString() + ']';
    }
    /**
     * @return a string containing all the albums names
     */
    public String albumsToString() {
        StringBuilder albumsNames = new StringBuilder();
        for (Album album : this.albums) {
            albumsNames.append(album.getName());
            albumsNames.append(", ");
        }
        // remove the last comma and space
        albumsNames.delete(albumsNames.length() - 2, albumsNames.length());
        return albumsNames.toString();
    }
    /**
     * @return a string containing all the events
     */
    public String eventsToString() {
        StringBuilder eventsString = new StringBuilder();
        for (Event event : this.events) {
            eventsString.append(event.getName());
            eventsString.append(" - ");
            eventsString.append(event.getDate());
            eventsString.append(":\n\t");
            eventsString.append(event.getDescription());
            eventsString.append(", ");
        }
        if (eventsString.isEmpty()) {
            return "";
        }
        // remove the last comma and space
        eventsString.delete(eventsString.length() - 2, eventsString.length());
        return eventsString.toString();
    }
    /**
     * @return a string containing all the merch
     */
    public String merchToString() {
        StringBuilder merchString = new StringBuilder();
        for (Merch merch1 : this.merch) {
            merchString.append(merch1.getName());
            merchString.append(" - ");
            merchString.append(merch1.getPrice());
            merchString.append(":\n\t");
            merchString.append(merch1.getDescription());
            merchString.append(", ");
        }
        if (merchString.isEmpty()) {
            return "";
        }
        // remove the last comma and space
        merchString.delete(merchString.length() - 2, merchString.length());
        return merchString.toString();
    }
}
