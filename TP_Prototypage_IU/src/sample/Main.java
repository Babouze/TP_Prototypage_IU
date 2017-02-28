package sample;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
    private static ResourceBundle bundle;

    @Override
    public void start(Stage primaryStage) throws Exception{
        bundle = ResourceBundle.getBundle("MessagesBundle", new Locale("fr", "FR"));

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"), bundle, null, null, Charset.forName("UTF-8"));
        primaryStage.setTitle("Crawler");

        Scene sc = new Scene(root);
        primaryStage.setScene(sc);

        createTreeView(sc,"pages", bundle);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void createTreeView(Scene sc, String pathGet, ResourceBundle p_bundle) {
        bundle = p_bundle;
        TreeItem<String> rootItem = createLine(bundle.getString("library"));
        TreeView<String> tree = new TreeView<>(rootItem);
        tree.setId("treeBibli");

        tree.setPrefSize(475,200);

        tree.getSelectionModel().selectedItemProperty().addListener(new javafx.beans.value.ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {
                Label labelPage = (Label) sc.lookup("#labelPage");


                Button btnSee = (Button) sc.lookup("#btnSeePage");
                Button btnDelete = (Button) sc.lookup("#btnDelPage");

                if(newValue.isLeaf()) { // No child, we display the buttons
                    labelPage.setText(newValue.getValue());
                    btnSee.setVisible(true);
                    btnDelete.setVisible(true);
                }
                else { // The selected item is a directory, we hide the buttons
                    labelPage.setText("");
                    btnSee.setVisible(false);
                    btnDelete.setVisible(false);
                }
            }
        });

        // Path of all pages
        File path = new File(pathGet);

        createTree(path,rootItem);

        Pane pane = (Pane) sc.lookup("#paneTreeView");
        pane.getChildren().add(tree);
    }

    private static TreeItem<String> createLine(String val) {
        TreeItem<String> item = new TreeItem<>(val);
        item.setExpanded(true);
        return item;
    }

    private static void createTree(File path, TreeItem<String> root) {

        if(path.isDirectory()) {
            // Directory

            // Getting the name of the directory
            String name = getNameOfPage(path.toString());

            // Creating a new root inside the tree
            TreeItem<String> newRoot = new TreeItem<>(name);
            root.getChildren().add(newRoot);

            File[] list = path.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    createTree(list[i],newRoot);
                }
            }
        }
        else {
            // Getting the name of the page
            String name = getNameOfPage(path.toString());

            // Simple line
            TreeItem<String> newItem = new TreeItem<>(name);
            root.getChildren().add(newItem);
        }

    }

    private static String getNameOfPage(String path) {
        String[] dir = path.split("/");
        String name = "";
        if (dir != null) {
            for (int i = 0; i < dir.length; i++) {
                name = dir[i];
            }
        }
        return name;
    }

}
