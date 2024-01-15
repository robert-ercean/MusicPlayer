package globalwaves.users.statistics;

import fileio.input.CommandInput;
import fileio.input.EpisodeInput;
import globalwaves.GlobalWaves;
import globalwaves.users.listener.Listener;
import globalwaves.users.listener.player.Player;
import output.Output;
import output.WrappedStatsForOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HostStats extends UserStatsObserver {
    private final Map<String, Integer> topEpisodes;
    private final ArrayList<Listener> listeners;
    public HostStats(final String username) {
        this.topEpisodes = new HashMap<>();
        this.listeners = new ArrayList<>();
        super.username = username;
    }

    /**
     * displays the host stats
     */
    @Override
    public Output display(final CommandInput command) {
        Output output = Output.getOutputTemplate(command);
        if (this.isEmpty()) {
            output.setMessage("No data to show for user " + super.username + ".");
            return output;
        }
        WrappedStatsForOutput result = new WrappedStatsForOutput();
        result.setTopEpisodes(getSortedMap(topEpisodes));
        result.setListeners(listeners.size());
        output.setResult(result);
        return output;
    }
    /**
     * parses the music events and updates the host's stats
     */
    @Override
    public void update(final String eventType, final Player userPlayer, final int idx) {
        if (eventType.equals("episode")) {
            EpisodeInput episode = userPlayer.getCurrentPodcast().getEpisodes().get(idx);
            topEpisodes.merge(episode.getName(), 1, Integer::sum);
            Listener listener = GlobalWaves.getInstance().getListeners().get(userPlayer.getOwner());
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }
    /**
     * @return true if the host has no stats to display
     */
    @Override
    public boolean isEmpty() {
        return topEpisodes.isEmpty();
    }
}
