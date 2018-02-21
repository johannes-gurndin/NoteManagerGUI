package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import noteblock.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Controller {

    public ListView<Note> note_list = new ListView<Note>();
    public TabPane tabs;
    public TextField tf_user;
    public TextField tf_pass;
    public Button btn_login;
    public Label login_lable;
    public Label lbl_pass;
    public HashMap<Note, Tab> openNotes = new HashMap<>();
    public HashMap<Tab, Note> openTabs = new HashMap<>();

    public void openNote(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            Note currentItemSelected = note_list.getSelectionModel().getSelectedItem();

            if(openNotes.keySet().contains(currentItemSelected)){
                tabs.getSelectionModel().select(openNotes.get(currentItemSelected));
            }else {
                Tab t = new Tab("NOTE");
                t.closableProperty().setValue(true);
                t.setOnCloseRequest(event -> {
                    Tab tt = (Tab)(event.getSource());
                    openNotes.remove(openTabs.get(tt));
                    openTabs.remove(tt);
                });
                FXMLLoader loader = new FXMLLoader(getClass().getResource("note.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                t.setContent(root);
                NoteController cntrl = loader.getController();
                openNotes.put(currentItemSelected, t);
                openTabs.put(t, currentItemSelected);
                cntrl.setNote(currentItemSelected);
                cntrl.setParentController(this);
                tabs.getTabs().add(t);
            }
        }
    }

    public void login(ActionEvent actionEvent) {
        String token = ClientBuilder.newClient()
                .target("http://localhost:8080/rest/")
                .path("user/login/{username}")
                .resolveTemplate("username", tf_user.getText())
                .request()
                .post(Entity.entity(tf_pass.getText(), MediaType.TEXT_PLAIN), String.class);
        if (!token.equals("false")) {
            tf_pass.visibleProperty().setValue(false);
            tf_user.visibleProperty().setValue(false);
            lbl_pass.visibleProperty().setValue(false);
            btn_login.visibleProperty().setValue(false);
            login_lable.setText("Logged in as: "+tf_user.getText());
            login_lable.setStyle("-fx-text-fill: green; -fx-font-size: 16;");
            Main.authToken = token;
            Main.username = tf_user.getText();
            ObservableList<Note> items = FXCollections.observableArrayList(Note.getNotes(new ArrayList<Filter>(), Main.authToken));
            Main.mainController.note_list.setItems(items);
        } else {
            tf_user.setStyle("-fx-text-fill: red");
        }

    }

    public void addNewNote(ActionEvent actionEvent) {
        Tab t = new Tab("NOTE");
        t.closableProperty().setValue(true);
        t.setOnCloseRequest(event -> {
            Tab tt = (Tab)(event.getSource());
            openNotes.remove(openTabs.get(tt));
            openTabs.remove(tt);
        });
        FXMLLoader loader = new FXMLLoader(getClass().getResource("note.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        t.setContent(root);
        NoteController cntrl = loader.getController();
        Note n = new Note();
        n.setCreatorname(Main.username);
        n.setTopic("dddd");
        cntrl.setNote(n);
        cntrl.edit_note.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("NEW");
                cntrl.note.setText(cntrl.note_content.getText());
                cntrl.note.setTitle(cntrl.note_title.getText());
                cntrl.note.setId(Integer.parseInt(ClientBuilder.newClient()
                        .target("http://localhost:8080/rest/")
                        .path("notes/insert/{token}")
                        .resolveTemplate("token", Main.authToken)
                        .request()
                        .post(Entity.xml(cntrl.note), String.class)));
                System.out.println("ID: "+cntrl.note.getId());
                ObservableList<Note> items = FXCollections.observableArrayList(Note.getNotes(new ArrayList<Filter>(), Main.authToken));
                Main.mainController.note_list.setItems(items);
                cntrl.edit_note.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        cntrl.update(event);
                    }
                });
            }
        });
        tabs.getTabs().add(t);

    }
}
