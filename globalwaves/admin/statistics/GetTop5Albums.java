package globalwaves.admin.statistics;

import fileio.input.CommandInput;
import globalwaves.GlobalWaves;
import globalwaves.admin.AdminCommand;
import globalwaves.audiofiles.Album;
import output.Output;

import java.util.ArrayList;
import java.util.List;

import static constants.Constants.MAX;

public final class GetTop5Albums implements AdminCommand {
    @Override
    public Output execute(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        List<Album> albums = getSortedAlbums();
        List<String> top5Albums = new ArrayList<>();
        for (Album album : albums) {
            top5Albums.add(album.getName());
        }
        // limit to 5
        top5Albums = top5Albums.subList(0, Math.min(MAX, top5Albums.size()));
        output.setResult(top5Albums);
        return output;
    }
    private List<Album> getSortedAlbums() {
        List<Album> albums = new ArrayList<>(GlobalWaves.getInstance().getAllAlbums());
        // sort the albums by their number of likes, if they have the same number of likes
        // we ll sort them alphabetically
        albums.sort((album1, album2) -> {
            int likes1 = album1.getTotalLikeCount();
            int likes2 = album2.getTotalLikeCount();
            if (likes1 == likes2) {
                return album1.getName().compareTo(album2.getName());
            }
            return likes2 - likes1;
        });
        return albums;
    }
}
