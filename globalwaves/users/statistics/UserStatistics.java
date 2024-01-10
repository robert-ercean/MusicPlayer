package globalwaves.users.statistics;

import globalwaves.users.listener.player.Player;

public interface UserStatistics {
    void registerStatsObserver(UserStatsObserver o);
    void removeStatsObserver(UserStatsObserver o);
    void notifyStatsObservers(String eventType, Player userPlayer, int idx);
}
