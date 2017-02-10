package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import org.omg.CORBA.Object;

import java.io.File;
import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Crawler");

        Scene sc = new Scene(root);
        primaryStage.setScene(sc);

        // ArrayList<Object> files = getArborescence("pages");
        // displayFiles(files);
        // System.out.println(files);

        createTreeView(sc,"pages");

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static void createTreeView(Scene sc, String pathGet) {

        TreeItem<String> rootItem = createLine("Bibliothèque");
        TreeView<String> tree = new TreeView<>(rootItem);

        File repo = new File(pathGet);

        File[] fileList = repo.listFiles();

        if(fileList != null) {
            TreeItem<String> mainLine = null;

            for(int i = 0 ; i < fileList.length ; i++) {
                String nameFile = fileList[i].toString();

                mainLine = createLine(nameFile);

                rootItem.getChildren().add(mainLine);

                // If the file has children
                if(getArbo(nameFile)) {
                    // createTreeView(sc,nameFile);
                    TreeItem<String> item = new TreeItem<>("Children here");
                    mainLine.getChildren().add(item);
                }

            }
        }

        /*
            TreeItem<String> subLine1 = createLine("index.html");
            TreeItem<String> subLine2 = createLine("index2.html");

            TreeItem<String> item = new TreeItem<>("Message 1");
            subLine1.getChildren().add(item);
        */



        Pane pane = (Pane) sc.lookup("#paneTreeView");

        pane.getChildren().add(tree);
    }

    public static TreeItem<String> createLine(String val) {
        TreeItem<String> item = new TreeItem<>(val);
        item.setExpanded(true);

        return item;
    }

    private static boolean getArbo(String path) {
        return true;
    }

}
