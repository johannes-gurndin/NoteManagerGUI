package noteblock;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
public class Topic {
    private String title;

    public Topic() {
    }

    public Topic(String title) {
        this.title = title;
    }

    public static ArrayList<Topic> getAllTopics() {
        return null;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
