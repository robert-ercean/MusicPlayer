package fileio.input;

import globalwaves.audiofiles.Podcast;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
@Setter
public final class PodcastInput {
    private String name;
    private String owner;
    private ArrayList<EpisodeInput> episodes;

    public PodcastInput() {
    }
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PodcastInput podcast)) {
            return false;
        }
        return podcast.getName().equals(this.getName());
    }
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    public PodcastInput(final Podcast podcast) {
        this.name = podcast.getName();
        this.owner = podcast.getOwner();
        this.episodes = podcast.getEpisodes();
    }
}
