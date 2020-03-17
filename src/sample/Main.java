package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.*;

public class Main extends Application {

    private static final String basePath = "/home/stevele/Desktop/NNLT";

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        try {
            traverse(Paths.get(basePath));
        } catch (IOException | DirectoryIteratorException x) {
            System.err.println(x);
        }
    }

    public static void traverse(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            DirectoryStream<Path> stream = Files.newDirectoryStream(path);
            System.out.println("\nDirectory " + path.getFileName() + "\t Parent:" + path.getParent());
            for (Path file: stream) {
                if (Files.isRegularFile(file)) {
                    System.out.print(file.getFileName() + "\t");
                } else {
                    traverse(file.toAbsolutePath());
                }
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
