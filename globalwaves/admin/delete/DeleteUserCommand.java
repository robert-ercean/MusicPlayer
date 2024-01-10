package globalwaves.admin.delete;

import fileio.input.CommandInput;
import fileio.input.Library;
import globalwaves.GlobalWaves;
import globalwaves.admin.AdminCommand;
import globalwaves.audiofiles.LikedAudioFiles;
import globalwaves.audiofiles.Playlist;
import globalwaves.audiofiles.Song;
import globalwaves.users.artist.Artist;
import globalwaves.users.host.Host;
import globalwaves.users.listener.Listener;
import globalwaves.users.listener.player.Player;
import output.Output;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static globalwaves.users.listener.player.Player.updatePlayerTime;

public final class DeleteUserCommand implements AdminCommand {
    @Override
    public Output execute(final CommandInput command)  {
        for (Listener user : GlobalWaves.getInstance().getListeners().values()) {
            Player player = user.getUserPlayer();
            if (player != null) {
                updatePlayerTime(user, command.getTimestamp());
            }
        }
        Output output = Output.getOutputTemplate(command);
        String username = command.getUsername();

        String userType = getUserType(username);

        if (userType.isEmpty()) {
            output.setMessage("The username " + username + " doesn't exist.");
            return output;
        }
        if (GlobalWaves.getInstance().isInteracting(username)) {
            output.setMessage(username + " can't be deleted.");
            return output;
        }

        switch (userType) {
            case "user" -> {
                if (GlobalWaves.getInstance().getListeners().get(username).isPremium()) {
                    output.setMessage("User is premium.");
                    return output;
                }
                deleteNormalUser(username);
            }
            case "artist" -> deleteArtist(username);
            case "host" -> deleteHost(username);
            default -> System.out.println("Unknown user type");
        }

        output.setMessage(username + " was successfully deleted.");
        return output;
    }
    private void deleteNormalUser(final String username) {
        Listener user = this.getUsers().get(username);
        // Remove the user from song likes record
        user.getLikedContentPage().getLikedSongs().forEach(song -> {
            int currLikes = this.getUserInteractions().getSongLikeCountMap().get(song);
            this.getUserInteractions().getSongLikeCountMap().put(song, currLikes - 1);
        });
        // Remove the user from playlist followers records
        Iterator<Playlist> iterator = this.getUserInteractions().getPlaylists().iterator();
        while (iterator.hasNext()) {
            Playlist playlist = iterator.next();
            playlist.getFollowersList().remove(username);
            playlist.setFollowers(playlist.getFollowers() - 1);
            if (playlist.getOwner().equals(username)) {
                iterator.remove();
            }
        }
        // Remove the user's playlists from other users' followed playlists
        this.getUsers().keySet().forEach(key -> {
            Listener currUser = this.getUsers().get(key);
            currUser.getLikedContentPage().getFollowedPlaylists().
                    removeIf(playlist -> playlist.getOwner().equals(username));
        });
        this.getUsers().remove(username);
    }
    private void deleteArtist(final String username) {
        List<Song> artistSpecificSongs = this.getArtists().get(username).getAllArtistSongs();
        artistSpecificSongs.forEach(this.getUserInteractions().
                getSongLikeCountMap().keySet()::remove);
        for (Listener user : this.getUsers().values()) {
            user.getLikedContentPage().getLikedSongs().removeIf(artistSpecificSongs::contains);
        }
        this.getArtists().remove(username);
        this.getLibrary().getSongs().removeIf(song -> song.getArtist().equals(username));
    }
    private void deleteHost(final String username) {
        this.getLibrary().getPodcasts().removeIf(podcast -> podcast.getOwner().equals(username));
        this.getHosts().remove(username);
    }
    private String getUserType(final String username) {
        if (getUsers().containsKey(username)) {
            return "user";
        } else if (getArtists().containsKey(username)) {
            return "artist";
        } else if (getHosts().containsKey(username)) {
            return "host";
        }
        return "";
    }
    private Map<String, Listener> getUsers() {
        return GlobalWaves.getInstance().getListeners();
    }
    private Map<String, Artist> getArtists() {
        return GlobalWaves.getInstance().getArtists();
    }
    private Map<String, Host> getHosts() {
        return GlobalWaves.getInstance().getHosts();
    }
    private Library getLibrary() {
        return GlobalWaves.getInstance().getLibrary();
    }
    private LikedAudioFiles getUserInteractions() {
        return GlobalWaves.getInstance().getUserInteractions();
    }
}
