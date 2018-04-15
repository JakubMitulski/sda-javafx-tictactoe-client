package pl.sda.poznan.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import pl.sda.poznan.viewmodel.ConnectionDialogViewModel;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


//Executing when user takes NewGame
public class MainWindowController {

    private final Logger logger = Logger.getLogger(getClass().getName());


    public void handleClick(MouseEvent mouseEvent) {
        Label source = (Label) mouseEvent.getSource();
        System.out.println(source.getId());
    }

    public void connectToServerAction(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("ConnectionDialogWindow.fxml"));

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Connect to server");
        dialog.setHeaderText("Fill data");

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> optionalType = dialog.showAndWait();
        optionalType.ifPresent(buttonType -> {
            ConnectionDialogController controller = fxmlLoader.getController();
            ConnectionDialogViewModel connectionDetails = controller.getConnectionDetails();
            connectToServer(connectionDetails);
        });

    }

    public void connectToServer(ConnectionDialogViewModel viewModel){
        logger.log(Level.INFO, String.format("Trying to connect server at adress {} with username {}",
                viewModel.getServerAdress(), viewModel.getPlayerName()));

        String[] adress = viewModel.getServerAdress().split(":");
        String host = adress[0];
        int port = Integer.parseInt(adress[1]);
        // todo: add server validation - display error dialog if sth is wrong
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            logger.log(Level.INFO, "Cannot connect to server: " + e.getMessage());

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Something went wrong...");
            alert.setContentText("Could not connect to server");
            alert.showAndWait();
        }
    }
}
