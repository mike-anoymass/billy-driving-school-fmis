/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billydrivingschoolfmis;

import javafx.embed.swing.JFXPanel;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author ANOYMASS
 */
public class StageManager {
    static Stage historyStage = new Stage();
    static Stage ParentStage = new Stage();
    static Stage announcementStage = new Stage();
    static Stage receiptStage = new Stage();
    static Stage browsingStage = new Stage();

    static void setStages(){
        announcementStage.initModality(Modality.WINDOW_MODAL);
        announcementStage.setResizable(false);
        announcementStage.initOwner(ParentStage);

        receiptStage.initModality(Modality.WINDOW_MODAL);
        receiptStage.setResizable(false);
        receiptStage.initOwner(ParentStage);

        historyStage.initModality(Modality.WINDOW_MODAL);
        historyStage.setResizable(false);
        historyStage.initOwner(ParentStage);

        browsingStage.initModality(Modality.WINDOW_MODAL);
        browsingStage.setResizable(false);
        browsingStage.initOwner(ParentStage);
    }
    
    
    
}
