package noteblock;


import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.xml.bind.annotation.XmlRootElement;
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
        return ClientBuilder.newClient()
                .target("http://localhost:8080/rest/")
                .path("notes/get/{token}")
                .resolveTemplate("token", token)
                .request()
                .post(Entity.xml(new GenericEntity<ArrayList<Filter>>(filter){}),new GenericType<ArrayList<Note>>() {});
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
