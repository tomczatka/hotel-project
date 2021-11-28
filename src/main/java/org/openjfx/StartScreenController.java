package org.openjfx;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class StartScreenController implements Initializable {


    @FXML
    public void goHotel(ActionEvent actionEvent) throws IOException {
        Parent registerPageParent = FXMLLoader.load(getClass().getResource("HotelScreen.fxml"));
        Scene registerScene = new Scene(registerPageParent);
        Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        appStage.hide();
        appStage.setScene(registerScene);
        appStage.show();
    }

    public void goTerminal(ActionEvent actionEvent) throws IOException {
        Parent registerPageParent = FXMLLoader.load(getClass().getResource("TerminalScreen.fxml"));
        Scene registerScene = new Scene(registerPageParent);
        Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        appStage.hide();
        appStage.setScene(registerScene);
        appStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
