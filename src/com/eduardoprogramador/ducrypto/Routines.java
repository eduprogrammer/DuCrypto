/*
 * Copyright 2022. Eduardo Programador
 * www.eduardoprogramador.com
 * consultoria@eduardoprogramador.com
 *
 * Todos os direitos reservados
 * */

package com.eduardoprogramador.ducrypto;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Routines {
    //declare
    public static final String CERT_TYPE_JKS = "JKS";
    public static final String CERT_TYPE_PFX = "PKCS12";
    public static final String CERT_WRONG_TYPE = "0";
    public static final String CERT_WRONG_PASS = "-1";
    public static final String HASH_MD5 = "MD5";
    public static final String HASH_SHA_1 = "SHA";
    public static final String HASH_SHA_256 = "SHA-256";
    private String password, res;
    private String comboBoxString;

    //constructors
    public Routines() {

    }

    //methods
    public String getFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Escolher Arquivo");
        File file = fileChooser.showOpenDialog(stage);
        if(file != null)
            return file.getPath();
        else
            return null;
    }

    public String saveFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Arquivo");
        File file = fileChooser.showSaveDialog(stage);
        if(file != null)
            return file.getPath();
        else
            return null;
    }

    public String singleTextInput(String title, String header) {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle(title);
        textInputDialog.setHeaderText(header);
        textInputDialog.showAndWait();
        return textInputDialog.getEditor().getText();
    }

    public String singlePasswordInput(String title, String header) {

        Dialog dialog = new Dialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        Label label = new Label("Senha: ");
        PasswordField passwordField = new PasswordField();
        Button button = new Button("OK");
        HBox hBox = new HBox();
        hBox.getChildren().addAll(label,passwordField,button);
        LayoutHelper layoutHelper = LayoutHelper.getInstance();
        layoutHelper.setDefaultHbox(hBox);

        dialog.getDialogPane().setContent(hBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                password = passwordField.getText();
                dialog.close();

            }
        });

        dialog.showAndWait();
        return password;

    }

    public String singleStringInput(String title, String header, String labelStr, String buttonStr) {

        Dialog dialog = new Dialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        Label label = new Label(labelStr + ": ");
        TextField textField = new TextField();
        Button button = new Button(buttonStr);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(label,textField,button);
        LayoutHelper layoutHelper = LayoutHelper.getInstance();
        layoutHelper.setDefaultHbox(hBox);

        dialog.getDialogPane().setContent(hBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                res = textField.getText();
                dialog.close();

            }
        });

        dialog.showAndWait();
        return res;

    }

    public String fromComboBoxInputs(String title, String header, String labelBox, ComboBox comboBox) {

        Dialog dialog = new Dialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        Label label = new Label(labelBox + ": ");
        Button button = new Button("OK");
        HBox hBox = new HBox();
        comboBox.getSelectionModel().select(0);
        hBox.getChildren().addAll(label,comboBox,button);
        LayoutHelper layoutHelper = LayoutHelper.getInstance();
        layoutHelper.setDefaultHbox(hBox);

        dialog.getDialogPane().setContent(hBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                comboBoxString = comboBox.valueProperty().getValue().toString();
                dialog.close();

            }
        });

        dialog.showAndWait();
        return comboBoxString;

    }

    public String yesOrNoFileSaveDialog(String title, String header, Stage stage) {

        Dialog dialog = new Dialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        Button buttonYes = new Button("Sim");
        Button buttonNo = new Button("Não (Padrão)");
        HBox hBox = new HBox();
        hBox.getChildren().addAll(buttonYes,buttonNo);
        LayoutHelper layoutHelper = LayoutHelper.getInstance();
        layoutHelper.setDefaultHbox(hBox);

        dialog.getDialogPane().setContent(hBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        buttonYes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                res = saveFile(stage);
                dialog.close();
            }
        });

        buttonNo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                res = null;
                dialog.close();
            }
        });

        dialog.showAndWait();
        return res;

    }

    public String yesOrNoFileOpenDialog(String title, String header, Stage stage) {

        Dialog dialog = new Dialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        Button buttonYes = new Button("Sim");
        Button buttonNo = new Button("Não (Padrão)");
        HBox hBox = new HBox();
        hBox.getChildren().addAll(buttonYes,buttonNo);
        LayoutHelper layoutHelper = LayoutHelper.getInstance();
        layoutHelper.setDefaultHbox(hBox);

        dialog.getDialogPane().setContent(hBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        buttonYes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                res = getFile(stage);
                dialog.close();
            }
        });

        buttonNo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                res = null;
                dialog.close();
            }
        });

        dialog.showAndWait();
        return res;

    }

    public void displayDialogError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR,content, ButtonType.OK,ButtonType.CLOSE);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.show();
    }

    public void displayDialogInfo(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION,content, ButtonType.OK,ButtonType.CLOSE);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.show();
    }

    public long getTimeFromString(String time) {

        try {

            String[] times = time.split("/");

            String day = times[0];
            String month = times[1];
            String year = times[2];

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy");
            String formatted = day + " " + month + " " + year;
            Date date = new Date();
            int nowDay = date.getDate();
            int nowMonth = date.getMonth() + 1;
            int nowYear = date.getYear() + 1900;

            String test = Integer.toString(nowDay) + " " + nowMonth + " " + nowYear;

            Date dateAfter = simpleDateFormat.parse(formatted);
            Date dateBefore = simpleDateFormat.parse(test);
            long dif = dateAfter.getTime() - dateBefore.getTime();

            long res = TimeUnit.DAYS.convert(dif,TimeUnit.MILLISECONDS);

            return res;

        } catch (Exception ex) {

            return 0;
        }
    }
}
