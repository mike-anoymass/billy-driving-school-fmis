package billydrivingschoolfmis;

import javafx.scene.control.Alert;

public class AlertClass {
    public static void makeAlert(String title, String txt) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        switch (title) {
            case "error":
                alert = new Alert(Alert.AlertType.ERROR);
                break;

            case "information":
                alert = new Alert(Alert.AlertType.INFORMATION);
                break;
        }

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(txt);
        alert.showAndWait();

    }
}
