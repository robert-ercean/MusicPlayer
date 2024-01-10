package globalwaves.users.statistics;

import fileio.input.CommandInput;
import fileio.input.EpisodeInput;
import globalwaves.GlobalWaves;
import globalwaves.users.listener.Listener;
import globalwaves.users.listener.player.Player;
import output.Output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HostStats extends UserStatsObserver {
    private final Map<String, Integer> topEpisodes;
    private final ArrayList<Listener> listeners;
    public HostStats(String username) {
        this.topEpisodes = new HashMap<>();
        this.listeners = new ArrayList<>();
        super.username = username;
    }
    @Override
    public Output display(CommandInput command) {
        return null; // TODO
    }
    @Override
    public void update(String eventType, Player userPlayer, int idx) {
        if (eventType.equals("episode")) {
            EpisodeInput episode = (EpisodeInput) userPlayer.getCurrentPodcast().getEpisodes().get(idx);
            topEpisodes.merge(episode.getName(), 1, Integer::sum);
            Listener listener = GlobalWaves.getInstance().getListeners().get(userPlayer.getOwner());
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }
    @Override
    public boolean isEmpty() {
        return topEpisodes.isEmpty();
    }
}
