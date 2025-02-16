package com.example.rockboxtagger;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class RockBoxTagger extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        TextField field = new TextField();
        field.setPromptText("Enter album name");

        Button btn = new Button("Download Album Cover for:");

        btn.setOnAction(actionEvent -> {
            DiscogsApi.searchForAlbum(field.getText());
        });

        GridPane root = new GridPane();

        root.setPadding(new Insets(10));
        root.setVgap(10);
        root.setHgap(10);

        GridPane.setConstraints(field, 0, 0, 2, 1);
        GridPane.setConstraints(btn, 0, 1, 2, 1);

        field.setAlignment(Pos.CENTER);
        btn.setAlignment(Pos.CENTER);

        root.getChildren().addAll(field, btn);

        Scene scene = new Scene(root, 400, 150);

        stage.setTitle("Album Cover Downloader");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
