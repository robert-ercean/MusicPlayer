package globalwaves.users.artist;

import globalwaves.audiofiles.Album;
import globalwaves.audiofiles.Song;
import fileio.input.CommandInput;
import fileio.input.SongInput;
import globalwaves.GlobalWaves;
import globalwaves.users.User;
import globalwaves.users.listener.notifications.Notifications;
import globalwaves.users.listener.notifications.NotificationsObserver;
import globalwaves.users.statistics.ArtistStats;
import globalwaves.users.listener.Listener;
import globalwaves.users.listener.player.Player;
import lombok.Getter;
import lombok.Setter;
import globalwaves.pages.artist.ArtistPage;
import globalwaves.pages.Page;
import globalwaves.pages.factory.PageFactory;
import output.Output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public final class Artist extends User implements Notifications {
    private final Page artistpage;
    private final ArtistStats stats;
    private List<NotificationsObserver> notificationsObservers;
    public Artist(final CommandInput command) {
        super.setUsername(command.getUsername());
        super.setAge(command.getAge());
        super.setCity(command.getCity());
        this.artistpage = PageFactory.createPage(null, "artist", super.getUsername());
        this.stats = new ArtistStats(super.getUsername());
        this.notificationsObservers = new ArrayList<>();
    }
    @Override
    public String registerSubscriber(NotificationsObserver o) {
        if (this.notificationsObservers.contains(o)) {
            return removeSubscriber(o);
        }
        this.notificationsObservers.add(o);
        return o.getUsername() + " subscribed to " + this.getUsername() + " successfully.";
    }
    @Override
    public String removeSubscriber(NotificationsObserver o) {
        this.notificationsObservers.remove(o);
        return o.getUsername() + " unsubscribed from " + this.getUsername() + " successfully.";
    }
    @Override
    public void notifySubscribers(String eventType) {
        for (NotificationsObserver o : this.notificationsObservers) {
            o.update(eventType);
        }
    }

    @Override
    public Output wrapped(CommandInput command) {
        return this.stats.display(command);
    }
    public int getTotalLikeCount() {
        return ((ArtistPage) this.artistpage).getTotalLikeCount();
    }
    /**
     * Returns a list of all the songs of the artist.
     */
    public List<Song> getAllArtistSongs() {
        List<Album> albums = ((ArtistPage) this.artistpage).getAlbums();
        List<Song> songs = new ArrayList<>();
        for (Album album : albums) {
            songs.addAll(album.getSongs());
        }
        return songs;
    }
    /**
     * Adds a new event to the artist's page.
     */
    public String addEvent(final CommandInput command) {
        String name = command.getName();
        String date = command.getDate();
        String description = command.getDescription();

        Event event = new Event(name, date, description);

        ArtistPage artistPage = (globalwaves.pages.artist.ArtistPage) this.artistpage;

        if (artistPage.getEvents().contains(event)) {
            return (command.getUsername() + "has another event with the same name.");
        } else if (!Event.checkDateValidity(date)) {
            return ("Event for " + command.getUsername() + " does not have a valid date.");
        }
        artistPage.getEvents().add(event);
        return (command.getUsername() + " has added new event successfully.");
    }
    /**
     * Remove an event from the artist's page.
     */
    public String removeEvent(final CommandInput command) {
        String artistName = command.getUsername();
        String eventName = command.getName();
        ArtistPage artistPage = (ArtistPage) this.artistpage;
        for (Event event : artistPage.getEvents()) {
            if (event.getName().equals(eventName)) {
                artistPage.getEvents().remove(event);
                return (artistName + " deleted the event successfully.");
            }
        }
        return (artistName + " doesn't have an event with the given name.");
    }
    /**
     * Adds merchandise to the artist's page.
     */
    public String addMerch(final CommandInput command) {
        String name = command.getName();
        String description = command.getDescription();
        int price = command.getPrice();

        Merch merch = new Merch(name, description, price);

        ArtistPage artistPage = (globalwaves.pages.artist.ArtistPage) this.artistpage;

        if (artistPage.getMerch().contains(merch)) {
            return (command.getUsername() + " has merchandise with the same name.");
        } else if (price < 0) {
            return ("Price for merchandise can not be negative.");
        }
        artistPage.getMerch().add(merch);
        return (command.getUsername() + " has added new merchandise successfully.");
    }
    /**
     * Add a new album to the artist's page and to the library.
     */
    public String addAlbum(final CommandInput command) {
        String albumName = command.getName();
        String artistName = command.getUsername();
        int releaseYear = command.getReleaseYear();
        String description = command.getDescription();
        List<Song> songs = command.getSongs();

        Album album = new Album(albumName, releaseYear, description, songs, this.getUsername());

        ArtistPage artistPage = (globalwaves.pages.artist.ArtistPage) this.artistpage;

        if (artistPage.getAlbums().contains(album)) {
            return (artistName + " has another album with the same name.");
        }
        Set<Song> uniqueSongs = new HashSet<>();

        for (Song song : songs) {
            // add each song to the HashSet
            if (!uniqueSongs.add(song)) {
                // if the add method returns false, the song is a duplicate
                return (artistName + " has the same song at least twice in this album.");
            }
        }
        // if the album contains no duplicates we add the songs to the library
        for (Song song : songs) {
            GlobalWaves.getInstance().getLibrary().getSongs().add(new SongInput(song));
        }
        artistPage.getAlbums().add(album);
        return (artistName + " has added new album successfully.");
    }
    /**
     * Show the artist's albums.
     */
    public List<Map<String, Object>> showAlbums() {
        List<Album> albums = this.getArtistpage().getAlbums();
        List<Map<String, Object>> albumMaps = new ArrayList<>();

        for (Album album : albums) {
            Map<String, Object> albumMap = new HashMap<>();
            albumMap.put("name", album.getName());
            List<String> songNames = new ArrayList<>();
            for (Song song : album.getSongs()) {
                songNames.add(song.getName());
            }
            albumMap.put("songs", songNames);
            albumMaps.add(albumMap);
        }
        return albumMaps;
    }
    /**
     * Remove an album from the artist's page and from the library.
     */
    public String removeAlbum(final CommandInput command) {
        String albumName = command.getName();
        String artistName = command.getUsername();

        Album album = new Album(albumName, 0, "", new ArrayList<>(), artistName);

        ArtistPage artistPage = (ArtistPage) this.artistpage;
        if (!artistPage.getAlbums().contains(album)) {
            return (artistName + " doesn't have an album with the given name.");
        }
        album = artistPage.getAlbums().get(artistPage.getAlbums().indexOf(album));
        if (album.isLoaded()) {
            return (artistName + " can't delete this album.");
        }
        artistPage.getAlbums().remove(album);
        return (artistName + " has deleted the album successfully.");
    }
    public ArtistPage getArtistpage() {
        return (ArtistPage) this.artistpage;
    }
    @Override
    public boolean isInteracting() {
        for (Listener user : GlobalWaves.getInstance().getListeners().values()) {
            if (user.getCurrentPage().getOwner().equals(super.getUsername())) {
                return true;
            }
            Player player = user.getUserPlayer();
            if (Player.isEmpty(player)) {
                continue;
            }
            if (player.getCurrentPlaylist() != null
                    && player.getCurrentPlaylist().getOwner().equals(super.getUsername())) {
                return true;
            }
            if (player.getCurrentSong() != null
                    && player.getCurrentSong().getArtist().equals(super.getUsername())) {
                return true;
            }
        }
        return false;
    }
}
