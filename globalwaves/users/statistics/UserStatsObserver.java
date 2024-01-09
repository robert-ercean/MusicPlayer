package globalwaves.users.statistics;

import fileio.input.CommandInput;
import globalwaves.users.listener.player.Player;
import lombok.Getter;
import output.Output;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public abstract class UserStatsObserver {
    protected String username;
    public abstract Output display(CommandInput command);
    public abstract void update(String eventType, Player userPlayer, int idx);
    public abstract boolean isEmpty();
    /**
     * Equals and hashCode methods override by checking only the username field
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserStatsObserver that)) return false;
        return getUsername().equals(that.getUsername());
    }
    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }
    /**
     * Sorts the given map by value and then by key (lexicographically)
     * Used in both ArtistStats and ListenerStats
     * @param map the map to be sorted
     * @return the sorted map
     */
    protected static Map<String, Integer> getSortedMap(Map<String, Integer> map) {
        return map.entrySet().stream().sorted((o1, o2) -> {
            if (o1.getValue().equals(o2.getValue())) {
                return o1.getKey().compareTo(o2.getKey());
            }
            return o2.getValue() - o1.getValue();
        }).limit(5).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    /**
     * Sorts the given map by value and then by key (lexicographically)
     * Used for sorting the listeners field inside ArtistStats
     * @param map the map to be sorted
     * @return the sorted list
     */
    protected static List<String> getSortedList(Map<String, Integer> map) {
        return map.entrySet().stream().sorted((o1, o2) -> {
            if (o1.getValue().equals(o2.getValue())) {
                return o1.getKey().compareTo(o2.getKey());
            }
            return o2.getValue() - o1.getValue();
        }).limit(5).map(Map.Entry::getKey).collect(Collectors.toList());
    }
}
