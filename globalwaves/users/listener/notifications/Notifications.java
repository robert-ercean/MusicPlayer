package globalwaves.users.listener.notifications;

public interface Notifications {
    String registerSubscriber(NotificationsObserver o);
    String removeSubscriber(NotificationsObserver o);
    void notifySubscribers(String eventType);
}
