package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import static sample.Main.createTreeView;

public class Controller implements Initializable
{
    @FXML
    private ChoiceBox language;

    private ResourceBundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        language.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Locale locale;
                Scene sc = language.getScene();
                if(newValue.equals(0)) {
                    locale = new Locale("fr" , "FR");
                    bundle = ResourceBundle.getBundle("MessagesBundle", locale);
                    try {
                        sc.setRoot(FXMLLoader.load(getClass().getResource("sample.fxml"), bundle, null, null, Charset.forName("UTF-8")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    createTreeView(sc,"pages", bundle);
                }
                else if(newValue.equals(1)) {
                    locale = new Locale("en" , "US");
                    bundle = ResourceBundle.getBundle("MessagesBundle", locale);
                    try {
                        sc.setRoot(FXMLLoader.load(getClass().getResource("sample.fxml"), bundle, null, null, Charset.forName("UTF-8")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    createTreeView(sc,"pages", bundle);
                }
                else if(newValue.equals(2)) {
                    locale = new Locale("ch" , "CH");
                    bundle = ResourceBundle.getBundle("MessagesBundle", locale);
                    try {
                        sc.setRoot(FXMLLoader.load(getClass().getResource("sample.fxml"), bundle, null, null, Charset.forName("UTF-8")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    createTreeView(sc,"pages", bundle);
                }
            }
        });
        bundle = resources;
    }

    @FXML
    private void download(ActionEvent event) throws IOException {
        Button btn = (Button) event.getSource();
        Scene sc = btn.getScene();
        ProgressBar pb = (ProgressBar) sc.lookup("#downloadBar");

        TextField pathInput = ((TextField) sc.lookup("#pathInput"));
        TextField urlInput = ((TextField) sc.lookup("#urlInput"));
        ChoiceBox depthCB = ((ChoiceBox) sc.lookup("#depth"));

        if(pathInput.getText().equals(""))
        {
            ButtonType loginButtonType = new ButtonType(bundle.getString("OK"), ButtonBar.ButtonData.OK_DONE);
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle(bundle.getString("error"));
            dialog.setContentText(bundle.getString("pathError"));
            dialog.getDialogPane().getButtonTypes().add(loginButtonType);
            dialog.getDialogPane().lookupButton(loginButtonType).setDisable(false);
            dialog.showAndWait();
            return;
        }
        else if(urlInput.getText().equals(""))
        {
            ButtonType loginButtonType = new ButtonType(bundle.getString("OK"), ButtonBar.ButtonData.OK_DONE);
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle(bundle.getString("error"));
            dialog.setContentText(bundle.getString("urlError"));
            dialog.getDialogPane().getButtonTypes().add(loginButtonType);
            dialog.getDialogPane().lookupButton(loginButtonType).setDisable(false);
            dialog.showAndWait();
            return;
        }

        //Splits website given into parts separated by "/"
        String site[] = urlInput.getText().split("/");
        if(site.length < 2)
        {
            ButtonType loginButtonType = new ButtonType(bundle.getString("OK"), ButtonBar.ButtonData.OK_DONE);
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle(bundle.getString("error"));
            dialog.setContentText(bundle.getString("urlFormatError"));
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
            ButtonType loginButtonType = new ButtonType(bundle.getString("OK"), ButtonBar.ButtonData.OK_DONE);
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle(bundle.getString("success"));
            dialog.setContentText(bundle.getString("downloadSuccessfull"));
            dialog.getDialogPane().getButtonTypes().add(loginButtonType);
            dialog.getDialogPane().lookupButton(loginButtonType).setDisable(false);

            pb.setVisible(false);
            btn.setText(bundle.getString("download"));

            dialog.showAndWait();
        });
        task.setOnFailed(e -> {
            ButtonType loginButtonType = new ButtonType(bundle.getString("OK"), ButtonBar.ButtonData.OK_DONE);
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle(bundle.getString("error"));
            dialog.setContentText(bundle.getString("downloadFailed"));
            dialog.getDialogPane().getButtonTypes().add(loginButtonType);
            dialog.getDialogPane().lookupButton(loginButtonType).setDisable(false);

            pb.setVisible(false);
            btn.setText(bundle.getString("download"));

            dialog.showAndWait();
        });
        task.setOnCancelled(e -> {
            //TODO: Effacer le fichier téléchargé et arrêter l'exécution
            pb.setVisible(false);
            btn.setText(bundle.getString("download"));
        });

        Thread thread = new Thread(task);

        if(btn.getText().equals(bundle.getString("download")))
        {
            btn.setText(bundle.getString("cancel"));
            pb.progressProperty().bind(task.progressProperty());
            pb.setVisible(true);

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

    @FXML
    private void btnSeePage(ActionEvent event) throws Exception {

        Button btn = (Button) event.getSource();
        Scene sc = btn.getScene();
        Label label = (Label) sc.lookup("#labelPage");

        String pageName = label.getText();

        // System.out.println("Visu de la page : " + pageName);

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(pageName);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());

        // Getting the entire path of the file
        TreeView<String> tree = (TreeView<String>) sc.lookup("#treeBibli");
        TreeItem<String> selectedItem = tree.getSelectionModel().getSelectedItem();

        String realPath = selectedItem.getValue();

        while(selectedItem.getParent().getValue() != "pages") {
            realPath = selectedItem.getParent().getValue() + "/" + realPath;
            selectedItem = selectedItem.getParent();
        }

        TextField pathInput = ((TextField) sc.lookup("#pathInput"));

        realPath = pathInput.getText() + "/" + realPath;
        System.out.println(realPath);

        // Creating the web view to display the page
        WebView browser = new WebView();

        URL url = getClass().getClassLoader().getResource(realPath);

        browser.getEngine().load(realPath);

        dialog.getDialogPane().setContent(browser);

        dialog.show();
    }

    @FXML
    private void btnDeletePage(ActionEvent event) {
        System.out.println("Bouton delete cliqué");
    }
}
