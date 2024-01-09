package output;

import lombok.Getter;
import lombok.Setter;
import globalwaves.users.listener.player.Player;

@Getter
@Setter
public class PlayerStatsForOutput {
    private Integer remainedTime;
    private String name;
    private String repeat;
    private Boolean shuffle;
    private Boolean paused;
    public PlayerStatsForOutput(final Player userPlayer) {
        this.remainedTime = userPlayer.getRemainedTime();
        this.name = userPlayer.getAudioFileName();
        this.repeat = userPlayer.getRepeatStatus();
        this.shuffle = userPlayer.isShuffleStatus();
        this.paused = userPlayer.isPaused();
    }
}
