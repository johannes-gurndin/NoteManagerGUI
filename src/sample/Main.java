package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import noteblock.Filter;
import noteblock.Note;

import java.util.ArrayList;

public class Main extends Application {

    public static String authToken = "hh";
    public static Controller mainController;

    public static void focusLogin(){
        mainController.tf_user.requestFocus();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main_window.fxml"));
        Parent root = loader.load();
        mainController = loader.getController();
        mainController.tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        ObservableList<Note> items = FXCollections.observableArrayList(Note.getNotes(new ArrayList<Filter>(), authToken));
        mainController.note_list.setItems(items);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300,275));
        primaryStage.show();
    }
    public static void main(String[] args){
        launch(args);
    }
}
