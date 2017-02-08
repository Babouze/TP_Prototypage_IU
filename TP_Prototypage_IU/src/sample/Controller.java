package sample;

import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;

import java.io.*;
import java.net.URL;

public class Controller
{
    private DoubleProperty downloadProgress;

    public Controller()
    {
        downloadProgress = new SimpleDoubleProperty(0.0);
    }

    @FXML
    private void download(ActionEvent event) throws IOException {
        Button btn = (Button) event.getSource();
        Scene sc = btn.getScene();
        ProgressBar pb = (ProgressBar) sc.lookup("#downloadBar");
        ProgressBar pb2 = (ProgressBar) sc.lookup("#downloadBarB");

        if(btn.getText().equals("Télécharger"))
        {
//            URL url;
//            InputStream is = null;
//            BufferedReader br;
//            String line;
            TextField pathInput = ((TextField) sc.lookup("#pathInput"));
            TextField urlInput = ((TextField) sc.lookup("#urlInput"));

            try
            {
                HTTrack downloader = new HTTrack();

                //Splits website given into parts separated by "/"
                String site[] = urlInput.getText().split("/");
                String host = site[2];

                //Building sites directory structure; Exclude last element in site (filename)
                String directories = "";
                for(int i = 2; i < site.length - 1; i++){
                    directories += site[i] + "/";
                }

                //TODO: convertir les espaces en '\ '
                directories = pathInput.getText() + directories;

                File folders = new File(directories);
                folders.mkdirs();

                String path = site[site.length - 1];

                downloader.scanAndDownload(path, host, directories, 0);
//                url = new URL(urlInput.getText());
//                is = url.openStream();  // throws an IOException
//                br = new BufferedReader(new InputStreamReader(is));
//
//                PrintWriter writer = new PrintWriter(pathInput.getText() + "url1.html", "UTF-8");
//
//                while ((line = br.readLine()) != null)
//                {
//                    writer.println(line);
//                }
//                writer.close();
            }
            catch (Exception ioe)
            {
                ioe.printStackTrace();
            }
            finally
            {
//                    if (is != null) is.close();
            }

            btn.setText("Annuler");
            pb.progressProperty().bind(downloadProgress);
            pb2.progressProperty().bind(downloadProgress);
            pb.setVisible(true);
            pb2.setVisible(true);
            downloadProgress.setValue(downloadProgress.getValue() + 0.1);
        }
        else
        {
            btn.setText("Télécharger");
            pb.setVisible(false);
            pb2.setVisible(false);
            downloadProgress.setValue(downloadProgress.getValue() - 0.05);
        }
    }
}
