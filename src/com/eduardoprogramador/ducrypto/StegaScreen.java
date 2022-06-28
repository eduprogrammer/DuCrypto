/*
 * Copyright 2022. Eduardo Programador
 * www.eduardoprogramador.com
 * consultoria@eduardoprogramador.com
 *
 * Todos os direitos reservados
 * */

package com.eduardoprogramador.ducrypto;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StegaScreen extends Routines implements LayoutHelper.OnLayoutStart, EventHandler {
    //declare
    private Label labelTitle, labelSubtitle, labelSrc, labelOut, labelContent, labelPassword;
    private TextField fieldSrc, fieldOut, fieldContent;
    private PasswordField fieldPassword;
    private Button buttonIn, buttonOut, buttonStart;
    private LayoutHelper layoutHelper;
    private Stage stage;
    private String fileIn, fileOut;

    @Override
    public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {
        layoutHelper = LayoutHelper.getInstance();
        this.stage = stage;

        labelTitle = new Label("Embutir em Imagem");
        labelSubtitle = new Label("Embute em uma imagem textos criptografados protegidos por senha");
        layoutHelper.setTitleFont(labelTitle);
        layoutHelper.setNormalFont(labelSubtitle);

        labelSrc = new Label("Imagem de Origem:");
        fieldSrc = new TextField();
        buttonIn = new Button("Carregar");
        HBox hBoxOne = new HBox();
        hBoxOne.getChildren().addAll(labelSrc,fieldSrc,buttonIn);
        layoutHelper.setDefaultHbox(hBoxOne);

        labelOut = new Label("Imagem com Segredo:");
        fieldOut = new TextField();
        buttonOut = new Button("Salvar");
        HBox hBoxTwo = new HBox();
        hBoxTwo.getChildren().addAll(labelOut,fieldOut,buttonOut);
        layoutHelper.setDefaultHbox(hBoxTwo);

        labelContent = new Label("Conteúdo:");
        fieldContent = new TextField();
        HBox hBoxThree = new HBox();
        hBoxThree.getChildren().addAll(labelContent,fieldContent);
        layoutHelper.setDefaultHbox(hBoxThree);

        labelPassword = new Label("Senha:");
        fieldPassword = new PasswordField();
        HBox hBoxFour = new HBox();
        hBoxFour.getChildren().addAll(labelPassword,fieldPassword);
        layoutHelper.setDefaultHbox(hBoxFour);

        buttonStart = new Button("Gravar");

        VBox root = new VBox();
        root.getChildren().addAll(menuBar,labelTitle,labelSubtitle,hBoxOne,hBoxTwo,hBoxThree,hBoxFour,buttonStart,l1,l2);
        layoutHelper.setDefaultVbox(root);

        Scene scene = new Scene(root,700,660);
        stage.setScene(scene);
        layoutHelper.loadDefaultStage(stage,image);

        //listenning
        buttonIn.setOnAction(this::handle);
        buttonOut.setOnAction(this::handle);
        buttonStart.setOnAction(this::handle);
    }

    @Override
    public void handle(Event event) {
        if(event.getTarget().equals(buttonIn)) {
            if((fileIn = getFile(stage)) != null)
                fieldSrc.setText(fileIn);
        } else if(event.getTarget().equals(buttonOut)) {
            if((fileOut = saveFile(stage)) != null)
                fieldOut.setText(fileOut);
        } else if(event.getTarget().equals(buttonStart)) {

            String content = fieldContent.getText();
            String password = fieldPassword.getText();

            if(fieldSrc.getText().equalsIgnoreCase("") ||
            fieldOut.getText().equalsIgnoreCase("")) {
                displayDialogError("Sem Arquivos","Sem Arquivos","Informe ao menos o arquivo de imagem de destino e o arquivo de imagem com segredo que será criado");
            } else {
                if(content.equalsIgnoreCase("") ||
                password.equalsIgnoreCase("")) {
                    displayDialogError("Campos Vazios","Campos Vazios","O conteúdo a ser embutido na imagem e a senha de proteção não podem estar vazios");
                } else {
                    if(CloseClasses.putInImage(fileIn,fileOut,content,password)) {
                        displayDialogInfo("Conteúdo Embutido","Conteúdo Embutido","O conteúdo que você especificou foi criptografado e embutido com êxito na imagem que foi gerada");
                    } else {
                        displayDialogError("Falha de Escrita","Falha de Escrita","Ocorreu uma falha na tentativa de embutir o conteúdo criptografado na imagem que seria gerada");
                    }
                }
            }
        }
    }
}
