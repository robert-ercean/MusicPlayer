package globalwaves.users.listener.search;

import fileio.input.CommandInput;
import globalwaves.GlobalWaves;
import globalwaves.users.listener.Listener;
import output.Output;

import java.util.ArrayList;
import java.util.List;

import static constants.Constants.MAX;

public final class ArtistSearchStrategy implements SearchStrategy {
    @Override
    public Output search(final CommandInput command) {
        Listener user = GlobalWaves.getInstance().getListeners().get(command.getUsername());
        List<String> artists = new ArrayList<>();
        for (String artist : GlobalWaves.getInstance().getArtists().keySet()) {
            if (artist.toLowerCase().startsWith(command.getFilters().getName().toLowerCase())) {
                artists.add(artist);
            }
            if (artists.size() == MAX) {
                break;
            }
        }
        user.setSearchedArtists(artists);
        user.setSelectedItemType("artist");
        Output searchOutputArtist = Output.getOutputTemplate(command);
        searchOutputArtist.setResults(artists);
        searchOutputArtist.setMessage("Search returned " + artists.size() + " results");
        return searchOutputArtist;
    }
}
