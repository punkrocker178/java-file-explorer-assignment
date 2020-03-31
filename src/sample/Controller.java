package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.lucene.document.Document;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;

public class Controller implements Initializable {

    public static String basePath;
    private static final int itemPerRow = 4;
    private static boolean isShowTableView = false;

    private Stage stage;

    public Stack<Node> nodes;
    public Stack<String> paths;
    public ArrayList<ColumnConstraints> columnConstraints;
    public ArrayList<RowConstraints> rowConstraints;
    public List<DocumentModel> results = new ArrayList<>();

    public GridPane gridPane;

    public Label pathError;
    public TextField addressBar;
    public TextField searchField;
    public BorderPane borderPane;
    public ScrollPane scrollPane;
    public StackPane stackPane;
    public Button backBtn;
    public Button searchBtn;
    public DirectoryChooser dirChooser;
    public TableView<DocumentModel> tableView;

    private LuceneController lucene;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        columnConstraints = new ArrayList<>();
        rowConstraints = new ArrayList<>();

        initGridPane();
        setIndexPath();

        tableView = new TableView<>();

        initTableView();

        initSearchBtn();

        pathError.setVisible(false);
        addressBar.addEventHandler(KeyEvent.KEY_PRESSED, goToPath());

        stackPane.getChildren().add(tableView);
        stackPane.getChildren().get(0).setVisible(false);
        stackPane.getChildren().add(gridPane);

        scrollPane.setContent(stackPane);
        borderPane.setCenter(scrollPane);

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

    public void initTableView() {
        TableColumn<DocumentModel, String> nameColumn = new TableColumn<>("File Name");
        nameColumn.setPrefWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));

        TableColumn<DocumentModel, String> pathColumn = new TableColumn<>("File Path");
        pathColumn.setPrefWidth(400);
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        tableView.getColumns().addAll(nameColumn, pathColumn);
    }

    private void initGridPane() {
        gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.gridLinesVisibleProperty().setValue(true);
        setColumnConstrains();
    }

    private void setIndexPath() {
        dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Choose Directory To Index");
        basePath = dirChooser.showDialog(stage).getAbsolutePath();
        lucene = new LuceneController(basePath + "/index");
        try {
            lucene.indexDocs(basePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initSearchBtn() {
        searchBtn.setOnAction(searchBtnClick());
        ImageView image = new ImageView(getClass().getResource("assets/icons/search.png").toString());
        image.setFitHeight(16);
        image.setFitWidth(16);
        searchBtn.setGraphic(image);
    }

    public void createLabel(Path file) {
        ImageView image = new ImageView(getClass().getResource(Utils.getExtensionIcon(file.getFileName())).toString());
        image.setFitHeight(64);
        image.setFitWidth(64);
        Label label = new Label(file.getFileName().toString(), image);
        label.addEventHandler(MouseEvent.MOUSE_CLICKED, clickLabel());
        nodes.add(label);
    }

    public void setColumnConstrains() {
        int i = 0;
        while (i < itemPerRow) {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setHgrow(Priority.ALWAYS);
            constraints.setHalignment(HPos.LEFT);
            constraints.setFillWidth(true);
            constraints.setPercentWidth(25);
            columnConstraints.add(constraints);
            gridPane.getColumnConstraints().add(constraints);
            i++;
        }
    }

    public void setRowConstraints() {
        RowConstraints constraintsRow = new RowConstraints(80);
        constraintsRow.setVgrow(Priority.ALWAYS);
        constraintsRow.setValignment(VPos.TOP);
        constraintsRow.setFillHeight(true);
        rowConstraints.add(constraintsRow);
        gridPane.getRowConstraints().add(constraintsRow);
    }

    public void showDirectories() {

        if (isShowTableView) {
            hideTableView();
        }

        int numRow = (int) Math.ceil(nodes.size() / 4.0);
        for (int row = 0; row < numRow; row++) {
            setRowConstraints();
            for (int col = 0; col < itemPerRow; col++) {
                if (nodes.size() > 0) {
                    gridPane.add(nodes.pop(), col, row);
                }
            }
        }
    }

    public void traverse(Path path) throws IOException {

        if (Files.isDirectory(path)) {

            clearLayout();
            pathError.setVisible(false);

            DirectoryStream<Path> stream = Files.newDirectoryStream(path);
            if (paths.size() > 5) {
                paths.remove(paths.size() - 1);
            }

            System.out.println("\nDirectory " + path.getFileName() + "\t Parent:" + path.getParent());
            paths.push(getPath(path));
            addressBar.setText(getPath(path));

            for (Path file : stream) {
                System.out.print(file.getFileName() + "\t");
                createLabel(file);
            }

        } else {
            throw new FileNotFoundException();
        }

    }

    private void clearLayout() {
        gridPane.getChildren().clear();
        gridPane.getRowConstraints().removeAll(rowConstraints);
    }

    public String getPath(Path path) {
        return Paths.get(path.getParent().toString(), path.getFileName().toString()).toString();
    }

    private void showPathNotFoundError() {
        pathError.setVisible(true);
        clearLayout();
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

    public void showTableView() {
        isShowTableView = true;
        switchLayout();
    }

    public void hideTableView() {
        isShowTableView = false;
        switchLayout();
    }

    public void switchLayout() {
        stackPane.getChildren().get(0).setVisible(isShowTableView);
        stackPane.getChildren().get(1).setVisible(!isShowTableView);
    }

    public void clearSearchedResults() {
        if (!tableView.getItems().isEmpty()) {
            tableView.getItems().removeAll(results);
            results.clear();
        }
    }

    /*--- Events ---*/

    public EventHandler<ActionEvent> searchBtnClick() {
        return event -> {
            clearSearchedResults();
            results.addAll(Utils.mapDocument(lucene.searchFiles("contents", searchField.getText())));
            tableView.getItems().addAll(results);
            if (!isShowTableView) {
                showTableView();
            }

        };
    }

    public EventHandler<MouseEvent> clickLabel() {
        return event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                String directoryName = ((Label) event.getSource()).getText();
                try {
                    traverse(Paths.get(paths.peek(), directoryName));
                    showDirectories();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public EventHandler<KeyEvent> goToPath() {
        return event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    clearSearchedResults();
                    traverse(Paths.get(addressBar.getText()));
                    showDirectories();
                } catch (FileNotFoundException e) {
                    showPathNotFoundError();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

}
