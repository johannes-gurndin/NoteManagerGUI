package noteblock;


import sample.Main;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@XmlRootElement
public class Note {
    private int id;

    private String title;

    private String text;

    private String creatorname;

    private String topic;

    public Note() {
    }

    public Note(String title, String text, String creatorname, String topic) {
        this.title = title;
        this.text = text;
        this.creatorname = creatorname;
        this.topic = topic;
    }

    public Note(int id, String title, String text, String topic, String creatorname) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.creatorname = creatorname;
        this.topic = topic;
    }

    public static ArrayList<Note> getNotes(ArrayList<Filter> filter, String token){
        if(token.equals("offlineToken")){
            return loadOfflineNotes();
        } else
            return ClientBuilder.newClient()
                .target("http://localhost:8080/rest/")
                .path("notes/get/{token}")
                .resolveTemplate("token", token)
                .request()
                .post(Entity.xml(new GenericEntity<ArrayList<Filter>>(filter){}),new GenericType<ArrayList<Note>>() {});
    }

    private static ArrayList<Note> loadOfflineNotes() {
        ArrayList<Note> offlineNotes = new ArrayList<>();
        File f = new File("C:\\Users\\Johannes\\IdeaProjects\\NoteManagerGUI\\notemanagerdocs\\notes.txt");
        if(!f.exists())
            return new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while((line=reader.readLine()) != null){
                //0 = id
                //1 = title
                //2 = text
                //3 = topic
                //4 = creatorname
                String[] note = line.split(":");
                offlineNotes.add(new Note(Integer.parseInt(note[0]),note[1],note[2],note[3],note[4]));
            }
        } catch (IOException e){}
        return offlineNotes;
    }

    public void setSeen(String token){
        if(!token.equals("offlineToken"))
            ClientBuilder.newClient()
                .target("http://localhost:8080/rest/")
                .path("notes/setSeen/{token}/{id}")
                .resolveTemplate("token", Main.authToken)
                .resolveTemplate("id", id)
                .request()
                .get()
                .readEntity(String.class);
    }

    public static ArrayList<Note> getUnseenNotes(String token){
        return ClientBuilder.newClient()
                .target("http://localhost:8080/rest/")
                .path("notes/getUnseen/{token}")
                .resolveTemplate("token", token)
                .request()
                .get(new GenericType<ArrayList<Note>>(){});
    }

    public String getCreatorname() {
        return creatorname;
    }

    public void setCreatorname(String creatorname) {
        this.creatorname = creatorname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "Topic: " + this.topic + "\nTitle: " + this.title + "\nCreator: " + this.creatorname;
    }
}
