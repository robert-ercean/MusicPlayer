package globalwaves.users.listener.search;

import fileio.input.CommandInput;
import output.Output;
public interface SearchStrategy {
    /**
     * Method that searches for a media file / artist / host in the library
     * Method is inherited by all the search strategies
     */
    Output search(CommandInput command);
    /**
     * Method that sets the search strategy
     * @param context the search context instance, from which we will execute the search command
     * @param type the type of the search strategy to be set inside the context
     */
    static void getSearchStrategy(SearchContext context, String type) {
        switch (type) {
            case "song" -> context.setStrategy(new SongSearchStrategy());
            case "album" -> context.setStrategy(new AlbumSearchStrategy());
            case "artist" -> context.setStrategy(new ArtistSearchStrategy());
            case "playlist" -> context.setStrategy(new PlaylistSearchStrategy());
            case "podcast" -> context.setStrategy(new PodcastSearchStrategy());
            case "host" -> context.setStrategy(new HostSearchStrategy());
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}
