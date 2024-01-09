package globalwaves.users.listener.player;

import globalwaves.users.listener.Listener;

import static constants.Constants.NO_REPEAT;
import static constants.Constants.REPEAT_INFINITE;
import static constants.Constants.REPEAT_ONCE;

public class SongTimeUpdateStrategy implements PlayerTimeUpdateStrategy {
    @Override
    public final void updateTime(final Listener user, final int elapsedTime) {
        Player userPlayer = user.getUserPlayer();
        switch (userPlayer.getRepeatStatus()) {
            case NO_REPEAT:
                user.setPlayerToEmpty();
                break;
            case REPEAT_ONCE:
                userPlayer.setRemainedTime(userPlayer.getCurrentSong().
                        getDuration() - Math.abs(userPlayer.getRemainedTime()));
                userPlayer.setRepeatStatus(NO_REPEAT);
                break;
            case REPEAT_INFINITE:
                int offset = Math.abs(userPlayer.getRemainedTime())
                        % userPlayer.getCurrentSong().getDuration();
                userPlayer.setRemainedTime(userPlayer.getCurrentSong()
                        .getDuration() - offset);
                break;
            default:
                System.out.println("Unknown repeat status");
                break;
        }
    }
}

