package com.example.rockboxtagger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class RockBoxTagger extends Application {

    private static Stage stage;
    private static Scene mainScene;
    private static Scene imageScene;
    private TextField pathField;

    private static ImageView topLeftIV;
    private static ImageView topRightIV;
    private static ImageView bottomLeftIV;
    private static ImageView bottomRightIV;
    private static Label topLeftLabel;
    private static Label topRightLabel;
    private static Label bottomLeftLabel;
    private static Label bottomRightLabel;

    @Override
    public void start(Stage s) {
        stage = s;
        createMainScene();
        createImageScene();

        stage.setTitle("Rockbox Tagger");
        stage.setScene(mainScene);
        stage.show();
    }

    private void createMainScene() {
        pathField = new TextField();
        pathField.setPromptText("Enter album name");

        Button btn = new Button("Check folder for albums:");
        btn.setOnAction(event -> checkFolderForAlbums());

        Button switchSceneBtn = new Button("Go to Image Gallery");
        switchSceneBtn.setOnAction(event -> stage.setScene(imageScene));

        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.setVgap(10);
        root.setHgap(10);

        GridPane.setConstraints(pathField, 0, 0, 2, 1);
        GridPane.setConstraints(btn, 0, 1, 2, 1);
        GridPane.setConstraints(switchSceneBtn, 0, 2, 2, 1);

        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(pathField, btn, switchSceneBtn);

        mainScene = new Scene(root, 400, 200);
    }

    private void createImageScene() {
        GridPane imageGrid = new GridPane();
        imageGrid.setPadding(new Insets(10));
        imageGrid.setHgap(10);
        imageGrid.setVgap(10);
        imageGrid.setAlignment(Pos.CENTER);

        String sampleImage = "https://via.placeholder.com/150";


        topLeftIV = new ImageView(new Image(sampleImage));
        topRightIV = new ImageView(new Image(sampleImage));
        bottomLeftIV = new ImageView(new Image(sampleImage));
        bottomRightIV = new ImageView(new Image(sampleImage));

        topLeftIV.setId("0");
        topRightIV.setId("1");
        bottomLeftIV.setId("2");
        bottomRightIV.setId("3");

        topLeftIV.setFitWidth(150);
        topRightIV.setFitWidth(150);
        bottomLeftIV.setFitWidth(150);
        bottomRightIV.setFitWidth(150);

        topLeftIV.setFitHeight(150);
        topRightIV.setFitHeight(150);
        bottomLeftIV.setFitHeight(150);
        bottomRightIV.setFitHeight(150);

        topLeftIV.setOnMouseClicked(RockBoxTagger::commit);
        topRightIV.setOnMouseClicked(RockBoxTagger::commit);
        bottomLeftIV.setOnMouseClicked(RockBoxTagger::commit);
        bottomRightIV.setOnMouseClicked(RockBoxTagger::commit);

        topLeftLabel = new Label("Not available");
        topRightLabel = new Label("Not available");
        bottomLeftLabel = new Label("Not available");
        bottomRightLabel = new Label("Not available");


        VBox imageBox0 = new VBox();
        Button button0 = new Button("0");
        button0.setId("0");
        button0.setText("Refresh");
        button0.setOnAction(RockBoxTagger::refresh);
        imageBox0.setAlignment(Pos.CENTER);
        imageBox0.getChildren().addAll(topLeftLabel, button0, topLeftIV);

        VBox imageBox1 = new VBox();
        Button button1 = new Button("1");
        button1.setId("1");
        button1.setText("Refresh");
        button1.setOnAction(RockBoxTagger::refresh);
        imageBox1.setAlignment(Pos.CENTER);
        imageBox1.getChildren().addAll(topRightLabel, button1, topRightIV);

        VBox imageBox2 = new VBox();
        Button button2 = new Button("2");
        button2.setId("2");
        button2.setText("Refresh");
        button2.setOnAction(RockBoxTagger::refresh);
        imageBox2.setAlignment(Pos.CENTER);
        imageBox2.getChildren().addAll(bottomLeftLabel, button2, bottomLeftIV);

        VBox imageBox3 = new VBox();
        Button button3 = new Button("3");
        button3.setId("3");
        button3.setText("Refresh");
        button3.setOnAction(RockBoxTagger::refresh);
        imageBox3.setAlignment(Pos.CENTER);
        imageBox3.getChildren().addAll(bottomRightLabel, button3, bottomRightIV);

        imageGrid.add(imageBox0, 0, 0);
        imageGrid.add(imageBox1, 1, 0);
        imageGrid.add(imageBox2, 0, 1);
        imageGrid.add(imageBox3, 1, 1);

        imageScene = new Scene(imageGrid, 400, 400);
    }

    private static void commit(MouseEvent event) {
        if (event.getSource() instanceof ImageView){
            var id = ((ImageView) event.getSource()).getId();

            if (Objects.equals(id, "0"))
                Driver.commit(0);
            if (Objects.equals(id, "1"))
                Driver.commit(1);
            if (Objects.equals(id, "2"))
                Driver.commit(2);
            if (Objects.equals(id, "3"))
                Driver.commit(3);

            if (Driver.isFinished())
                stage.setScene(mainScene);
        }
    }

    private void checkFolderForAlbums() {
        String folderPath = pathField.getText();
        if (folderPath != null && !folderPath.trim().isEmpty()) {
            if (Driver.init(folderPath.trim()))
                stage.setScene(imageScene);
            setImages();
        }
    }

    private static void refresh(ActionEvent actionEvent) {
        var source = actionEvent.getSource();
        if (source instanceof Button) {
            var id = ((Button) source).getId();

            if (Objects.equals(id, "0"))
                Driver.reRoll(0);
            if (Objects.equals(id, "1"))
                Driver.reRoll(1);
            if (Objects.equals(id, "2"))
                Driver.reRoll(2);
            if (Objects.equals(id, "3"))
                Driver.reRoll(3);
            setImages();
        }
    }

    private static void setImages(){
        topLeftIV.setImage(Driver.getImageFor(0));
        topRightIV.setImage(Driver.getImageFor(1));
        bottomLeftIV.setImage(Driver.getImageFor(2));
        bottomRightIV.setImage(Driver.getImageFor(3));

        topLeftLabel.setText(Driver.getTitleFor(0));
        topRightLabel.setText(Driver.getTitleFor(1));
        bottomLeftLabel.setText(Driver.getTitleFor(2));
        bottomRightLabel.setText(Driver.getTitleFor(3));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
