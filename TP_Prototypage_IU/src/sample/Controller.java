package sample;

import com.sun.javaws.progress.Progress;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class Controller
{
    @FXML
    private void launchProgressBar(ActionEvent event)
    {
        Button btn = (Button) event.getSource();
        Scene sc = btn.getScene();
        btn.setText("Annuler");
        ProgressBar pb = (ProgressBar) sc.lookup("#downloadBar");
        pb.setVisible(true);
        pb.setProgress(pb.getProgress() + 0.1);
    }
}
