import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        int port = 8000;
        try {
            ServerSocket socket = new ServerSocket(port);
            while (true) {
                Socket client = socket.accept();
                handleClient(client);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static void handleClient(Socket socket){
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            String response = command(bufferedReader.readLine());
            printWriter.println(response);
            bufferedReader.close();
            printWriter.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    public static String command(String command){
        String [] parts = command.split(" ");
        if(parts.length == 1){
            return "NO SUCH COMMAND";
        }

        switch (parts[0]){
            case "FILE" :
                if(parts[1].equals("LIST")){
                    return getFileList();
                }else {
                    return "NO SUCH COMMAND";
                }
            case "GET" :
                if(parts.length == 2){
                    return getFileContent(parts[1]);
                }
                else {
                    return "NO SUCH COMMAND";
                }

            default:
                return "NO SUCH COMMAND";
        }


    }

    public static String getFileList(){
        String dir = "files";
        String result ="";
        File rootDirectory = new File(dir);
        if(rootDirectory.isDirectory()){
            File[] listOfFiles = rootDirectory.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                if(listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".txt")){
                    if (i != listOfFiles.length - 1){
                        result += listOfFiles[i].getName() + "\n";
                    }else{
                        result += listOfFiles[i].getName();
                    }
                }
            }
        }
        return result;
    }

    public static String getFileContent(String filename){
        try {
            Path filePath = Paths.get("files", filename);
            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    return content.toString();
                }
            } else {
                return "NO SUCH FILE";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

}