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

public class CertificateScreen extends Routines implements LayoutHelper.OnLayoutStart, EventHandler {
    //declare
    private Label labelTitle, labelSubtitle, labelLocal;
    private TextField fieldLocal;
    private Button buttonSearch, buttonSee;
    private HBox hBoxSearch;
    private TextArea textArea;
    private LayoutHelper layoutHelper;
    private VBox vBoxRoot;
    private Routines routines;
    private Stage stage;
    private boolean canSeeCert = false;
    private String certPath;

    //constructors
    public CertificateScreen() {
        routines = Loader.getRoutines();
    }

    @Override
    public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {

        this.stage = stage;
        layoutHelper = LayoutHelper.getInstance();

        labelTitle = new Label("Ver Conteúdo de Certificado");
        layoutHelper.setTitleFont(labelTitle);
        labelSubtitle = new Label("(Formatos Suportados - JKS e PFX)");
        layoutHelper.setNormalFont(labelSubtitle);
        labelLocal = new Label("Local do Certificado:");
        layoutHelper.setNormalFont(labelLocal);
        fieldLocal = new TextField();
        buttonSearch = new Button("Localizar");
        hBoxSearch = new HBox();
        layoutHelper.setDefaultHbox(hBoxSearch);
        hBoxSearch.getChildren().addAll(labelLocal,fieldLocal,buttonSearch);
        buttonSee = new Button("Examinar");
        textArea = new TextArea();

        vBoxRoot = new VBox();
        layoutHelper.setDefaultVbox(vBoxRoot);
        vBoxRoot.getChildren().addAll(menuBar,labelTitle,labelSubtitle,hBoxSearch,buttonSee,textArea,l1,l2);

        Scene scene = new Scene(vBoxRoot,700,660);
        stage.setScene(scene);
        layoutHelper.loadDefaultStage(stage,image);

        //listenning
        buttonSee.setOnAction(this::handle);
        buttonSearch.setOnAction(this::handle);

    }

    @Override
    public void handle(Event event) {
        if(event.getTarget().equals(buttonSearch)) {
            if((certPath = getFile(stage)) != null) {
                canSeeCert = true;
                fieldLocal.setText(certPath);
            } else {
                canSeeCert = false;
                fieldLocal.setText("");
            }
        } else if(event.getTarget().equals(buttonSee)) {
            String alias = singleTextInput("Alias do Certificado","Informe o Alias do Certificado");
            if(!alias.equalsIgnoreCase("")) {
                String password = singlePasswordInput("Senha do Certificado","Informe a Senha do Certificado");
                if(!password.equalsIgnoreCase("")) {
                    String certContent = OpenClasses.getCertificateContent(certPath,password,alias,CERT_TYPE_JKS);
                    if(certContent != null && !certContent.equalsIgnoreCase(CERT_WRONG_PASS) && !certContent.equalsIgnoreCase(CERT_WRONG_TYPE)) {
                        //display content in text area
                        textArea.setText(certContent);
                        canSeeCert = false;
                        fieldLocal.setText("");
                    } else {
                        //display error dialog to the user
                        if(certContent.equalsIgnoreCase(CERT_WRONG_TYPE)) {
                            //try with other type
                            String otherContent = OpenClasses.getCertificateContent(certPath,password,alias,CERT_TYPE_PFX);
                            if(otherContent != null && !otherContent.equalsIgnoreCase(CERT_WRONG_TYPE) && !otherContent.equalsIgnoreCase(CERT_WRONG_PASS)) {
                                textArea.setText(otherContent);
                                canSeeCert = false;
                                fieldLocal.setText("");
                            } else if(otherContent.equalsIgnoreCase(CERT_WRONG_PASS)) {
                                displayDialogError("Senha Incorreta","Senha Incorreta","A senha informada para a averiguação do conteúdo do certificado é incorreta");
                                canSeeCert = false;
                                fieldLocal.setText("");
                            } else if(otherContent.equalsIgnoreCase(CERT_WRONG_TYPE)) {
                                //internal error
                                displayDialogError("Erro Interno","Erro Interno","Erro interno na leitura do certificado");
                                canSeeCert = false;
                                fieldLocal.setText("");
                            } else {
                                displayDialogError("Erro Interno","Erro Interno","Erro interno na leitura do certificado");
                                canSeeCert = false;
                                fieldLocal.setText("");
                            }

                        } else if(certContent.equalsIgnoreCase(CERT_WRONG_PASS)) {
                            displayDialogError("Senha Incorreta","Senha Incorreta","A senha informada para a averiguação do conteúdo do certificado é incorreta");
                            canSeeCert = false;
                            fieldLocal.setText("");
                        } else {
                            //generic error
                            displayDialogError("Erro Interno","Erro Interno","Erro interno na leitura do certificado");
                            canSeeCert = false;
                            fieldLocal.setText("");
                        }
                    }
                } else {
                    displayDialogError("Senha Não Informada","Senha Não Informada","Para ver o conteúdo de um certificado, é necessário informar a senha");
                    canSeeCert = false;
                    fieldLocal.setText("");
                }
            } else {
                displayDialogError("Alias Inválido","Alias Inválido","Para ver o conteúdo de um certificado, é necessário informar o alias");
                canSeeCert = false;
                fieldLocal.setText("");
            }
        }
    }
}
