package sample;

import noteblock.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import java.io.IOException;
import java.util.ArrayList;

public class Controller {

    private ArrayList<NoteController> open_notes = new ArrayList<>();

    public void openNote(Note n){
        Tab t = new Tab("NOTE");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("note.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        t.setContent(root);
        NoteController cntrl = loader.getController();
        open_notes.add(cntrl);
        cntrl.
        chatTabs.getTabs().add(t);
        chats.get(id).targetIp.setText(targetIP);
        chats.get(id).chatID.setText(id);
    }



}
