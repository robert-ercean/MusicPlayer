package globalwaves.admin.statistics;

import fileio.input.CommandInput;
import globalwaves.GlobalWaves;
import globalwaves.admin.AdminCommand;
import globalwaves.users.artist.Artist;
import output.Output;

import java.util.ArrayList;
import java.util.List;

import static constants.Constants.MAX;

public final class GetTop5Artists implements AdminCommand {
    @Override
    public Output execute(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        List<Artist> artists = new ArrayList<>(GlobalWaves.getInstance().getArtists().values());
        artists.sort((artist1, artist2) -> {
            int likes1 = artist1.getTotalLikeCount();
            int likes2 = artist2.getTotalLikeCount();
            return likes2 - likes1;
        });
        List<String> top5Artists = new ArrayList<>();
        for (Artist artist : artists) {
            top5Artists.add(artist.getUsername());
        }
        // limit to 5
        top5Artists = top5Artists.subList(0, Math.min(MAX, top5Artists.size()));
        output.setResult(top5Artists);
        return output;
    }
}
