package org.openjfx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class TerminalScreenController implements Initializable {
    Dialog<Room> dialog = new Dialog<>();
    Dialog<Room> dialog2 = new Dialog<>();
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    ButtonType submit = new ButtonType("Zatwierdz", ButtonBar.ButtonData.OK_DONE);
    ButtonType exit = new ButtonType("Wyjdz z pokoju", ButtonBar.ButtonData.OK_DONE);
    GridPane grid = new GridPane();
    TextField rooms1 = new TextField();
    TextField rooms2 = new TextField();
    TextField rooms3 = new TextField();
    PrintWriter pw;
    ArrayList<Integer> roomsordered = new ArrayList<>();
    ArrayList<Pair<Integer,Integer>> pairlist = new ArrayList<>();
    ArrayList<Pair<Integer,PrintWriter>> pw1list = new ArrayList<>();
    @FXML
    public ListView<String> orderedroomslist;
    ObservableList<String> itemslist = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dialog2.setTitle("Pokoj");
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Bledne dane do pokoju");
        dialog2.setHeaderText(null);
        dialog2.getDialogPane().getButtonTypes().add(exit);
        dialog.setTitle("Rezerwacja pokoji");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().add(submit);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Liczba pokoi 1-os.:"), 0, 0);
        grid.add(rooms1, 1, 0);
        grid.add(new Label("Liczba pokoi 2-os.:"), 0, 1);
        grid.add(rooms2, 1, 1);
        grid.add(new Label("Liczba pokoi 3-os.:"), 0, 2);
        grid.add(rooms3, 1, 2);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submit) {
                int singlerooms = Integer.parseInt(rooms1.getText());
                int doublerooms = Integer.parseInt(rooms2.getText());
                int triplerooms = Integer.parseInt(rooms3.getText());
                Thread clientthread = new Thread(()->{
                    try {
                        Socket socket = new Socket("localhost",1500);
                        pw = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String inputString;
                        pw.println("rooms"+"|"+singlerooms+"|"+doublerooms+"|"+triplerooms);
                        while((inputString = br.readLine()) != null){
                            if(inputString.startsWith("orderedroom")){
                                String[] rooms = inputString.split("\\|");
                                itemslist.add("Pokoj "+rooms[4]+"-osobowy " + "nr."+rooms[1]);
                                roomsordered.add(Integer.parseInt(rooms[1]));
                                connecttorooms(Integer.parseInt(rooms[3]),Integer.parseInt(rooms[1]));
                                pairlist.add(new Pair<>(Integer.parseInt(rooms[1]),Integer.parseInt(rooms[2])));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
                clientthread.start();
            }
            return null;
        });
        orderedroomslist.setItems(itemslist);
    }
    @FXML
    public void orderrooms(){
        dialog.showAndWait();

    }
    @FXML
    public void end(){
        StringBuilder string= new StringBuilder("endorder");
        for (Integer integer : roomsordered) {
            string.append("|").append(integer);
        }
        pw.println(string);
        itemslist.clear();
        roomsordered.clear();
    }
    @FXML
    public void goin(){
        String[] id = itemslist.get(orderedroomslist.getSelectionModel().getSelectedIndex()).split("\\.");
        dialog2.setContentText("Jestes w pokoju numer"+id[1]);
        for (int i = 0; i <pairlist.size() ; i++) {
            if (pairlist.get(i).getKey()==Integer.parseInt(id[1])) {
                for (int j = 0; j < pw1list.size(); j++) {
                    if (pw1list.get(j).getKey() == Integer.parseInt(id[1])) {
                        pw1list.get(j).getValue().println("wejscie" + "|" + id[1]+ "|" + pairlist.get(i).getValue() );
                    }
                }
            }
    }
        dialog2.setResultConverter(dialogButton -> {
            if (dialogButton == exit) {
                for (int j = 0; j < pw1list.size(); j++) {
                    if (pw1list.get(j).getKey() == Integer.parseInt(id[1])) {
                        pw1list.get(j).getValue().println("wyjscie");
                    }
                }

            }
            return null;
            });
    }

    public void connecttorooms(int port,int number) {
        Thread thread2 = new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", port);
                PrintWriter pw1 = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader br1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String inputStream;
                pw1list.add(new Pair<>(number,pw1));
                while((inputStream = br1.readLine()) != null){
                    if(inputStream.equals("dane poprawne")){
                        Platform.runLater(()->{dialog2.showAndWait();});

                    }
                    if(inputStream.equals("bledne dane")){
                        Platform.runLater(()->{alert.showAndWait();});

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread2.start();

    }
}
