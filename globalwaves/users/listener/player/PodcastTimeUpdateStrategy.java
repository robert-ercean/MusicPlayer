package globalwaves.users.listener.player;

import globalwaves.users.listener.Listener;

import static constants.Constants.NO_REPEAT;
import static constants.Constants.REPEAT_INFINITE;
import static constants.Constants.REPEAT_ONCE;

public class PodcastTimeUpdateStrategy implements PlayerTimeUpdateStrategy {
    @Override
    public final void updateTime(final Listener user, final int elapsedTime) {
        Player userPlayer = user.getUserPlayer();
        int episodeIdx = userPlayer.getCurrentPodcast().getIdx();
        int podcastSize = userPlayer.getCurrentPodcast().
                getEpisodes().size();
        int pastTime = Math.abs(userPlayer.getRemainedTime());
        int remainingTime = userPlayer.getCurrentPodcast().
                getTotalRemainingTimeFromIdx(episodeIdx + 1);
        int newIdx;
        switch (userPlayer.getRepeatStatus()) {
            case NO_REPEAT:
                if (episodeIdx + 1 >= podcastSize || pastTime > remainingTime) {
                    user.setPlayerToEmpty();
                    user.getUserPlayer().getCurrentPodcast().setIdx(0);
                    user.notifyStatsObservers("episode", userPlayer, episodeIdx);
                    break;
                }
                newIdx = userPlayer.getCurrentPodcast().
                        getEpisodeIdxByElapsedTime(pastTime, episodeIdx + 1);
                userPlayer.setAudioFileName(userPlayer.
                        getCurrentPodcast().getEpisodes().get(
                                newIdx).getName());
                userPlayer.setRemainedTime(userPlayer.getCurrentPodcast().
                        getEpisodes().get(newIdx).getDuration()
                        - userPlayer.getCurrentPodcast().
                        getEpisodeTimeStampByElapsedTime(
                                pastTime, episodeIdx + 1));
                userPlayer.getCurrentPodcast().setIdx(newIdx);
                // iterate over all the played episodes and notify the observers of each one
                for (int i = episodeIdx; i < newIdx; i++) {
                    user.notifyStatsObservers("episode", userPlayer, i);
                }
                break;
            case REPEAT_ONCE:
                if (pastTime > userPlayer.getCurrentPodcast().
                        getEpisodes().get(episodeIdx).getDuration()) {
                    int newTime = pastTime - userPlayer.
                            getCurrentPodcast().getEpisodes().
                            get(episodeIdx).getDuration();
                    newIdx = userPlayer.getCurrentPodcast().
                            getEpisodeIdxByElapsedTime(
                                    newTime, episodeIdx + 1);
                    if (newIdx == -1) {
                        user.setPlayerToEmpty();
                        user.getUserPlayer().getCurrentPodcast().setIdx(0);
                        break;
                    }
                    userPlayer.setAudioFileName(userPlayer.
                            getCurrentPodcast().getEpisodes().
                            get(newIdx).getName());
                    userPlayer.setRemainedTime(userPlayer.
                            getCurrentPodcast().getEpisodes().get(newIdx).getDuration()
                            - userPlayer.getCurrentPodcast().
                            getEpisodeTimeStampByElapsedTime(
                                    newTime, episodeIdx + 1));
                } else {
                    userPlayer.setRemainedTime(userPlayer.
                            getCurrentPodcast().getEpisodes().
                            get(episodeIdx).getDuration() - pastTime);
                    userPlayer.setRepeatStatus(NO_REPEAT);
                }
                break;
            case REPEAT_INFINITE:
                int newTimeStamp = pastTime % userPlayer.
                        getCurrentPodcast().getEpisodes().
                        get(episodeIdx).getDuration();
                userPlayer.setRemainedTime(userPlayer.
                        getCurrentPodcast().getEpisodes()
                        .get(episodeIdx).getDuration() - newTimeStamp);
                break;
            default:
                System.out.println("Unknown repeat status");
                break;
        }
    }
}
