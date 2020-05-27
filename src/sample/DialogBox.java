package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.beans.EventHandler;


public class DialogBox {

    static String folderName;

    public static String showPopup() {
        Stage stage = new Stage();

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Create Directory");
        stage.setMinWidth(250);

        Label label = new Label("Enter directory name");
        TextField textField = new TextField();
        Button btn = new Button("OK");

        btn.setOnAction(event -> {
                    folderName = textField.getText().toString();
                    stage.close();
                }
        );

        VBox layout = new VBox(20);
        layout.getChildren().addAll(label, textField, btn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10, 10, 10, 10));

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.showAndWait();
        
        return folderName;
    }
}
