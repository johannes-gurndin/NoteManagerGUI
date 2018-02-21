package sample;


import noteblock.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

public class TestServiceMain {
    public static String login(String username, String password){
        String token = ClientBuilder.newClient()
                .target("http://localhost:8080/rest/")
                .path("user/login/{username}")
                .resolveTemplate("username", username)
                .request()
                .post(Entity.entity(password, MediaType.TEXT_PLAIN), String.class);
        return token;
    }


    public static ArrayList<Note> getNotes(ArrayList<Filter> filter, String token){
        return ClientBuilder.newClient()
                .target("http://localhost:8080/rest/")
                .path("notes/get/{token}")
                .resolveTemplate("token", token)
                .request()
                .post(Entity.xml(new GenericEntity<ArrayList<Filter>>(filter){}),new GenericType<ArrayList<Note>>() {});
    }

    public static ArrayList<Topic> getAllTopics(String token){
        return ClientBuilder.newClient()
                .target("http://localhost:8080/rest/")
                .path("topic/getall/{token}")
                .resolveTemplate("token", token)
                .request()
                .get(new GenericType<ArrayList<Topic>>(){});
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

    public static void main(String[] args) {
        String token = login("joe", "plapla");
        ArrayList<Note> notesOfJoe;
        ArrayList<Filter> filter = new ArrayList<>();
        filter.add(new Filter("creator", "joe"));

        notesOfJoe = getNotes(filter, token);

        for(Note n : notesOfJoe){
            System.out.println(n.toString());
        }


        //System.out.println(addNewNote(new Note("pla", "plaplaplaplaplaplaplaplaplaplaplaplaplaplaplaplaplapla", "joe", "plaplapla"), token));

        for(Topic n : getAllTopics(token)){
            System.out.println(n.toString());
        }
        delete(2, token);
    }
}
