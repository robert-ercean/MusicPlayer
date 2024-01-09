package globalwaves.audiofiles;

import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import globalwaves.GlobalWaves;
import globalwaves.users.listener.Listener;
import globalwaves.users.listener.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public final class Podcast {
    private String name;
    private String owner;
    private ArrayList<EpisodeInput> episodes;
    private Integer idx = 0;

    public Podcast(final PodcastInput podcastInput) {
        this.name = podcastInput.getName();
        this.owner = podcastInput.getOwner();
        this.episodes = podcastInput.getEpisodes();
    }
    public Podcast(final String name, final String owner, final ArrayList<EpisodeInput> episodes) {
        this.owner = owner;
        this.name = name;
        this.episodes = episodes;
    }

    /**
     * Checks if the podcast is loaded in any listener's player
     */
    public boolean isLoaded() {
        for (Listener user : GlobalWaves.getInstance().getUsers().values()) {
            Player player = user.getUserPlayer();
            if (player != null && player.getCurrentPodcast() != null
                    && player.getCurrentPodcast().getName().equals(this.name)) {
                return true;
            }
        }
        return false;
    }
    /**
     * @param episodeName - episode name to be checked
     * @return the index of the episode in the playlist
     */
    public int getEpisodeIdxByName(final String episodeName) {
        for (int i = 0; i < this.episodes.size(); i++) {
            if (this.episodes.get(i).getName().equals(episodeName)) {
                return i;
            }
        }
        return -1;
    }
    /**
     * @param epIdx - episode index
     * @return time until playlist ends from given episode index
     */
    public int getTotalRemainingTimeFromIdx(final int epIdx) {
        int totalTime = 0;
        for (int i = epIdx; i < this.episodes.size(); i++) {
            totalTime += this.episodes.get(i).getDuration();
        }
        return totalTime;
    }
    /**
     * @param elapsedTime - elapsed time after updating the player timestamps
     * @param epIdx - episode index
     * @return current playing episode index in the playlist after a period of time
     */
    public int getEpisodeIdxByElapsedTime(final int elapsedTime, final int epIdx) {
        int totalTime = 0;
        for (int i = epIdx; i < this.episodes.size(); i++) {
            totalTime += this.episodes.get(i).getDuration();
            if (totalTime > elapsedTime) {
                return i;
            }
        }
        return -1; // finished the podcast
    }
    /**
     * @param elapsedTime - elapsed time after updating the player timestamps
     * @param epIdx - episode index
     * @return current timestamp in the current playing episode after a period of time
     */
    public int getEpisodeTimeStampByElapsedTime(final int elapsedTime, final int epIdx) {
        int totalTime = 0;
        for (int i = epIdx; i < this.episodes.size(); i++) {
            totalTime += this.episodes.get(i).getDuration();
            if (totalTime > elapsedTime) {
                return elapsedTime - (totalTime - this.episodes.get(i).getDuration());
            }
        }
        return -1; // shouldn't get here
    }
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Podcast podcast) {
            return this.name.equals(podcast.name);
        }
        return false;
    }
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
