package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Crawler");

        Scene sc = new Scene(root);
        primaryStage.setScene(sc);

        createTreeView(sc,"pages");

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private static void createTreeView(Scene sc, String pathGet) {

        TreeItem<String> rootItem = createLine("Bibliothèque");
        TreeView<String> tree = new TreeView<>(rootItem);

        tree.setPrefSize(475,200);

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
