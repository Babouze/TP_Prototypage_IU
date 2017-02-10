package sample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class HTTrack {

    public static void scanAndDownload(String pathname, String hostname, String directory, int depth) throws IOException{

        //Creating connection
        Socket mysocket = new Socket(hostname, 80);

        OutputStreamWriter out = new OutputStreamWriter(mysocket.getOutputStream());
        Scanner in = new Scanner(mysocket.getInputStream(), "UTF-8");

        out.write("GET https://" + directory.replace(hostname, "") + pathname + " HTTP/1.0\r\nHost : " + hostname + "\r\nAccept : text/html\r\nIf-Modified-Since : Saturday, 26-March-2010 08:17:01 GMT\r\nUser-Agent : Mozilla/5.0\n\r\n");
        out.flush();

        System.out.println("GET " + directory.replace(hostname, "") + pathname + " HTTP/1.0\r\nHost: " + hostname + "\r\nAccept : text/html\r\nIf-Modified-Since : Saturday, 26-March-2010 08:17:01 GMT\r\nUser-Agent : Mozilla/5.0\n\r\n");

        //If pathname contains directories, create them
        String splitpath[] = pathname.split("/");
        String newdirectories = directory;
        for(int i = 0; i < splitpath.length - 1; i++){
            newdirectories += splitpath[i] + "/";
        }

        new File(newdirectories).mkdirs();
        FileOutputStream page = new FileOutputStream(new File(directory + pathname)); //filename is at the end of path


        //Reading and scanning input stream
        boolean inbody = false;
        while(in.hasNextLine()){
            String line = in.nextLine();
            System.out.println(line);
            if(line.isEmpty() && !inbody){
                inbody = true;
                line = in.nextLine();
            }
            if(inbody){
                page.write(line.getBytes());
                page.write("\n".getBytes());

                //Check for matching hostname rather than absolute vs relative
                if(line.contains("a href")){
                    //Check if link is absolute or relative
                    if(!line.contains("http")){
                        //filename will be in between " marks and will be in link[1]
                        String link[] = line.split("\"");

                        //Recursively call scanAndDownload with new filename
                        if(depth < 2){
                            scanAndDownload(link[1], hostname, directory, depth + 1);
                        }
                    }
                    //Download absolute links of the same host
                    else {
                        String link[] = line.split("/");
                        //hostname is in link[2]
                        if(link[2].contentEquals(hostname)){
                            //building path and directory
                            String newdir = "";
                            for(int i = 2; i < link.length - 1; i++){
                                newdir += link[i];
                                scanAndDownload(link[link.length - 1], hostname, directory, depth + 1);
                            }
                        }
                    }
                }
            }

        }
    }


//    public static void main(String[] args) throws UnknownHostException, IOException {
//
//        if(args.length != 1){
//            System.err.println("Usage: java httrack http://[website to be downloaded]");
//            System.exit(1);
//        }
//
//        //Splits website given into parts separated by "/"
//        String site[] = args[0].split("/");
//        String host = site[2];
//
//        //Building sites directory structure; Exclude last element in site (filename)
//        String directories = "";
//        for(int i = 2; i < site.length - 1; i++){
//            directories += site[i] + "/";
//        }
//
//        File folders = new File(directories);
//        folders.mkdirs();
//
//        String path = site[site.length - 1];
//
//        scanAndDownload(path, host, directories, 0);
//    }

}
