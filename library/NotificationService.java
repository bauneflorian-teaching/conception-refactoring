package library;

public class NotificationService {
    public void sendNotification(Member member, String message) {
        System.out.println("Notification to " + member.name + ": " + message);
    }
}
