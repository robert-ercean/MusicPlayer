package globalwaves.audiofiles;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PodcastState {
    private int lastEpisodeIdx;
    private int remainingTime;
    public PodcastState(final int lastEpisodeIdx, final int timeIdx) {
        this.lastEpisodeIdx = lastEpisodeIdx;
        this.remainingTime = timeIdx;
    }
}
