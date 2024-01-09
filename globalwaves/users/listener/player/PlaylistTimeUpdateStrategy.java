package globalwaves.users.listener.player;

import globalwaves.users.listener.Listener;

import static constants.Constants.NO_REPEAT;
import static constants.Constants.REPEAT_ALL;
import static constants.Constants.REPEAT_CURRENT_SONG;
public class PlaylistTimeUpdateStrategy implements PlayerTimeUpdateStrategy {
    @Override
    public final void updateTime(final Listener user, final int elapsedTime) {
        Player userPlayer = user.getUserPlayer();
        int songIdx = userPlayer.getCurrentPlaylist().getIdx();
        int playlistSize = userPlayer.getCurrentPlaylist().getSongs().size();

        int pastTime = Math.abs(userPlayer.getRemainedTime());
        int remainingTime = userPlayer.getCurrentPlaylist().
                getTotalRemainingDurationFromIdx(songIdx + 1);
        int newIdx;
        int oldIdx = songIdx;
        switch (userPlayer.getRepeatStatus()) {
            case NO_REPEAT:
                if (songIdx + 1 >= playlistSize || pastTime > remainingTime) {
                    int startIdx = (songIdx == 0) ? 0 : oldIdx + 1;
                    for (int i = startIdx; i < playlistSize; i++) {
                        user.notifyObservers("playlist", userPlayer, i);
                    }
                    user.setPlayerToEmpty();
                    user.getUserPlayer().getCurrentPlaylist().setIdx(0);
                    break;
                }
                newIdx = userPlayer.getCurrentPlaylist().getSongIdxByElapsedTime(pastTime, songIdx + 1);
                if (newIdx == -1) { // this means that we finished the playlist
                    for (int i = (oldIdx == 0) ? 0 : oldIdx + 1; i < playlistSize; i++) {
                        user.notifyObservers("playlist", userPlayer, i);
                    }
                    user.setPlayerToEmpty();
                    break;
                }
                userPlayer.setCurrentSong(userPlayer.getCurrentPlaylist().getSongsList().get(newIdx));
                userPlayer.setAudioFileName(userPlayer.getCurrentSong().getName());
                userPlayer.setRemainedTime(userPlayer.getCurrentSong().getDuration()
                        - userPlayer.getCurrentPlaylist().
                        getSongTimeStampByElapsedTime(pastTime, songIdx + 1));
                userPlayer.getCurrentPlaylist().setIdx(newIdx);
                // iterate over all the played songs and notify the observers of each one
                for (int i = (oldIdx == 0) ? 0 : oldIdx + 1; i <= newIdx; i++) {
                    user.notifyObservers("playlist", userPlayer, i);
                }
                break;
            case REPEAT_ALL:
                if (pastTime > remainingTime) {
                    int newTime = pastTime - remainingTime;
                    newIdx = userPlayer.getCurrentPlaylist().
                            getSongIdxByElapsedTime(newTime, 0);
                    if (newIdx == -1) { // this means that we finished the playlist
                        newTime = newTime % userPlayer.getCurrentPlaylist().
                                getTotalRemainingDurationFromIdx(0);
                        newIdx = userPlayer.getCurrentPlaylist().
                                getSongIdxByElapsedTime(newTime, 0);
                    }
                    userPlayer.setCurrentSong(userPlayer.
                            getCurrentPlaylist().getSongsList().get(newIdx));
                    userPlayer.setAudioFileName(userPlayer.getCurrentSong().getName());
                    userPlayer.setRemainedTime(userPlayer.getCurrentSong().getDuration()
                            - userPlayer.getCurrentPlaylist().
                            getSongTimeStampByElapsedTime(newTime, 0));
                } else {
                    newIdx = userPlayer.getCurrentPlaylist().
                            getSongIdxByElapsedTime(pastTime, songIdx + 1);
                    userPlayer.setCurrentSong(userPlayer.getCurrentPlaylist().getSongsList().get(newIdx));
                    userPlayer.setAudioFileName(userPlayer.getCurrentSong().getName());
                    userPlayer.setRemainedTime(userPlayer.getCurrentSong().getDuration()
                    - userPlayer.getCurrentPlaylist().getSongTimeStampByElapsedTime(
                    pastTime, songIdx + 1));
                }
                userPlayer.getCurrentPlaylist().setIdx(newIdx);
                break;
            case REPEAT_CURRENT_SONG:
                int currSongTime = userPlayer.getCurrentSong().getDuration();
                if (pastTime > currSongTime) {
                    int newTimeStamp = pastTime % currSongTime;
                    userPlayer.setRemainedTime(currSongTime - newTimeStamp);
                } else {
                    userPlayer.setRemainedTime(currSongTime - pastTime);
                }
                break;
            default:
                System.out.println("Unknown repeat status");
                break;
        }
    }
}
