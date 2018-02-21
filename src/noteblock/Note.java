package noteblock;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Note {
    private int id;
    private String title;
    private String text;
    private String creatorname;
    private String topic;

    public Note() {
    }

    public Note(int id, String title, String text, String creatorname, String topic) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.creatorname = creatorname;
        this.topic = topic;
    }


    public int save() {
        return 0;
    }

    public static boolean delete(int id) {
        return false;
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
}
