package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CreateNewAccountFormController {
    public PasswordField textConfirmPassword;
    public PasswordField txtNewPassword;
    public Label lblPasswordNotMatch1;
    public Label lblPasswordNotMatch2;
    public TextField txtUserName;
    public TextField txtEmail;
    public Button btnRegister;
    public Label lblUserID;
    public AnchorPane root;
    public Button btnAlreadyHaveAnAction;
    public Label lblAlreadyHaveAnAccount;


    public void initialize(){
        setVisibility(false);

        setDisableCommon(true);

    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {
        String newPassword = txtNewPassword.getText();
        String confirmPassword = textConfirmPassword.getText();

        if(newPassword.equals(confirmPassword)){

            setBorderColor("transparent");
            setVisibility(false);

        }else {

            setBorderColor("red");
            setVisibility(true);

            txtNewPassword.requestFocus();
        }



        register();

    }

    public void setBorderColor(String color){

        txtNewPassword.setStyle("-fx-border-color: "+color);
        textConfirmPassword.setStyle("-fx-border-color: "+color);
    }

    public void setVisibility(boolean isvisible){
        lblPasswordNotMatch1.setVisible(isvisible);
        lblPasswordNotMatch2.setVisible(isvisible);
    }



    public void setDisableCommon(boolean isDisable){
        txtUserName.setDisable(isDisable);
        txtEmail.setDisable(isDisable);
        txtNewPassword.setDisable(isDisable);
        textConfirmPassword.setDisable(isDisable);
        btnRegister.setDisable(isDisable);
    }

    public void btnAddNewUserOnAction(ActionEvent actionEvent) {
        setDisableCommon(false);

        txtUserName.requestFocus();
        autoGenerateID();
    }

    public void autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select uid from user order by uid desc limit 1");

            boolean isExist = resultSet.next();

            if (isExist){
                String userID = resultSet.getString(1);

                userID = userID.substring(1,4);

                int intId = Integer.parseInt(userID);

                intId++;

                if (intId<10){
                    lblUserID.setText("U00" + intId);
                }else if (intId<100){
                    lblUserID.setText("U0"+intId);
                }else if (intId<1000){
                    lblUserID.setText("U"+intId);
                }


            }else {
                lblUserID.setText("U001");
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void register(){


        String id = lblUserID.getText();
        String userName = txtUserName.getText();
        String email = txtEmail.getText();
        String password = textConfirmPassword.getText();

        if (userName.trim().isEmpty()){
            txtUserName.requestFocus();
        }else if (email.trim().isEmpty()){
            txtEmail.requestFocus();
        }else if (txtNewPassword.getText().trim().isEmpty()){
            txtNewPassword.requestFocus();
        }else if (password.trim().isEmpty()){
            textConfirmPassword.requestFocus();
        }else {
            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into user values(?,?,?,?)");
                preparedStatement.setObject(1,id);
                preparedStatement.setObject(2,userName);
                preparedStatement.setObject(3,email);
                preparedStatement.setObject(4,password);

                preparedStatement.executeUpdate();


                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginPageForm.fxml"));
                Scene scene = new Scene(parent);

                Stage primaryStage = (Stage) root.getScene().getWindow();

                primaryStage.setScene(scene);
                primaryStage.setTitle("Login");
                primaryStage.centerOnScreen();


            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }
        }
    }



    public void lblAlreadyHaveAnAccountOnMouseClicked(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginPageForm.fxml"));

        Scene scene = new Scene(parent);

        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Page");
        primaryStage.centerOnScreen();
    }
}
