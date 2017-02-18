package sample;

import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import java.io.*;
import java.util.Optional;

public class Controller
{
    public Controller()
    {

    }

    @FXML
    private void download(ActionEvent event) throws IOException {
        Button btn = (Button) event.getSource();
        Scene sc = btn.getScene();
        ProgressBar pb = (ProgressBar) sc.lookup("#downloadBar");
        ProgressBar pb2 = (ProgressBar) sc.lookup("#downloadBarB");

        TextField pathInput = ((TextField) sc.lookup("#pathInput"));
        TextField urlInput = ((TextField) sc.lookup("#urlInput"));
        ChoiceBox depthCB = ((ChoiceBox) sc.lookup("#depth"));

        //Splits website given into parts separated by "/"
        String site[] = urlInput.getText().split("/");
        String host = site[2];

        String path = site[site.length - 1];

//                String http = urlInput.getText().split("/")[0];

        Task<Void> task = new Task<Void>() {
            @Override protected Void call() throws IOException, InterruptedException {
                updateProgress(10, 100);

                //Building sites directory structure; Exclude last element in site (filename)
                String directories = "";
                for(int i = 2; i < site.length - 1; i++) {
                    directories += site[i] + "/";
                }

                //NOTE: Pas besoin d'échapper les espaces, mkdirs le gère déjàs
                directories = pathInput.getText() + directories;

                File folders = new File(directories);
                folders.mkdirs();

                updateProgress(50, 100);

                HTTrack downloader = new HTTrack();
                downloader.scanAndDownload(urlInput.getText(), path, host, directories, Integer.valueOf(depthCB.getValue().toString()));
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Succès");
            dialog.setContentText("Téléchargement terminé");
            dialog.getDialogPane().getButtonTypes().add(loginButtonType);
            dialog.getDialogPane().lookupButton(loginButtonType).setDisable(false);

            pb.setVisible(false);
            pb2.setVisible(false);
            btn.setText("Télécharger");

            dialog.showAndWait();
        });
        task.setOnFailed(e -> {
            ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Erreur");
            dialog.setContentText("Téléchargement échoué");
            dialog.getDialogPane().getButtonTypes().add(loginButtonType);
            dialog.getDialogPane().lookupButton(loginButtonType).setDisable(false);

            pb.setVisible(false);
            pb2.setVisible(false);
            btn.setText("Télécharger");

            dialog.showAndWait();
        });
        task.setOnCancelled(e -> {
            //TODO: Effacer le fichier téléchargé et arrêter l'exécution
            pb.setVisible(false);
            pb2.setVisible(false);
            btn.setText("Télécharger");
        });

        if(btn.getText().equals("Télécharger"))
        {
            btn.setText("Annuler");
            pb.progressProperty().bind(task.progressProperty());
            pb2.progressProperty().bind(task.progressProperty());
            pb.setVisible(true);
            pb2.setVisible(true);

            new Thread(task).start();
        }
        else
        {
            task.cancel();
        }
    }
}
