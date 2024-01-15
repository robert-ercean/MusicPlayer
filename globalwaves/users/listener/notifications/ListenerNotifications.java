package globalwaves.users.listener.notifications;

import globalwaves.users.User;
import globalwaves.users.artist.Artist;
import globalwaves.users.host.Host;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer design pattern implementation for notifications
 * In this class the notifications are managed allowing
 * for further additions to the whole notification system
 */
@Getter
public class ListenerNotifications extends NotificationsObserver {
    private final List<Notification> notifications;

    public ListenerNotifications(final String username) {
        this.notifications = new ArrayList<>();
        super.username = username;
    }
    /**
     * @param eventType the type of event that happened
     * @param user the user that triggered the event
     */
    @Override
    public void update(final String eventType, final User user) {
        switch (eventType) {
            case "newEvent" -> processNewEvent((Artist) user);
            case "newMerch" -> processNewMerch((Artist) user);
            case "newAlbum" -> processNewAlbum((Artist) user);
            case "newPodcast" -> processNewPodcast((Host) user);
            case "newAnnouncement" -> processNewAnnouncement((Host) user);
            default -> throw new IllegalStateException("Unexpected value: " + eventType);
        }
    }
    private void processNewAlbum(final Artist artist) {
        Notification notification = new Notification();
        notification.setName("New Album");
        notification.setDescription("New Album from " + artist.getUsername() + ".");
        this.notifications.add(notification);
    }
    private void processNewPodcast(final Host host) {
        Notification notification = new Notification();
        notification.setName("New Podcast");
        notification.setDescription("New Podcast from " + host.getUsername() + ".");
        this.notifications.add(notification);
    }
    private void processNewEvent(final Artist artist) {
        Notification notification = new Notification();
        notification.setName("New Event");
        notification.setDescription("New Event from " + artist.getUsername() + ".");
        this.notifications.add(notification);
    }
    private void processNewMerch(final Artist artist) {
        Notification notification = new Notification();
        notification.setName("New Merchandise");
        notification.setDescription("New Merchandise from " + artist.getUsername() + ".");
        this.notifications.add(notification);
    }
    private void processNewAnnouncement(final Host host) {
        Notification notification = new Notification();
        notification.setName("New Announcement");
        notification.setDescription("New Announcement from " + host.getUsername() + ".");
        this.notifications.add(notification);
    }
}
