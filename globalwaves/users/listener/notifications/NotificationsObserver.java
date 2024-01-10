package globalwaves.users.listener.notifications;

import globalwaves.users.User;
import lombok.Getter;

@Getter
public abstract class NotificationsObserver {
    String username;
    public abstract void update(String eventType, User user);

    /**
     * Equals override to check if a notifications observer is already in
     * the observers list of an artist or host(using the .contains() method)
     * @return true if the usernames are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationsObserver that)) {
            return false;
        }
        return username.equals(that.username);
    }
    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
