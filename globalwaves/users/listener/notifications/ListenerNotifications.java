package globalwaves.users.listener.notifications;

import globalwaves.users.User;
import globalwaves.users.artist.Artist;
import globalwaves.users.host.Host;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
public class ListenerNotifications extends NotificationsObserver {
    private final List<Notification> notifications;

    public ListenerNotifications(String username) {
        this.notifications = new ArrayList<>();
        super.username = username;
    }
    @Override
    public void update(String eventType, User user) {
        switch (eventType) {
            case "newEvent" -> processNewEvent((Artist) user);
            case "newMerch" -> processNewMerch((Artist) user);
            case "newAlbum" -> processNewAlbum((Artist) user);
            case "newPodcast" -> processNewPodcast((Host) user);
            case "newAnnouncement" -> processNewAnnouncement((Host) user);
        }
    }
    private void processNewAlbum(Artist artist) {
        Notification notification = new Notification();
        notification.setName("New Album");
        notification.setDescription("New Album from " + artist.getUsername() + ".");
        this.notifications.add(notification);
    }
    private void processNewPodcast(Host host) {
        Notification notification = new Notification();
        notification.setName("New Podcast");
        notification.setDescription("New Podcast from " + host.getUsername() + ".");
        this.notifications.add(notification);
    }
    private void processNewEvent(Artist artist) {
        Notification notification = new Notification();
        notification.setName("New Event");
        notification.setDescription("New Event from " + artist.getUsername() + ".");
        this.notifications.add(notification);
    }
    private void processNewMerch(Artist artist) {
        Notification notification = new Notification();
        notification.setName("New Merchandise");
        notification.setDescription("New Merchandise from " + artist.getUsername() + ".");
        this.notifications.add(notification);
    }
    private void processNewAnnouncement(Host host) {
        Notification notification = new Notification();
        notification.setName("New Announcement");
        notification.setDescription("New Announcement from " + host.getUsername() + ".");
        this.notifications.add(notification);
    }
}
