package controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.ToDoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ToDoListFormController {
    public Label lblUserID;
    public Label lblTitle;
    public AnchorPane root;
    public Pane subRoot;
    public TextField txtDescription;
    public ListView<ToDoTM> lstToDo;
    public Button btnUpdate;
    public Button btnDelete;
    public TextField txtSelectedToDo;


    public void initialize(){
        lblTitle.setText("Hi "+LoginFormController.loginUserName+" Welcome to To-Do List");
        lblUserID.setText(LoginFormController.loginUserID);

        subRoot.setVisible(false);

        loadList();

        setDisableCommon(true);

        lstToDo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observable, ToDoTM oldValue, ToDoTM newValue) {

                if (lstToDo.getSelectionModel().getSelectedItem() == null){
                    return;
                }

            setDisableCommon(false);

            subRoot.setVisible(false);

            txtSelectedToDo.setText(lstToDo.getSelectionModel().getSelectedItem().getDescription());
            }
        });

        }


    public void setDisableCommon(boolean isDisable){
        txtSelectedToDo.setDisable(isDisable);
        btnDelete.setDisable(isDisable);
        btnUpdate.setDisable(isDisable);

        txtSelectedToDo.clear();
    }

    public void btnLogOutOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to Log out...?", ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)){
            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginPageForm.fxml"));
            Scene scene = new Scene(parent);

            Stage primaryStage = (Stage)root.getScene().getWindow();

            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("../image/only hands.png")));
            primaryStage.centerOnScreen();

        }

    }

    public void btnAddNewToDoOnAction(ActionEvent actionEvent) {

        lstToDo.getSelectionModel().clearSelection();

        setDisableCommon(true);

        subRoot.setVisible(true);
        txtDescription.requestFocus();
    }

    public void btnAddToListOnAction(ActionEvent actionEvent) {
        String toDoId = autoGenerateID();
        String description = txtDescription.getText();
        String userId = lblUserID.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into todo values (?,?,?)");

            preparedStatement.setObject(1,toDoId);
            preparedStatement.setObject(2,description);
            preparedStatement.setObject(3,userId);

            preparedStatement.executeUpdate();

            txtDescription.clear();
            subRoot.setVisible(false);


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        loadList();

    }

    public String autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();

        String id ="";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from todo order by id desc limit 1");

            boolean isExist = resultSet.next();

            if (isExist){
                String todoID = resultSet.getString(1);

                todoID = todoID.substring(1,4);

                int intId = Integer.parseInt(todoID);

                intId++;

                if (intId<10){
                    id = "T00" + intId;
                }else if (intId<100){
                    id = "T0" + intId;
                }else if (intId<1000){
                    id = "T" + intId;
                }


            }else {
                id = "T001";
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return id;
    }

    public void loadList(){
        ObservableList<ToDoTM> todos = lstToDo.getItems();
        todos.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todo where user_id =?");
            preparedStatement.setObject(1,lblUserID.getText());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String user_id = resultSet.getString(3);

                todos.add(new ToDoTM(id,description,user_id));
            }
            lstToDo.refresh();



        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    public void OnMouseClick(MouseEvent mouseEvent) {
        lstToDo.getSelectionModel().clearSelection();
        setDisableCommon(true);
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do You want to delete this todo..?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            String id = lstToDo.getSelectionModel().getSelectedItem().getId();

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from todo where id = ?");
                preparedStatement.setObject(1,id);

                preparedStatement.executeUpdate();
                loadList();
                setDisableCommon(true);


            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        String description = txtSelectedToDo.getText();
        String id = lstToDo.getSelectionModel().getSelectedItem().getId();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todo set description = ? where id = ?");
            preparedStatement.setObject(1,description);
            preparedStatement.setObject(2,id);

            preparedStatement.executeUpdate();

            loadList();
            setDisableCommon(true);


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
