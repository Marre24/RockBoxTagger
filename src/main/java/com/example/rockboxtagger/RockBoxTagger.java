package com.example.rockboxtagger;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RockBoxTagger extends Application {

    private Stage stage;
    private Scene mainScene;
    private Scene imageScene;
    private TextField pathField;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        createMainScene();
        createImageScene();

        this.stage.setTitle("Album Cover Downloader");
        this.stage.setScene(mainScene);
        this.stage.show();
    }

    private void createMainScene() {
        pathField = new TextField();
        pathField.setPromptText("Enter album name");

        Button btn = new Button("Download Album Cover for:");
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

        // Sample images (Replace with actual images)
        String sampleImage = "https://via.placeholder.com/150";

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                ImageView imageView = new ImageView(new Image(sampleImage));
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);

                Label label = new Label("Image " + (i * 2 + j + 1));

                VBox imageBox = new VBox();
                imageBox.setAlignment(Pos.CENTER);
                imageBox.getChildren().addAll(label, imageView);

                imageGrid.add(imageBox, j, i);
            }
        }

        Button backButton = new Button("Back to Main");
        backButton.setOnAction(event -> stage.setScene(mainScene));

        VBox root = new VBox(10, imageGrid, backButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));

        imageScene = new Scene(root, 400, 400);
    }

    private void checkFolderForAlbums() {
        String folderPath = pathField.getText();
        if (folderPath != null && !folderPath.trim().isEmpty()) {
            FolderReader.getAlbumNames(folderPath);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
