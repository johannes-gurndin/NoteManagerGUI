package sample;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import noteblock.*;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import java.util.ArrayList;


public class NoteController {
    private Note note;
    public TextArea note_content;
    public TextField note_title;
    public Button edit_note;

    public void setNote(Note n){
        note = n;
        note_title.setText(n.getTitle());
        note_content.setText(n.getText());
    }


    /*

    DO KEMMEN DI METHODEN INNI DE A NOTE BERTEFFEN



     */
    public static ArrayList<Note> getNotes(ArrayList<Filter> filter, String token){
        return ClientBuilder.newClient()
                .target("http://localhost:8080/rest/")
                .path("notes/get/{token}")
                .resolveTemplate("token", token)
                .request()
                .post(Entity.xml(new GenericEntity<ArrayList<Filter>>(filter){}),new GenericType<ArrayList<Note>>() {});
    }
    public static int addNewNote(Note n, String token){
        int ret = 0;
        try {
            ret = Integer.parseInt(ClientBuilder.newClient()
                    .target("http://localhost:8080/rest/")
                    .path("notes/insert/{token}")
                    .resolveTemplate("token", token)
                    .request()
                    .post(Entity.xml(n), String.class));

        } catch (NumberFormatException e){}
        return ret;
    }

    public static int update(Note n, String token){
        int ret = 0;
        try {
            ret = Integer.parseInt(ClientBuilder.newClient()
                    .target("http://localhost:8080/rest/")
                    .path("notes/update/{token}")
                    .resolveTemplate("token", token)
                    .request()
                    .post(Entity.xml(n), String.class));

        } catch (NumberFormatException e){}
        return ret;
    }
    public static boolean delete(int id, String token){
        return Boolean.valueOf(ClientBuilder.newClient()
                .target("http://localhost:8080/rest/")
                .path("notes/del/{token}/{id}")
                .resolveTemplate("token", token)
                .resolveTemplate("id", id)
                .request()
                .delete(String.class));

    }
}
