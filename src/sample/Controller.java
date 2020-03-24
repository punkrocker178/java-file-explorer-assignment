package sample;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.Stack;

public class Controller implements Initializable {

    private static final String basePath = "/home/";
    private static final int itemPerRow = 4;

    public Stack<Node> nodes;
    public Stack<String> paths;

    public TextField addressBar;
    public GridPane gridPane;
    public Button backBtn;

    public void click() {
        System.out.println("Clicked");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nodes = new Stack<>();
        paths = new Stack<>();
        paths.add(basePath);
        try {
            traverse(Paths.get(basePath));
            showDirectories();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public EventHandler<MouseEvent> clickLabel() {
        return event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                String directoryName = ((Label)event.getSource()).getText();
                try {
                    traverse(Paths.get(paths.peek() , directoryName));
                    showDirectories();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void createLabel(Path file) {
        Label label = new Label(file.getFileName().toString());
        label.addEventHandler(MouseEvent.MOUSE_CLICKED, clickLabel());
        nodes.add(label);
    }

    public void showDirectories() {
        int numRow = (int) Math.ceil(nodes.size() / 4.0);
        for (int i = 0; i < numRow ; i++) {
            for (int j = 0; j < itemPerRow; j++) {
                if (nodes.size() > 0) {
                    gridPane.add(nodes.pop(), j, i);
                }
            }
        }
    }

    public void traverse(Path path) throws IOException {

        gridPane.getChildren().clear();
        DirectoryStream<Path> stream = Files.newDirectoryStream(path);

        if (Files.isDirectory(path)) {

            if (paths.size() > 5) {
                paths.remove(paths.size() -1);
            }

            System.out.println("\nDirectory " + path.getFileName() + "\t Parent:" + path.getParent());
            paths.push(getPath(path));
            addressBar.setText(getPath(path));
        }

        for (Path file: stream) {

//                if (Files.isRegularFile(file)) {
//                    System.out.print(file.getFileName() + "\t");
//                }

//                if (Files.isDirectory(file)) {
//                    System.out.print(file.getFileName() + "\t");
//                    traverse(file.toAbsolutePath());
//                }
            System.out.print(file.getFileName() + "\t");
            createLabel(file);
//
        }

    }

    public String getPath(Path path) {
        return Paths.get(path.getParent().toString(), path.getFileName().toString()).toString();
    }

    public void goBack() {
        try {
            if (paths.size() > 1) {
                paths.pop();
                traverse(Paths.get(paths.pop()));
            }
            showDirectories();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
