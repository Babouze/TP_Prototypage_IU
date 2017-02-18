package sample;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import java.io.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

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

        if(pathInput.getText().equals(""))
        {
            ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Erreur");
            dialog.setContentText("Veuillez choisir un endroit où enregistrer le fichier");
            dialog.getDialogPane().getButtonTypes().add(loginButtonType);
            dialog.getDialogPane().lookupButton(loginButtonType).setDisable(false);
            dialog.showAndWait();
            return;
        }
        else if(urlInput.getText().equals(""))
        {
            ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Erreur");
            dialog.setContentText("Veuillez saisir une adresse");
            dialog.getDialogPane().getButtonTypes().add(loginButtonType);
            dialog.getDialogPane().lookupButton(loginButtonType).setDisable(false);
            dialog.showAndWait();
            return;
        }

        //Splits website given into parts separated by "/"
        String site[] = urlInput.getText().split("/");
        if(site.length < 2)
        {
            ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Erreur");
            dialog.setContentText("L'adresse que vous avez entrée semble être incorrecte. Elle doit être au format suivant : http://example.com/");
            dialog.getDialogPane().getButtonTypes().add(loginButtonType);
            dialog.getDialogPane().lookupButton(loginButtonType).setDisable(false);
            dialog.showAndWait();
            return;
        }

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

                HTTrack downloader = new HTTrack();
                updateProgress(50, 100);
                downloader.scanAndDownload(urlInput.getText(), path, host, directories, Integer.valueOf(depthCB.getValue().toString()));
                updateProgress(90, 100);

                return null;
            }
        };
        task.setOnSucceeded(e -> {
            ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Succès");
            dialog.setContentText("Téléchargement terminé. L'arborescence a été ajoutée à votre bibliothèque");
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
            dialog.setContentText("Téléchargement échoué, vérifiez votre connexion internet et l'URL avant de réessayer");
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

        Thread thread = new Thread(task);

        if(btn.getText().equals("Télécharger"))
        {
            btn.setText("Annuler");
            pb.progressProperty().bind(task.progressProperty());
            pb2.progressProperty().bind(task.progressProperty());
            pb.setVisible(true);
            pb2.setVisible(true);

            thread.start();
        }
        else
        {
            task.cancel();
            thread.interrupt();
        }
    }

    @FXML
    private void pathChooser(ActionEvent event) {
        DirectoryChooser dirChooser = new DirectoryChooser();

        Button choose = (Button) event.getSource();
        Scene sc = choose.getScene();
        Stage stage = (Stage) sc.getWindow();
        TextField pathInput = ((TextField) sc.lookup("#pathInput"));
        if(pathInput.getText().equals(""))
            dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        else
            dirChooser.setInitialDirectory(new File(pathInput.getText()));
        File path = dirChooser.showDialog(stage);
        pathInput.setText(path.getPath() + "/");
    }
}
