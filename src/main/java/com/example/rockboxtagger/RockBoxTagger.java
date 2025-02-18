package com.example.rockboxtagger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class RockBoxTagger extends Application {

    // TODO: 2025-02-18 create listview for the folders imported
    
    private static Stage stage;
    private static Scene mainScene;
    private static Scene masterReleaseChooser;
    private static Scene trackListChooser;
    private TextField pathField;

    private static final ListView<String> trackLists = new ListView<>();
    private static Label count;

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
        createMasterReleaseScene();
        createTrackListScene();

        stage.setTitle("Rockbox Tagger");
        stage.setScene(mainScene);
        stage.show();
    }

    private void createTrackListScene() {
        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.setVgap(10);
        root.setHgap(10);


        count = new Label();
        Button next = new Button("Next");
        Button previous = new Button("Previous");
        Button commit = new Button("Commit tracklist");

        next.setOnAction(event -> {
            trackLists.setItems(TrackListChooser.getNextTrackList());
            count.setText(TrackListChooser.getCount());
        });

        previous.setOnAction(event -> {
            trackLists.setItems(TrackListChooser.getPreviousTrackList());
            count.setText(TrackListChooser.getCount());
        });

        commit.setOnAction(event -> {
            if (!TrackListChooser.commitTrackList())
                stage.setScene(mainScene);
        });

        GridPane.setConstraints(count, 1, 0, 1, 1);
        GridPane.setConstraints(next, 2, 0, 1, 1);
        GridPane.setConstraints(previous, 0, 0, 1, 1);
        GridPane.setConstraints(commit, 3, 0, 1, 1);
        GridPane.setConstraints(trackLists, 0, 1, 4, 1);

        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(next, commit, trackLists, count, previous);

        trackListChooser = new Scene(root, 600 , 800);
    }

    private void createMainScene() {
        pathField = new TextField();
        pathField.setPromptText("Enter album name");

        Button btn = new Button("Check folder for albums:");
        btn.setOnAction(event -> checkFolderForAlbums());


        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.setVgap(10);
        root.setHgap(10);

        GridPane.setConstraints(pathField, 0, 0, 2, 1);
        GridPane.setConstraints(btn, 0, 1, 2, 1);

        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(pathField, btn);

        mainScene = new Scene(root, 400, 200);
    }

    private void createMasterReleaseScene() {
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

        masterReleaseChooser = new Scene(imageGrid, 400, 400);
    }

    private static void commit(MouseEvent event) {
        if (event.getSource() instanceof ImageView){
            var id = ((ImageView) event.getSource()).getId();

            if (Objects.equals(id, "0"))
                MasterReleaseChooser.commit(0);
            if (Objects.equals(id, "1"))
                MasterReleaseChooser.commit(1);
            if (Objects.equals(id, "2"))
                MasterReleaseChooser.commit(2);
            if (Objects.equals(id, "3"))
                MasterReleaseChooser.commit(3);

            if (MasterReleaseChooser.isFinished()){
                TrackListChooser.init(MasterReleaseChooser.getCommittedMasterReleases());
                trackLists.setItems(TrackListChooser.getNextTrackList());
                count.setText(TrackListChooser.getCount());
                stage.setScene(trackListChooser);
            }
        }
    }

    private void checkFolderForAlbums() {
        String folderPath = pathField.getText();
        if (folderPath != null && !folderPath.trim().isEmpty()) {
            if (MasterReleaseChooser.init(folderPath.trim()))
                stage.setScene(masterReleaseChooser);
            setImages();
        }
    }

    private static void refresh(ActionEvent actionEvent) {
        var source = actionEvent.getSource();
        if (source instanceof Button) {
            var id = ((Button) source).getId();

            if (Objects.equals(id, "0"))
                MasterReleaseChooser.reRoll(0);
            if (Objects.equals(id, "1"))
                MasterReleaseChooser.reRoll(1);
            if (Objects.equals(id, "2"))
                MasterReleaseChooser.reRoll(2);
            if (Objects.equals(id, "3"))
                MasterReleaseChooser.reRoll(3);
            setImages();
        }
    }

    private static void setImages(){
        topLeftIV.setImage(MasterReleaseChooser.getImageFor(0));
        topRightIV.setImage(MasterReleaseChooser.getImageFor(1));
        bottomLeftIV.setImage(MasterReleaseChooser.getImageFor(2));
        bottomRightIV.setImage(MasterReleaseChooser.getImageFor(3));

        topLeftLabel.setText(MasterReleaseChooser.getTitleFor(0));
        topRightLabel.setText(MasterReleaseChooser.getTitleFor(1));
        bottomLeftLabel.setText(MasterReleaseChooser.getTitleFor(2));
        bottomRightLabel.setText(MasterReleaseChooser.getTitleFor(3));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
