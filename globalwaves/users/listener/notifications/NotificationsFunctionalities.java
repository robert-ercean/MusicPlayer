package globalwaves.users.listener.notifications;

import globalwaves.users.User;

public interface NotificationsFunctionalities {
    /**
     * @param o notifications observer to be registered
     * @return string indicating whether the user subscribed or unsubscribed
     */
    String registerSubscriber(NotificationsObserver o);
    /**
     * @param o notifications observer to be removed
     * @return string that will be attached in the output message
     */

    String removeSubscriber(NotificationsObserver o);
    /**
     * @param eventType the type of the event
     * @param user the user that will be notified
     */
    void notifySubscribers(String eventType, User user);
}
