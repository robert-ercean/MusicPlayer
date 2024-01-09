package globalwaves.admin.statistics;

import fileio.input.CommandInput;
import globalwaves.GlobalWaves;
import globalwaves.admin.AdminCommand;
import globalwaves.audiofiles.Playlist;
import output.Output;

import java.util.ArrayList;
import java.util.List;

import static constants.Constants.MAX;

public final class GetTop5PlaylistsCommand implements AdminCommand {
    @Override
    public Output execute(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (output.getResult() == null) {
            output.setResult(new ArrayList<String>());
        }
        List<Playlist> playlists = GlobalWaves.getInstance().getUserInteractions().getPlaylists();
        playlists.sort((o1, o2) -> {
            if (o1.getFollowers().equals(o2.getFollowers())) {
                return o1.getTimeStampCreated() - o2.getTimeStampCreated();
            }
            return o2.getFollowers() - o1.getFollowers();
        });
        int cnt = 0;
        for (Playlist playlist : playlists) {
            if (cnt == MAX) {
                break;
            }
            ((List<String>) output.getResult()).add(playlist.getName());
            cnt++;
        }
        return output;
    }
}
