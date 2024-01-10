package globalwaves.users.listener.notifications;

import globalwaves.users.User;

public interface NotificationsFunctionalities {
    String registerSubscriber(NotificationsObserver o);
    String removeSubscriber(NotificationsObserver o);
    void notifySubscribers(String eventType, User user);
}
