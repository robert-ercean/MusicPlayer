package globalwaves.admin.statistics;

import fileio.input.CommandInput;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import globalwaves.GlobalWaves;
import globalwaves.admin.AdminCommand;
import globalwaves.audiofiles.LikedAudioFiles;
import globalwaves.audiofiles.Song;
import output.Output;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static constants.Constants.MAX;

public final class GetTop5SongsCommand implements AdminCommand {
    @Override
    public Output execute(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (output.getResult() == null) {
            output.setResult(new ArrayList<String>());
        }
        List<SongInput> sortedSongs = getSortedSongs(GlobalWaves.getInstance().
                getUserInteractions(), GlobalWaves.getInstance().getLibrary());
        // Get top 5 songs or all songs if there are fewer than 5
        int count = 0;
        for (SongInput song : sortedSongs) {
            if (count == MAX) {
                break;
            }
            ((List<String>) output.getResult()).add(song.getName());
            count++;
        }
        return output;
    }
    /**
     * Sorts the songs in the library by the number of likes they have received.
     * @return the sorted songs
     */
    private static List<SongInput> getSortedSongs(final LikedAudioFiles userInteractions,
                                                  final LibraryInput library) {
        Map<Song, Integer> likeCountMap = userInteractions.getSongLikeCountMap();

        List<SongInput> sortedSongs = new ArrayList<>(library.getSongs());
        sortedSongs.sort((s1, s2) -> {
            Song song1 = new Song(s1);
            Song song2 = new Song(s2);
            int likeCount1 = likeCountMap.getOrDefault(song1, 0);
            int likeCount2 = likeCountMap.getOrDefault(song2, 0);
            // If the songs have the same number of likes, sort them by their index in the library
            // (the order in which they were added)
            if (likeCount1 == likeCount2) {
                return library.getSongs().indexOf(s1) - library.getSongs().indexOf(s2);
            }
            return likeCount2 - likeCount1; // descending order of likes
        });
        return sortedSongs;
    }
}

