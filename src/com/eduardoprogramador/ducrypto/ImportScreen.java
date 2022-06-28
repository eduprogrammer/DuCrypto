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

public class ImportScreen extends Routines implements LayoutHelper.OnLayoutStart, EventHandler {
    //declare
    private Label labelCertSrc, labelCertSigned, labelTitle, labelSubtitle, labelAlias, labelPass;
    private TextField fieldCertSrc, fieldCertSigned, fieldAlias;
    private PasswordField fieldPass;
    private Button buttonImport, buttonSrc, buttonSigned;
    private LayoutHelper layoutHelper;
    private Stage stage;
    private String certSrc, certSigned;

    @Override
    public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {
        layoutHelper = LayoutHelper.getInstance();
        this.stage = stage;

        labelTitle = new Label("Importar Certificado");
        layoutHelper.setTitleFont(labelTitle);
        labelSubtitle = new Label("Importar certificado assinado por uma Autoridade Certificadora (CA)");
        layoutHelper.setNormalFont(labelSubtitle);

        labelCertSrc = new Label("Certificado de Origem (.PFX):");
        fieldCertSrc = new TextField();
        buttonSrc = new Button("Abrir");
        labelAlias = new Label("Alias (NOVO):");
        fieldAlias = new TextField();
        labelPass = new Label("Senha:");
        fieldPass = new PasswordField();
        HBox hBoxOne = new HBox();
        hBoxOne.getChildren().addAll(labelCertSrc,fieldCertSrc,buttonSrc,labelAlias,fieldAlias,labelPass,fieldPass);
        layoutHelper.setDefaultHbox(hBoxOne);

        labelCertSigned = new Label("Certificado Assinado (.CER):");
        fieldCertSigned = new TextField();
        buttonSigned = new Button("Abrir");
        HBox hBoxTwo = new HBox();
        hBoxTwo.getChildren().addAll(labelCertSigned,fieldCertSigned,buttonSigned);
        layoutHelper.setDefaultHbox(hBoxTwo);

        buttonImport = new Button("Importar");

        VBox root = new VBox();
        root.getChildren().addAll(menuBar,labelTitle,labelSubtitle,hBoxOne,hBoxTwo,buttonImport,l1,l2);
        layoutHelper.setDefaultVbox(root);

        Scene scene = new Scene(root,700,660);
        stage.setScene(scene);
        layoutHelper.loadDefaultStage(stage,image);

        //listenning
        buttonSrc.setOnAction(this::handle);
        buttonSigned.setOnAction(this::handle);
        buttonImport.setOnAction(this::handle);

    }

    @Override
    public void handle(Event event) {
        if(event.getTarget().equals(buttonSrc)) {
            certSrc = getFile(stage);
            if(certSrc != null)
                fieldCertSrc.setText(certSrc);
        } else if(event.getTarget().equals(buttonSigned)) {
            certSigned = getFile(stage);
            if(certSigned != null)
                fieldCertSigned.setText(certSigned);
        } else if(event.getTarget().equals(buttonImport)) {
            String alias = fieldAlias.getText();
            String pass = fieldPass.getText();
            if(fieldCertSigned.getText().equalsIgnoreCase("") ||
            fieldCertSrc.getText().equalsIgnoreCase("") ||
            alias.equalsIgnoreCase("") || pass.equalsIgnoreCase("")) {
                displayDialogError("Campos Vazios","Campos Vazios","Todos os campos devem ser preenchidos para a obtenção da importação do certificado");
            } else {
                if(CloseClasses.importCert(certSrc,pass,certSigned,alias)) {
                    displayDialogInfo("Certificado Importado","Certificado Importado","Certificado importado com sucesso");
                } else {
                    displayDialogError("Erro de Importação","Erro de Importação","Ocorreu um erro na importação do certificado");
                }
            }
        }
    }
}
