package globalwaves.users.statistics;

import globalwaves.users.listener.player.Player;

public interface UserStatistics {
    /**
     * @param o the observer to be registered
     */
    void registerStatsObserver(UserStatsObserver o);

    /**
     * currently not used
     * @param o the observer to be deleted
     */
    void removeStatsObserver(UserStatsObserver o);
    /**
     * @param eventType the type of the event
     * @param userPlayer the player that triggered the event
     * @param idx the index of the event
     */
    void notifyStatsObservers(String eventType, Player userPlayer, int idx);
}
