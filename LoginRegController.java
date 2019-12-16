package Kursovaya;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;


public class LoginRegController {
    @FXML
    TextField mylogin;
    @FXML
    TextField mypassword;
    @FXML
    TextField textfield_name;
    @FXML
    TextField textfield_surname;
    @FXML
    TextField textfield_login;
    @FXML
    PasswordField passwordfield_password;
    @FXML
    TextField textfield_location;
    @FXML
    TextField textfield_mail;
    @FXML
    Button reg;
    @FXML
    Button login;

    public void ClickToLogin(ActionEvent actionEvent) {
        if ((mylogin.getText().equals("kamilla")) && mypassword.getText().equals("1234")) {
            Stage FirstStage = (Stage) login.getScene().getWindow();
            FirstStage.close();
            try {
                Stage SecondStage = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("Working.fxml"));
                SecondStage.setScene(new Scene(root));
                SecondStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

        public void ClickToReg (ActionEvent actionEvent) {
            DataBaseHandler dbHandler = new DataBaseHandler();
            dbHandler.signUpUser(textfield_name.getText(), textfield_surname.getText(), textfield_login.getText(),
                    passwordfield_password.getText(), textfield_mail.getText(), textfield_location.getText());
            Stage FirstStage = (Stage) reg.getScene().getWindow();
            FirstStage.close();
            try {
                Stage SecondStage = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource(Working.fxml"));
                SecondStage.setScene(new Scene(root));
                SecondStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
