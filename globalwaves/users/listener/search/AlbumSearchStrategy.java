package globalwaves.users.listener.search;

import fileio.input.Filter;
import globalwaves.audiofiles.Album;
import fileio.input.CommandInput;
import globalwaves.GlobalWaves;
import globalwaves.users.listener.Listener;
import output.Output;

import java.util.ArrayList;
import java.util.List;

import static constants.Constants.MAX;

public final class AlbumSearchStrategy implements SearchStrategy {
    @Override
    public Output search(final CommandInput command) {
        Listener user = GlobalWaves.getInstance().getListeners().get(command.getUsername());
        List<Album> allAlbums = GlobalWaves.getInstance().getAllAlbums();
        List<Album> matchingAlbums = new ArrayList<>();
        for (Album album : allAlbums) {
            if (matchesFiltersForAlbums(album, command)) {
                matchingAlbums.add(album);
            }
            if (matchingAlbums.size() == MAX) {
                break;
            }
        }
        Output output = Output.getOutputTemplate(command);
        output.setResults(Album.albumsNamesToStringList(matchingAlbums));
        user.setSelectedItemType("album");
        user.setSearchedAlbums(matchingAlbums);
        output.setMessage("Search returned " + matchingAlbums.size() + " results");
        return output;
    }
    /**
     * Checks if an album matches the filters given in the command
     */
    public static boolean matchesFiltersForAlbums(final Album album, final CommandInput command) {
        Filter filters = command.getFilters();
        if (filters.getName() != null && !album.getName().startsWith(filters.getName())) {
            return false;
        }
        if (filters.getOwner() != null && !album.getOwner().equals(filters.getOwner())) {
            return false;
        }
        return filters.getDescription() == null || album.getDescription().
                contains(filters.getDescription());
    }
}
