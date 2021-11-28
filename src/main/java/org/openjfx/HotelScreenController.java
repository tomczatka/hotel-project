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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class HotelScreenController implements Initializable {

    Hotel hotel;
    Dialog<Hotel> dialog = new Dialog<>();
    ButtonType submit = new ButtonType("Zatwierdz", ButtonBar.ButtonData.OK_DONE);
    GridPane grid = new GridPane();
    TextField port = new TextField();
    TextField rooms1 = new TextField();
    TextField rooms2 = new TextField();
    TextField rooms3 = new TextField();
    Random random = new Random();

    List<Room> singlerooms = new ArrayList<>();
    List<Room> doublerooms = new ArrayList<>();
    List<Room> triplerooms = new ArrayList<>();
    List<Room> allrooms = new ArrayList<>();
    List<Pair<Integer, PrintWriter>> pwlist = new ArrayList<>();

    @FXML
    public ListView<String> roomslist;
    ObservableList<String> itemslist;

    public HotelScreenController() {
        itemslist = FXCollections.observableArrayList();
    }

    public void addrooms(int rooms1, int rooms2, int rooms3) throws IOException {

        int index = 1;
        for (int i = 0; i < rooms1; i++) {
            Room room = new Room(index, 1, "wolny",10000 + random.nextInt(89999));
            singlerooms.add(room);
            allrooms.add(room);
            index++;
        }
        for (int i = 0; i < rooms2; i++) {
            Room room = new Room(index, 2, "wolny",10000 + random.nextInt(89999));
            doublerooms.add(room);
            allrooms.add(room);
            index++;
        }
        for (int i = 0; i < rooms3; i++) {
            Room room = new Room(index, 3, "wolny",10000 + random.nextInt(89999));
            triplerooms.add(room);
            allrooms.add(room);
            index++;
        }
        addtolistview();
    }

    private void addtolistview() {
        Platform.runLater(() -> {
            itemslist.clear();
            for (Room allroom : allrooms) {
                itemslist.add(allroom.toString());
            }
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dialog.setTitle("Tworzenie hotelu");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().add(submit);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Podaj numer portu:"), 0, 0);
        grid.add(port, 1, 0);
        grid.add(new Label("Liczba pokoi 1-os.:"), 0, 1);
        grid.add(rooms1, 1, 1);
        grid.add(new Label("Liczba pokoi 2-os.:"), 0, 2);
        grid.add(rooms2, 1, 2);
        grid.add(new Label("Liczba pokoi 3-os.:"), 0, 3);
        grid.add(rooms3, 1, 3);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submit) {
                hotel = new Hotel(Integer.parseInt(port.getText()), Integer.parseInt(rooms1.getText()), Integer.parseInt(rooms2.getText()), Integer.parseInt(rooms3.getText()));
                try {
                    ServerSocket hotel = new ServerSocket(Integer.parseInt(port.getText()));
                    Thread hotelthread = new Thread(() -> {
                        while (true) {
                            try {
                                Socket socket = hotel.accept();
                                roomoperations(socket);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    hotelthread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
        if (singlerooms.isEmpty() & doublerooms.isEmpty() & triplerooms.isEmpty()) {
            dialog.showAndWait();
        }

        try {
            addrooms(hotel.getRooms1(), hotel.getRooms2(), hotel.getRooms3());
        } catch (IOException e) {
            e.printStackTrace();
        }
        roomslist.setItems(itemslist);
    }


    @FXML
    public void clickonlist() {
        delateroom(roomslist.getSelectionModel().getSelectedIndex());
    }

    public void delateroom(int id) {
        Room delatedroom = allrooms.get(id);
        switch (delatedroom.getSize()) {
            case 1:
                for (int i = 0; i < singlerooms.size(); i++) {
                    if (singlerooms.get(i).getSize() == delatedroom.getSize()) {
                        singlerooms.remove(i);
                    }
                }
            case 2:
                for (int i = 0; i < doublerooms.size(); i++) {
                    if (doublerooms.get(i).getSize() == delatedroom.getSize()) {
                        doublerooms.remove(i);
                    }
                }
            case 3:
                for (int i = 0; i < triplerooms.size(); i++) {
                    if (triplerooms.get(i).getSize() == delatedroom.getSize()) {
                        triplerooms.remove(i);
                    }
                }
        }
        allrooms.remove(id);
        itemslist.remove(id);
    }

    public void roomoperations(Socket socket) {
        Thread thread = new Thread(() -> {
            try {
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String inputString;
                while ((inputString = br.readLine()) != null) {
                    if (inputString.startsWith("room started")) {
                        String[] tab = inputString.split("\\|");
                        pwlist.add(new Pair<>(Integer.parseInt(tab[1]), pw));
                        pw.println("P" + setPort());
                    }
                    if (inputString.startsWith("rooms")) {
                        String[] rooms = inputString.split("\\|");
                        ArrayList<String> list = order(Integer.parseInt(rooms[1]), Integer.parseInt(rooms[2]), Integer.parseInt(rooms[3]));
                        for (int i = 0; i < list.size(); i++) {
                            pw.println(list.get(i));
                        }
                    }
                    if (inputString.startsWith("endorder")) {
                        String[] rooms = inputString.split("\\|");
                        for (Room singleroom : singlerooms) {
                            for (int i = 1; i <rooms.length; i++) {
                                if (singleroom.getNumber() == Integer.parseInt(rooms[i])) {
                                    singleroom.setStatus("wolny");

                                }
                            }
                        }
                        for (Room doubleroom : doublerooms) {
                            for (int i = 1; i <rooms.length; i++) {
                                if (doubleroom.getNumber() == Integer.parseInt(rooms[i])) {
                                    doubleroom.setStatus("wolny");
                                }
                            }
                        }
                        for (Room tripleroom : triplerooms) {
                            for (int i = 1; i <rooms.length; i++) {
                                if (tripleroom.getNumber() == Integer.parseInt(rooms[i])) {
                                    tripleroom.setStatus("wolny");
                                }
                            }
                        }
                        for (Room allroom : allrooms) {
                            for (int i = 1; i <rooms.length; i++) {
                                if (allroom.getNumber() == Integer.parseInt(rooms[i])) {
                                    allroom.setStatus("wolny");
                                }
                            }
                        }
                        addtolistview();

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        thread.start();
    }

    int min = 1501;

    public synchronized int setPort() {
        return min++;

    }

    public ArrayList<String> order(int rooms1, int rooms2, int rooms3) {
        int a = 0;
        int b = 0;
        int c = 0;
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < singlerooms.size() && a < +rooms1; i++) {
            if (singlerooms.get(i).getStatus().equals("wolny")) {
                singlerooms.get(i).setStatus("Zarezerwowany");
                singlerooms.get(i).setKey(10000 + random.nextInt(89999));
                for (int j = 0; j < allrooms.size(); j++) {
                    if (allrooms.get(j).getNumber() == singlerooms.get(i).getNumber()) {
                        allrooms.get(j).setStatus("Zarezerwowany");
                        allrooms.get(j).setKey(10000 + random.nextInt(89999));
                    }
                }
                list.add("orderedroom" + "|" + singlerooms.get(i).getNumber() + "|" + singlerooms.get(i).getKey() + "|" + singlerooms.get(i).getPort() + "|" + singlerooms.get(i).getSize());
                a++;
                addtolistview();
            }
        }
        for (int i = 0; i < doublerooms.size() && b < +rooms2; i++) {
            if (doublerooms.get(i).getStatus().equals("wolny")) {
                doublerooms.get(i).setStatus("Zarezerwowany");
                doublerooms.get(i).setKey(10000 + random.nextInt(89999));
                for (int j = 0; j < allrooms.size(); j++) {
                    if (allrooms.get(j).getNumber() == doublerooms.get(i).getNumber()) {
                        allrooms.get(j).setStatus("Zarezerwowany");
                        allrooms.get(j).setKey(10000 + random.nextInt(89999));
                    }
                }
                list.add("orderedroom" + "|" + doublerooms.get(i).getNumber() + "|" + doublerooms.get(i).getKey() + "|" + doublerooms.get(i).getPort() + "|" + doublerooms.get(i).getSize());
                b++;
                addtolistview();
            }

        }
        for (int i = 0; i < triplerooms.size() && c < +rooms3; i++) {
            if (triplerooms.get(i).getStatus().equals("wolny")) {
                triplerooms.get(i).setStatus("Zarezerwowany");
                triplerooms.get(i).setKey(10000 + random.nextInt(89999));
                for (int j = 0; j < allrooms.size(); j++) {
                    if (allrooms.get(j).getNumber() == doublerooms.get(i).getNumber()) {
                        allrooms.get(j).setStatus("Zarezerwowany");
                        allrooms.get(j).setKey(10000 + random.nextInt(89999));
                    }
                }
                list.add("orderedroom" + "|" + triplerooms.get(i).getNumber() + "|" + triplerooms.get(i).getKey() + "|" + triplerooms.get(i).getPort() + "|" + doublerooms.get(i).getSize());
                c++;
                addtolistview();
            }
        }
        return list;
    }


}
