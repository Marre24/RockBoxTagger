package com.example.rockboxtagger;

import javafx.scene.image.Image;

import java.io.File;
import java.util.LinkedList;

public class MasterReleaseChooser {

    private static LinkedList<String> albumNames;
    private static String currentAlbum;
    private static String folderDir;

    private static AlbumQuery activeQuery;

    private static final LinkedList<MasterRelease> COMMITTED_MASTER_RELEASES = new LinkedList<>();

    private static boolean finished = false;

    public static boolean isFinished() {
        return finished;
    }

    public static LinkedList<MasterRelease> getCommittedMasterReleases() {
        return COMMITTED_MASTER_RELEASES;
    }

    public static boolean init(String folderPath) {
        albumNames = FolderReader.getAlbumNames(folderPath);
        folderDir = folderPath;

        return albumNames != null && !albumNames.isEmpty() && goNext();
    }

    public static void commit(int i) {
        var mr = activeQuery.commit(i);
        COMMITTED_MASTER_RELEASES.add(mr);
        if (!mr.title().equals(currentAlbum))
            renameFolder(folderDir + "\\" + currentAlbum, mr.title());

        finished = !goNext();
    }

    private static void renameFolder(String from, String to) {
        File dir = new File(from);
        File newDir = new File(to);

        if (!dir.isDirectory()) {
            System.err.println("There is no directory at the given path: " + from);
        } else if (newDir.exists()) {
            System.err.println("The target directory name already exists: " + to);
        } else {
            boolean success = dir.renameTo(newDir);
            if (success) {
                System.out.println("Directory renamed successfully from '" + from + "' to '" + to + "'.");
            } else {
                System.err.println("Failed to rename the directory.");
            }
        }
    }

    public static void reRoll(int i) {
        activeQuery.reRoll(i);
    }

    public static boolean goNext(){
        if (albumNames.isEmpty()){
            System.out.println("Finished!");
            return false;
        }

        currentAlbum = albumNames.getFirst();
        activeQuery = new AlbumQuery();
        activeQuery.startQuery(albumNames.getFirst());
        albumNames.removeFirst();
        return true;
    }

    public static Image getImageFor(int i){
        //String path = ImageDownloader.COVER_ART_PATH + activeQuery.getAlbums()[i].masterID() + ImageDownloader.ENDING;
        String path = activeQuery.getAlbums()[i].imageUrl();
        try {
            return new Image(path);
        } catch (Exception e){
            System.err.println("Could not create " + path + ": " + e.getMessage());
            return null;
        }
    }

    public static String getTitleFor(int i){
        var album = activeQuery.getAlbums()[i];
        return album.artist() + "-" + album.title();
    }

}
