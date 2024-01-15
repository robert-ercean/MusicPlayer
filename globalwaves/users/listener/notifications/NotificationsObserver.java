package globalwaves.users.listener.notifications;

import globalwaves.users.User;
import lombok.Getter;

@Getter
public abstract class NotificationsObserver {
    String username;
    /**
     * @param eventType the type of event that happened
     * @param user the user that triggered the event
     */
    public abstract void update(String eventType, User user);

    /**
     * Equals override to check if a notifications observer is already in
     * the observers list of an artist or host(using the .contains() method)
     * @return true if the usernames are equal, false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationsObserver that)) {
            return false;
        }
        return username.equals(that.username);
    }
    /**
     * Hashcode override to check if a notifications observer is already in
     * the observers list of an artist or host(using the .contains() method)
     * @return the hashcode of the username
     */
    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
