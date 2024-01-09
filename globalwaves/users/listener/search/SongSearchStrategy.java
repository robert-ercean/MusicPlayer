package globalwaves.users.listener.search;

import fileio.input.Filter;
import globalwaves.GlobalWaves;
import globalwaves.audiofiles.Song;
import fileio.input.CommandInput;
import fileio.input.SongInput;
import globalwaves.users.listener.Listener;
import output.Output;

import java.util.ArrayList;
import java.util.List;

import static constants.Constants.MAX;

public final class SongSearchStrategy implements SearchStrategy {
    @Override
    public Output search(final CommandInput command) {
        Listener user = GlobalWaves.getInstance().getUsers().get(command.getUsername());
        List<SongInput> allSongs = GlobalWaves.getInstance().getLibrary().getSongs();
        List<Song> matchingSongs = new ArrayList<>();
        for (SongInput song : allSongs) {
            if (matchesFiltersForSongs(song, command.getFilters())) {
                matchingSongs.add(new Song(song));
            }
            if (matchingSongs.size() == MAX) {
                break;
            }
        }
        List<String> matchingSongsNames = new ArrayList<>();
        for (Song song : matchingSongs) {
            matchingSongsNames.add(song.getName());
        }
        Output searchOutputSong = new Output(
                command.getCommand(),
                command.getUsername(),
                command.getTimestamp(),
                "Search returned " + matchingSongs.size() + " results",
                matchingSongsNames
        );
        user.setSearchedSongs(matchingSongs);
        user.setSelectedItemType("song");
        return searchOutputSong;
    }
    /**
     * Checks if a song matches the filters which are parsed from the command parameters
     */
    public static boolean matchesFiltersForSongs(
            final SongInput song, final Filter filters) {
        if (filters.getArtist() != null && !song.getArtist().equals(filters.getArtist())) {
            return false;
        }
        if (filters.getTags() != null && !song.getTags().containsAll(filters.getTags())) {
            return false;
        }
        if (filters.getAlbum() != null && !song.getAlbum().equals(filters.getAlbum())) {
            return false;
        }
        if (filters.getGenre() != null && !song.getGenre().equalsIgnoreCase(
                filters.getGenre())) {
            return false;
        }
        if (filters.getLyrics() != null && !song.getLyrics().toLowerCase().contains(
                filters.getLyrics().toLowerCase())) {
            return false;
        }
        // the names are not case-sensitive
        if (filters.getName() != null && !song.getName().toLowerCase().startsWith(
                filters.getName().toLowerCase())) {
            return false;
        }
        if (filters.getReleaseYear() != null) {
            char option = filters.getReleaseYear().charAt(0);
            switch (option) {
                case '<':
                    if (song.getReleaseYear() > Integer.parseInt(filters.
                            getReleaseYear().substring(1))) {
                        return false;
                    }
                    break;
                case '>':
                    if (song.getReleaseYear() < Integer.parseInt(filters.getReleaseYear()
                            .substring(1))) {
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }
}
