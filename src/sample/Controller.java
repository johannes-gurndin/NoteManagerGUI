package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.util.Callback;
import noteblock.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class Controller {

    public ListView<Note> note_list = new ListView<Note>();
    public TabPane tabs;
    public TextField tf_user;
    public TextField tf_pass;
    public Button btn_login;
    public Label login_lable;
    public Label lbl_pass;
    public HashMap<Note, Tab> openNotes = new HashMap<>();
    public HashMap<Tab, Note> openTabs = new HashMap<>();
    private ArrayList<Note> unseen = new ArrayList<>();
    public Button btn_add;

    public void openNote(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            Note currentItemSelected = note_list.getSelectionModel().getSelectedItem();
            if(unseen.contains(currentItemSelected)){
                currentItemSelected.setTitle(currentItemSelected.getTitle().substring(0, currentItemSelected.getTitle().length() - 5));
                currentItemSelected.setSeen(Main.authToken);
                unseen.remove(currentItemSelected);
                note_list.refresh();
            }
            if (openNotes.keySet().contains(currentItemSelected)) {
                tabs.getSelectionModel().select(openNotes.get(currentItemSelected));
            } else {
                Tab t = new Tab(currentItemSelected.getTitle());
                t.closableProperty().setValue(true);
                t.setOnCloseRequest(event -> {
                    Tab tt = (Tab) (event.getSource());
                    openNotes.remove(openTabs.get(tt));
                    openTabs.remove(tt);
                });
                FXMLLoader loader = new FXMLLoader(getClass().getResource("note.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                t.setContent(root);
                NoteController cntrl = loader.getController();
                openNotes.put(currentItemSelected, t);
                openTabs.put(t, currentItemSelected);
                if(!currentItemSelected.getCreatorname().equals(tf_user.getText()) || Main.authToken.equals("offlineToken")) {
                    cntrl.btn_delete.setVisible(false);
                    cntrl.edit_note.setVisible(false);
                }
                cntrl.setNote(currentItemSelected);
                cntrl.setParentController(this);
                for(Topic tp:Topic.getTopics(Main.authToken)) {
                    System.out.println(tp.getTitle());
                    cntrl.cb_topic.getItems().add(tp.getTitle());
                }

                cntrl.cb_topic.setEditable(true);
                cntrl.cb_topic.getSelectionModel().select(currentItemSelected.getTopic());
                tabs.getTabs().add(t);
            }
        }
    }

    public void login(ActionEvent actionEvent) {
        String token;
        ObservableList<Note> items;
        ArrayList<Note> notes;
        try {
             token = ClientBuilder.newClient()
                     .target("http://localhost:8080/rest/")
                     .path("user/login/{username}")
                     .resolveTemplate("username", tf_user.getText())
                     .request()
                     .post(Entity.entity(tf_pass.getText(), MediaType.TEXT_PLAIN), String.class);
         } catch (Exception e){
            token = offlineLogin(tf_user.getText(), tf_pass.getText());
         }

        switch (token){
            case "offlineToken":
                tf_user.setStyle("-fx-text-fill: black");
                tf_pass.visibleProperty().setValue(false);
                tf_user.visibleProperty().setValue(false);
                lbl_pass.visibleProperty().setValue(false);
                btn_login.setText("Logout");
                btn_login.setOnAction(event -> {
                    note_list.getItems().clear();
                    tabs.getTabs().clear();
                    btn_add.setVisible(false);
                    openNotes.clear();
                    openTabs.clear();
                    Main.authToken = "";
                    login_lable.setStyle("-fx-text-fill: black; -fx-font-size: 13;");
                    tf_pass.clear();
                    tf_user.clear();
                    btn_login.setOnAction(this::login);
                    tf_pass.visibleProperty().setValue(true);
                    tf_user.visibleProperty().setValue(true);
                    lbl_pass.visibleProperty().setValue(true);
                    btn_login.setText("Login");
                    login_lable.setText("Username: ");

                });
                login_lable.setText("Logged in as: " + tf_user.getText());
                login_lable.setStyle("-fx-text-fill: green; -fx-font-size: 16;");

                Main.authToken = token;
                Main.created_offline = loadCreatedOffline();


                btn_add.visibleProperty().setValue(true);
                Main.username = tf_user.getText();
                notes = Note.getNotes(new ArrayList<>(), Main.authToken);
                for (Note n : Main.created_offline){
                    notes.add(n);
                }
                items = FXCollections.observableArrayList(notes);
                Main.mainController.note_list.setItems(items);
                break;
            case "false":
                tf_user.setStyle("-fx-text-fill: red");
                break;
            default:
                tf_user.setStyle("-fx-text-fill: black");
                tf_pass.visibleProperty().setValue(false);
                tf_user.visibleProperty().setValue(false);
                lbl_pass.visibleProperty().setValue(false);
                btn_login.setText("Logout");
                btn_login.setOnAction(event -> {
                    note_list.getItems().clear();
                    tabs.getTabs().clear();
                    btn_add.setVisible(false);
                    openNotes.clear();
                    openTabs.clear();
                    String ret = ClientBuilder.newClient()
                            .target("http://localhost:8080/rest/")
                            .path("user/logout/{token}")
                            .resolveTemplate("token", Main.authToken)
                            .request()
                            .get(String.class);
                    Main.authToken = "";
                    login_lable.setStyle("-fx-text-fill: black; -fx-font-size: 13;");
                    tf_pass.clear();
                    tf_user.clear();
                    btn_login.setOnAction(this::login);
                    tf_pass.visibleProperty().setValue(true);
                    tf_user.visibleProperty().setValue(true);
                    lbl_pass.visibleProperty().setValue(true);
                    btn_login.setText("Login");
                    login_lable.setText("Username: ");

                });
                login_lable.setText("Logged in as: " + tf_user.getText());
                login_lable.setStyle("-fx-text-fill: green; -fx-font-size: 16;");
                Main.authToken = token;



                Main.created_offline = loadCreatedOffline();
                transferCreatedOffline();

                saveAllUsersOffline();
                saveAllNotesOffline();

                btn_add.visibleProperty().setValue(true);
                Main.username = tf_user.getText();
                notes = Note.getNotes(new ArrayList<>(), Main.authToken);
                for (Note n : Main.created_offline){
                    notes.add(n);
                }
                items = FXCollections.observableArrayList(notes);

                ArrayList<Integer> unseen_ids = new ArrayList<>();
                unseen = Note.getUnseenNotes(Main.authToken);
                for (Note un : unseen) {
                    unseen_ids.add(un.getId());
                }
                for(Note n:items){
                    if(unseen_ids.contains(n.getId())){
                        n.setTitle(n.getTitle()+" *NEW*");
                        unseen.add(n);
                    }
                }
                Main.mainController.note_list.setItems(items);
        }
    }

    private void transferCreatedOffline() {
        for(Note n : Main.created_offline){
            String ret = ClientBuilder.newClient()
                    .target("http://localhost:8080/rest/")
                    .path("notes/insert/{token}")
                    .resolveTemplate("token", Main.authToken)
                    .request()
                    .post(Entity.xml(n), String.class);
            if(ret.equals("false"))
                System.out.println("Error while transferring notes!");
            else
                System.out.println("Notes transferred");
        }
        deleteTransferred();
    }

    private void deleteTransferred() {
        Main.created_offline.clear();
        File f = new File(System.getProperty("user.dir") + "\\notemanagerdocs\\offlineNotes.txt");
        f.delete();
    }

    private ArrayList<Note> loadCreatedOffline() {
        return Note.loadOfflineNotes("\\notemanagerdocs\\offlineNotes.txt");
    }

    public void addNewNote(ActionEvent actionEvent) {
        Tab t = new Tab("NOTE");
        t.closableProperty().setValue(true);
        t.setOnCloseRequest(event -> {
            Tab tt = (Tab) (event.getSource());
            openNotes.remove(openTabs.get(tt));
            openTabs.remove(tt);
        });
        FXMLLoader loader = new FXMLLoader(getClass().getResource("note.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        t.setContent(root);
        NoteController cntrl = loader.getController();
        Note n = new Note();
        n.setCreatorname(Main.username);
        cntrl.setNote(n);
        for(Topic tp:Topic.getTopics(Main.authToken)) {
            System.out.println(tp.getTitle());
            cntrl.cb_topic.getItems().add(tp.getTitle());
        }
        cntrl.cb_topic.setPromptText("Topic");
        cntrl.cb_topic.setEditable(true);
        cntrl.btn_delete.visibleProperty().setValue(false);
        cntrl.edit_note.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("NEW");
                if(cntrl.cb_topic.getSelectionModel().getSelectedItem() == null || cntrl.cb_topic.getSelectionModel().getSelectedItem().equals("")) {
                    cntrl.cb_topic.requestFocus();
                }else {
                    cntrl.note.setText(cntrl.note_content.getText());
                    cntrl.note.setTitle(cntrl.note_title.getText());
                    cntrl.note.setTopic(cntrl.cb_topic.getSelectionModel().getSelectedItem());
                    if(Main.authToken.equals("offlineToken")){
                        Main.created_offline.add(cntrl.note);
                    } else {
                        cntrl.note.setId(Integer.parseInt(ClientBuilder.newClient()
                                .target("http://localhost:8080/rest/")
                                .path("notes/insert/{token}")
                                .resolveTemplate("token", Main.authToken)
                                .request()
                                .post(Entity.xml(cntrl.note), String.class)));
                        cntrl.btn_delete.visibleProperty().setValue(true);
                    }
                    System.out.println("ID: " + cntrl.note.getId());
                    ObservableList<Note> items = FXCollections.observableArrayList(Note.getNotes(new ArrayList<Filter>(), Main.authToken));
                    for(Note n : Main.created_offline){
                        items.add(n);
                    }
                    Main.mainController.note_list.setItems(items);
                    if(!Main.authToken.equals("offlineToken")) {
                        cntrl.edit_note.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                cntrl.update(event);
                            }
                        });
                    } else {
                        cntrl.edit_note.visibleProperty().setValue(false);
                    }
                }
            }
        });
        tabs.getTabs().add(t);

    }

    private void saveAllNotesOffline(){
        ArrayList<Note> notes = Note.getNotes(new ArrayList<Filter>(), Main.authToken);
        File f = new File(System.getProperty("user.dir") + "\\notemanagerdocs\\notes.txt");
        Main.ensureFileExists(f);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
            for(Note n : notes){
                writer.write(n.toStringSave());
            }
        } catch (IOException e){}
    }


    private void saveAllUsersOffline(){
        ArrayList<Filter> filter = ClientBuilder.newClient()
                .target("http://localhost:8080/rest/")
                .path("user/getall/{token}")
                .resolveTemplate("token", Main.authToken)
                .request()
                .get(new GenericType<ArrayList<Filter>>() {});
        File f = new File(System.getProperty("user.dir") + "\\notemanagerdocs\\users.txt");
        Main.ensureFileExists(f);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
            for (Filter user:
                 filter) {
                writer.write(user.getKey() + ":" + user.getValue() + "\n");
            }
        } catch (IOException e){

        }

    }

    private String offlineLogin(String username, String pass) {
        String ret = "false";
        ArrayList<Filter> users = new ArrayList<>();
        File f = new File(System.getProperty("user.dir") + "\\notemanagerdocs\\users.txt");
        if(!f.exists())
            return ret;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while((line=reader.readLine()) != null){
                String [] userpass = line.split(":");
                users.add(new Filter(userpass[0], userpass[1]));
            }
        } catch (IOException e){}

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            String hash = hash256(pass);

            for (Filter user:
                 users) {
                if(user.getValue().equals(hash)){
                    ret = "offlineToken";
                }
            }

        } catch (NoSuchAlgorithmException e) {
        }

        return ret;
    }
    public static String hash256(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }
    public static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
}
