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
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Loader extends LayoutHelper {
    //declare
    private Label l1, l2;
    private static final int MENU_CERTIFICATE = 0;
    private static final int ITEM_SEE_CERTIFICATE = 0;
    private static final int ITEM_CREATE_CERTIFICATE = 1;
    private static final int ITEM_CONVERT_CERTIFICATE = 2;
    private static final int ITEM_CERTIFICATE_SIGNING_REQUEST = 3;
    private static final int ITEM_CERTIFICATE_SIGN_CA = 4;
    private static final int ITEM_CERTIFICATE_IMPORT = 5;
    private static final int MENU_CRYPTO = 1;
    private static final int ITEM_CRYPTO_NEW = 0;
    private static final int ITEM_CRYPTO_UNENCRYPT = 1;
    private static final int ITEM_CRYPTO_CREATE_KEY = 2;
    private static final int MENU_MODULES = 2;
    private static final int ITEM_MODULES_HASH = 0;
    private static final int ITEM_MODULES_MAC = 1;
    private static final int ITEM_MODULES_SIGN = 2;
    private static final int ITEM_MODULES_CHAT = 3;
    private static final int ITEM_MODULES_TRANSFER = 4;
    private static final int ITEM_MODULES_BASE64 = 5;
    private static final int MENU_STEGA = 3;
    private static final int ITEM_STEGA_TO_IMAGE = 0;
    private static final int ITEM_STEGA_FROM_IMAGE = 1;
    private static final int MENU_ABOUT = 4;
    private static final int ITEM_ABOUT_SOFTWARE = 0;
    private static final int ITEM_ABOUT_BUY = 1;
    private static final int ITEM_ABOUT_DEVELOPER = 2;
    private static final int ITEM_ABOUT_SITE = 3;

    private MenuBar menuBar;
    private Stage stage;
    private Image image;

    //constructors
    public Loader() {

    }

    public Loader(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {
        this.menuBar = menuBar;
        this.l1 = l1;
        this.l2 = l2;
        this.stage = stage;
        this.image = image;
        doMenuAction();
    }

    private void doMenuAction() {
        menuBar.getMenus().get(MENU_CERTIFICATE).getItems().get(ITEM_SEE_CERTIFICATE).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //load see certificate screen (see)
                Certificate certificate = new Certificate(menuBar,l1,l2,stage,image);
                certificate.initSee();

            }
        });

        menuBar.getMenus().get(MENU_CERTIFICATE).getItems().get(ITEM_CREATE_CERTIFICATE).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //load the create certificate screen
                Certificate certificate = new Certificate(menuBar,l1,l2,stage,image);
                certificate.initCreate();


            }
        });

        menuBar.getMenus().get(MENU_CERTIFICATE).getItems().get(ITEM_CONVERT_CERTIFICATE).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //load the convert certificate screen
                Certificate certificate = new Certificate(menuBar,l1,l2,stage,image);
                certificate.initConvert();


            }
        });

        menuBar.getMenus().get(MENU_CERTIFICATE).getItems().get(ITEM_CERTIFICATE_SIGNING_REQUEST).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //load the certificate signing request screen
                Certificate certificate = new Certificate(menuBar,l1,l2,stage,image);
                certificate.initRequest();

            }
        });

        menuBar.getMenus().get(MENU_CERTIFICATE).getItems().get(ITEM_CERTIFICATE_SIGN_CA).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //load the certificate sign screen (CA)
                Certificate certificate = new Certificate(menuBar,l1,l2,stage,image);
                certificate.initCA();
            }
        });

        menuBar.getMenus().get(MENU_CERTIFICATE).getItems().get(ITEM_CERTIFICATE_IMPORT).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Certificate certificate = new Certificate(menuBar,l1,l2,stage,image);
                certificate.initImport();
            }
        });

        menuBar.getMenus().get(MENU_CRYPTO).getItems().get(ITEM_CRYPTO_NEW).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Crypto crypto = new Crypto(menuBar,l1,l2,stage,image);
                crypto.initEncrypt();
            }
        });

        menuBar.getMenus().get(MENU_CRYPTO).getItems().get(ITEM_CRYPTO_UNENCRYPT).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Crypto crypto = new Crypto(menuBar,l1,l2,stage,image);
                crypto.initUnencrypt();
            }
        });

        menuBar.getMenus().get(MENU_CRYPTO).getItems().get(ITEM_CRYPTO_CREATE_KEY).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Crypto crypto = new Crypto(menuBar,l1,l2,stage,image);
                crypto.initCreateKey();
            }
        });

        menuBar.getMenus().get(MENU_MODULES).getItems().get(ITEM_MODULES_HASH).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Modules modules = new Modules(menuBar,l1,l2,stage,image);
                modules.initHash();
            }
        });

        menuBar.getMenus().get(MENU_MODULES).getItems().get(ITEM_MODULES_MAC).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Modules modules = new Modules(menuBar,l1,l2,stage,image);
                modules.initMac();
            }
        });

        menuBar.getMenus().get(MENU_MODULES).getItems().get(ITEM_MODULES_SIGN).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Modules modules = new Modules(menuBar,l1,l2,stage,image);
                modules.initSign();
            }
        });

        menuBar.getMenus().get(MENU_MODULES).getItems().get(ITEM_MODULES_CHAT).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Modules modules = new Modules(menuBar,l1,l2,stage,image);
                modules.initChat();
            }
        });

        menuBar.getMenus().get(MENU_MODULES).getItems().get(ITEM_MODULES_TRANSFER).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Modules modules = new Modules(menuBar,l1,l2,stage,image);
                modules.initTransfer();
            }
        });

        menuBar.getMenus().get(MENU_MODULES).getItems().get(ITEM_MODULES_BASE64).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Modules modules = new Modules(menuBar,l1,l2,stage,image);
                modules.initBase64();
            }
        });

        menuBar.getMenus().get(MENU_STEGA).getItems().get(ITEM_STEGA_TO_IMAGE).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stega stega = new Stega(menuBar,l1,l2,stage,image);
                stega.toImage();
            }
        });

        menuBar.getMenus().get(MENU_STEGA).getItems().get(ITEM_STEGA_FROM_IMAGE).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stega stega = new Stega(menuBar,l1,l2,stage,image);
                stega.fromImage();
            }
        });

        menuBar.getMenus().get(MENU_ABOUT).getItems().get(ITEM_ABOUT_SOFTWARE).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Routines routines = new Routines();
                routines.displayDialogInfo("Sobre o DuCrypto","Sobre o DuCrypto","DuCrypto " +
                        "é um poderoso software que implementa diversos algoritmos criptográficos e abrange os " +
                        "três princípios da segurança da informação: Autenticidade, Confidencialidade e Integridade. " +
                        "O aplicativo roda em diversas plataformas como Windows, Linux e MAC OS X e abrange " +
                        "diversas funcionalidades. Na área de autenticação, o usuário pode criar certificado " +
                        "raiz, criar requisições de certificados, importar chaves públicas e privadas, criar e " +
                        "verificar assinaturas digitais, implementar criptografia de chaves públicas, etc. Na " +
                        "confidencialidade, DuCrypto é responsável por prover uma imensa gama de algoritmos, " +
                        "desde criptografia simétrica com palavra-chave até o uso de algoritmos tradicionais " +
                        "como AES, DES e tripla chave DES. No âmbito de chaves assimétricas, DuCrypto suporta a " +
                        "criação de chaves RSA e DSA com diversos tamanhos e possui chat e transferidor de " +
                        "arquivos com o algoritmo Diffie-Hellmann. Na integridade, DuCrypto lança mão de hashes " +
                        "tradicionais como MD5, SHA1, SHA256 e SHA512, ademais de ter suporte a MAC, o qual além " +
                        "de prover a integridade do arquivo, possui o condão de garantir a autenticidade do " +
                        "mesmo.\n\nAutor: Eduardo Programador\nSite: www.eduardoprogramador.com\nContato: " +
                        "consultoria@eduardoprogramador.com");
            }
        });


        menuBar.getMenus().get(MENU_ABOUT).getItems().get(ITEM_ABOUT_DEVELOPER).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Routines routines = new Routines();
                routines.displayDialogInfo("Sobre o Desenvolvedor","Sobre o Desenvolvedor","Ed" +
                        "uardo Programador é desenvolvedor de software e atua em diversas plataformas digitais como " +
                        "Android, Windows, Linux, MAC OS X e Web. Dentre seus projetos finalizados, possui mais de " +
                        "20 (vinte) aplicativos aprovados na PlayStore e dezenas de softwares desenvolvidos para " +
                        "desktop Windows, Linux e MAC OS X, além de outros tantos em desenvolvimento e planejamento. " +
                        "Para acompanhar mais detalhes sobre o desenvolvedor, visite seu sítio eletrônico oficial:" +
                        "\n\nwww.eduardoprogramador.com");
            }
        });

        menuBar.getMenus().get(MENU_ABOUT).getItems().get(ITEM_ABOUT_SITE).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                OpenClasses.openWeb("eduardoprogramador.com");
            }
        });
    }

    public static Routines getRoutines() {
        return new Routines();
    }

}
