package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import noteblock.*;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import java.util.ArrayList;


public class NoteController {
    public Note note;
    public Button btn_delete;
    public ComboBox<String> cb_topic = new ComboBox<>();
    private Controller pController;
    public TextArea note_content;
    public TextField note_title;
    public Button edit_note;


    public void setNote(Note n){
        note = n;
        note_title.setText(n.getTitle());
        note_content.setText(n.getText());
    }

    public void setParentController(Controller c){
        pController = c;
    }


    /*

    DO KEMMEN DI METHODEN INNI DE A NOTE BERTEFFEN



     */

    public void update(ActionEvent actionEvent) {
        System.out.println("UPDATE");
        if(cb_topic.getSelectionModel().getSelectedItem().equals("")) {
            cb_topic.requestFocus();
        }else {
            int ret = 0;
            String old_text = note.getText();
            note.setText(note_content.getText());
            String old_title = note.getTitle();
            note.setTitle(note_title.getText());
            String old_topic = note.getTopic();
            note.setTopic(cb_topic.getSelectionModel().getSelectedItem());
            System.out.println(Main.authToken);
            try {
                ret = Integer.parseInt(ClientBuilder.newClient()
                        .target("http://localhost:8080/rest/")
                        .path("notes/update/{token}")
                        .resolveTemplate("token", Main.authToken)
                        .request()
                        .put(Entity.xml(note), String.class));
                pController.note_list.refresh();
                System.out.println(ret);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("letz");
                System.out.println(Main.authToken);
                note.setText(old_text);
                note.setTitle(old_title);
                note.setTopic(old_topic);
                Main.focusLogin();
            }
        }
    }

    public void delete(ActionEvent actionEvent) {
        ClientBuilder.newClient()
                .target("http://localhost:8080/rest/")
                .path("notes/del/{token}/{id}")
                .resolveTemplate("token", Main.authToken)
                .resolveTemplate("id", note.getId())
                .request()
                .delete()
                .readEntity(String.class);
        pController.tabs.getTabs().remove(pController.openNotes.get(note));
        ObservableList<Note> items = FXCollections.observableArrayList(Note.getNotes(new ArrayList<Filter>(), Main.authToken));
        Main.mainController.note_list.setItems(items);
    }
}
