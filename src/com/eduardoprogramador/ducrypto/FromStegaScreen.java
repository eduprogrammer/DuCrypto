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

public class FromStegaScreen extends Routines implements LayoutHelper.OnLayoutStart, EventHandler {
    //declare
    private Label labelTitle, labelSubtitle, labelSrc, labelPassword;
    private TextField fieldSrc;
    private PasswordField fieldPassword;
    private TextArea textArea;
    private Button buttonSrc, buttonStart;
    private LayoutHelper layoutHelper;
    private Stage stage;
    private String file;

    @Override
    public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {
        layoutHelper = LayoutHelper.getInstance();
        this.stage = stage;

        labelTitle = new Label("Retirar de Imagem");
        labelSubtitle = new Label("Retira conteúdo criptografado de imagens protegido por senha");
        layoutHelper.setTitleFont(labelTitle);
        layoutHelper.setNormalFont(labelSubtitle);

        labelSrc = new Label("Imagem com Segredo:");
        fieldSrc = new TextField();
        buttonSrc = new Button("Carregar");
        HBox hBoxOne = new HBox();
        hBoxOne.getChildren().addAll(labelSrc,fieldSrc,buttonSrc);
        layoutHelper.setDefaultHbox(hBoxOne);

        labelPassword = new Label("Senha:");
        fieldPassword = new PasswordField();
        HBox hBoxTwo = new HBox();
        hBoxTwo.getChildren().addAll(labelPassword,fieldPassword);
        layoutHelper.setDefaultHbox(hBoxTwo);

        textArea = new TextArea();
        buttonStart = new Button("Descriptografar");

        VBox root = new VBox();
        root.getChildren().addAll(menuBar,labelTitle,labelSubtitle,hBoxOne,hBoxTwo,textArea,buttonStart,l1,l2);
        layoutHelper.setDefaultVbox(root);

        Scene scene = new Scene(root,700,660);
        stage.setScene(scene);
        layoutHelper.loadDefaultStage(stage,image);

        //listenning
        buttonSrc.setOnAction(this::handle);
        buttonStart.setOnAction(this::handle);

    }

    @Override
    public void handle(Event event) {
        if(event.getTarget().equals(buttonSrc)) {
            if((file = getFile(stage)) != null)
                fieldSrc.setText(file);
        } else if(event.getTarget().equals(buttonStart)) {
            if(fieldSrc.getText().equalsIgnoreCase("")) {
                displayDialogError("Nenhum Arquivo","Nenhum Arquivo","Você não selecionou nenhuma imagem para prosseguir com a extração de conteúdo criptografado");
            } else {
                String password = fieldPassword.getText();
                if(password.equalsIgnoreCase("")) {
                    displayDialogError("Senha Não Informada","Senha Não Informada","Você precisa fornecer a senha para extração do conteúdo criptografado");
                } else {

                    Runnable runnable = () -> {


                        textArea.appendText("[*] Extraindo conteúdo criptografado de arquivo...\n");
                        String res = CloseClasses.getFromImage(file,password);


                        if(res == null) {
                            textArea.appendText("[*]Erro de Extração. Não foi possível extrair o conteúdo criptografado da imagem selecionada com a senha especificada\n");
                        } else {
                            textArea.setText("------ CONTEÚDO SECRETO (INÍCIO) ------\n" + res + "\n------ CONTEÚDO SECRETO (FIM) ------\n");
                        }
                    };
                    new Thread(runnable).start();
                }
            }
        }
    }
}
