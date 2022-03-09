package billydrivingschoolfmis;

import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

public class NotificationClass {
    public static void showNotification(String txt) {
        TrayNotification tray = new TrayNotification("Billy FMIS",
                txt,
                NotificationType.SUCCESS);

        tray.setAnimationType(AnimationType.SLIDE);
        tray.showAndDismiss(Duration.seconds(3));
    }

}
