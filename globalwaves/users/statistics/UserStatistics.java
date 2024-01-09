package globalwaves.users.statistics;

import globalwaves.users.listener.player.Player;

public interface UserStatistics {
    void registerObserver(UserStatsObserver o);
    void removeObserver(UserStatsObserver o);
    void notifyObservers(String eventType, Player userPlayer, int idx);
}
