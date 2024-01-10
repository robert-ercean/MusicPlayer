package globalwaves.users.listener.search;

import fileio.input.Filter;
import globalwaves.GlobalWaves;
import globalwaves.audiofiles.Playlist;
import fileio.input.CommandInput;
import globalwaves.users.listener.Listener;
import output.Output;

import java.util.ArrayList;
import java.util.List;

import static constants.Constants.MAX;
import static constants.Constants.PRIVATE;

public final class PlaylistSearchStrategy implements SearchStrategy {
    @Override
    public Output search(final CommandInput command) {
        Listener user = GlobalWaves.getInstance().getListeners().get(command.getUsername());
        List<Playlist> allPlaylists = GlobalWaves.getInstance().
                getUserInteractions().getPlaylists();
        List<Playlist> matchingPlaylists = new ArrayList<>();
        for (Playlist playlist : allPlaylists) {
            if (matchesFiltersForPlaylists(playlist, command)) {
                matchingPlaylists.add(playlist);
            }
            if (matchingPlaylists.size() == MAX) {
                break;
            }
        }
        List<String> matchingPlaylistsNames = new ArrayList<>();
        for (Playlist playlist : matchingPlaylists) {
            matchingPlaylistsNames.add(playlist.getName());
        }
        Output searchOutputPlaylist = new Output(
                command.getCommand(),
                command.getUsername(),
                command.getTimestamp(),
                "Search returned " + matchingPlaylists.size() + " results",
                matchingPlaylistsNames
        );
        user.setSelectedItemType("playlist");
        user.setSearchedPlaylists(matchingPlaylists);
        return searchOutputPlaylist;
    }
    /**
     * Checks if a playlist matches the filters given in the command.
     */
    public static boolean matchesFiltersForPlaylists(
            final Playlist playlist, final CommandInput command) {
        Filter filters = command.getFilters();
        if (filters.getName() != null && !playlist.getName().startsWith(filters.getName())) {
            return false;
        }
        if (filters.getOwner() != null && !playlist.getOwner().equals(filters.getOwner())) {
            return false;
        }
        return playlist.getOwner().equals(command.getUsername())
                || !playlist.getVisibility().equals(PRIVATE);
    }
}
