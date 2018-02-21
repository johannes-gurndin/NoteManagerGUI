package sample;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import noteblock.*;

import java.net.URI;

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

    public void save(){
        /*HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/rest))
                .GET()
                .build();
        HttpResponse<Note> response = client.send(request, HttpResponse.BodyHandler.asFile(tempFile));
        System.out.println(response.statusCode());
        System.out.println(response.body());*/
    }

    /*

    DO KEMMEN DI METHODEN INNI DE A NOTE BERTEFFEN



     */

}
