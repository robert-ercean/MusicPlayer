package globalwaves.users.listener.player;

import globalwaves.users.listener.Listener;

public interface PlayerTimeUpdateStrategy {
    /**
     * Update the player time for the given listener
     * @param user - user to update the player time for
     * @param elapsedTime - time elapsed since the last user's command
     */
    void updateTime(Listener user, int elapsedTime);
    /**
     * Returns the time update strategy for the loaded audio file
     * @param selectedItemType - loaded audio file type
     */
    static PlayerTimeUpdateStrategy getTimeUpdateStrategy(final String selectedItemType) {
        return switch (selectedItemType) {
            case "song" -> new SongTimeUpdateStrategy();
            case "podcast" -> new PodcastTimeUpdateStrategy();
            // we view an album as a playlist when interacting with the player
            case "album", "playlist" -> new PlaylistTimeUpdateStrategy();
            default -> throw new IllegalStateException("Unexpected value: " + selectedItemType);
        };
    }
}
