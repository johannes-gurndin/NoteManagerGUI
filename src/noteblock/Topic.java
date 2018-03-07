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
public class Topic {
    private String title;

    public Topic() {
    }

    public static ArrayList<Topic> getTopics(String token){
        return token.equals("offlineToken") ? new ArrayList<>() : ClientBuilder.newClient()
                .target("http://localhost:8080/rest/")
                .path("topic/getall/{token}")
                .resolveTemplate("token", token)
                .request()
                .get(new GenericType<ArrayList<Topic>>(){});
    }

    public Topic(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
