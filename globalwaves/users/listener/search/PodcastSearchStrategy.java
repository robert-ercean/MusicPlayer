package globalwaves.users.listener.search;

import fileio.input.Filter;
import globalwaves.GlobalWaves;
import globalwaves.audiofiles.Podcast;
import fileio.input.CommandInput;
import fileio.input.PodcastInput;
import globalwaves.users.listener.Listener;
import output.Output;

import java.util.ArrayList;
import java.util.List;

import static constants.Constants.MAX;
public final class PodcastSearchStrategy implements SearchStrategy {
    @Override
    public Output search(final CommandInput command) {
        Listener user = GlobalWaves.getInstance().getListeners().get(command.getUsername());
        List<PodcastInput> allPodcasts = GlobalWaves.getInstance().getLibrary().getPodcasts();
        List<Podcast> matchingPodcasts = new ArrayList<>();
        for (PodcastInput podcast : allPodcasts) {
            if (matchesFiltersForPodcasts(podcast, command.getFilters())) {
                matchingPodcasts.add(new Podcast(podcast));
            }
            if (matchingPodcasts.size() == MAX) {
                break;
            }
        }
        List<String> matchingPodcastsNames = new ArrayList<>();
        for (Podcast podcast : matchingPodcasts) {
            matchingPodcastsNames.add(podcast.getName());
        }
        Output searchOutputPodcast = new Output(
                command.getCommand(),
                command.getUsername(),
                command.getTimestamp(),
                "Search returned " + matchingPodcasts.size() + " results",
                matchingPodcastsNames
        );
        user.setSelectedItemType("podcast");
        user.setSearchedPodcasts(matchingPodcasts);
        return searchOutputPodcast;
    }
    /**
     * Checks to see if a podcast matches the filters.
     */
    public static boolean matchesFiltersForPodcasts(
            final PodcastInput podcast, final Filter filters) {
        if (filters.getName() != null && !podcast.getName().startsWith(
                filters.getName())) {
            return false;
        }
        return filters.getOwner() == null || podcast.getOwner().equals(
                filters.getOwner());
    }
}
