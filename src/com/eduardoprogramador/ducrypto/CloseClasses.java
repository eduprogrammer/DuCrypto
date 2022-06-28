/*
 * Copyright 2022. Eduardo Programador
 * www.eduardoprogramador.com
 * consultoria@eduardoprogramador.com
 *
 * Todos os direitos reservados
 * */

package com.eduardoprogramador.ducrypto;

import sun.misc.BASE64Decoder;
import sun.security.pkcs10.PKCS10;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

//build methods
public class CloseClasses extends Routines {
    //declare
    private static SecretKey secretKey;
    private static PrivateKey privateKeyClient, privateKeyServer;
    private static PublicKey publicKeyClient, publicKeyServer;

    //constructors
    private CloseClasses() {

    }

    //methods
    public static boolean createNewCert(String certType, String certAlg, String sigAlg, ArrayList<String> certNames, String pass,String alias, int keySize, long validity, String outPath) {
        try {
            String extension = (certType.equalsIgnoreCase("JKS")) ? ".jks" : ".pfx";
            KeyStore keyStore = KeyStore.getInstance(certType);
            keyStore.load(null,null);
            CertAndKeyGen certAndKeyGen = new CertAndKeyGen(certAlg,sigAlg,null);
            X500Name x500Name = new X500Name(certNames.get(0),certNames.get(1),certNames.get(2),certNames.get(3),certNames.get(4),certNames.get(5));
            certAndKeyGen.generate(keySize);
            X509Certificate[] x509Certificates = new X509Certificate[1];
            x509Certificates[0] = certAndKeyGen.getSelfCertificate(x500Name,new Date(),validity);
            keyStore.setKeyEntry(alias,certAndKeyGen.getPrivateKey(),pass.toCharArray(),x509Certificates);
            FileOutputStream fileOutputStream = new FileOutputStream(outPath + extension);
            keyStore.store(fileOutputStream,pass.toCharArray());
            fileOutputStream.close();
            return true;

        } catch (Exception ex) {
            return false;
        }

    }

    public static boolean convertCert(String certType, String srcPath, String password, String alias, String newType,String outPath) {

        try {
            //old keystore
            KeyStore keyStore = KeyStore.getInstance(certType);
            FileInputStream fileInputStream = new FileInputStream(srcPath);
            keyStore.load(fileInputStream,password.toCharArray());
            X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(alias);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias,new KeyStore.PasswordProtection(password.toCharArray()));
            X509Certificate[] certificates = new X509Certificate[1];
            certificates[0] = x509Certificate;

            //new keystore
            String extension = (newType.equalsIgnoreCase("JKS")) ? ".jks" : ".pfx";
            KeyStore keyStoreNew = KeyStore.getInstance(newType);
            keyStoreNew.load(null,null);
            keyStoreNew.setKeyEntry(alias,privateKeyEntry.getPrivateKey(),password.toCharArray(),certificates);
            FileOutputStream fileOutputStream = new FileOutputStream(outPath + extension);
            keyStoreNew.store(fileOutputStream,password.toCharArray());

            fileInputStream.close();
            fileOutputStream.close();

            return true;


        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean createRequest(String alg, int keySize, String sigAlg, ArrayList<String> certNames, String outPath) {
        try {

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(alg);
            keyPairGenerator.initialize(keySize,new SecureRandom());
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            X500Name x500Name = new X500Name(certNames.get(0),certNames.get(1),certNames.get(2),certNames.get(3),certNames.get(4),certNames.get(5));
            PKCS10 pkcs10 = new PKCS10(publicKey);
            Signature signature = Signature.getInstance(sigAlg);
            signature.initSign(privateKey);
            pkcs10.encodeAndSign(x500Name,signature);
            FileOutputStream fileOutputStream = new FileOutputStream(outPath + ".csr");
            PrintStream printStream = new PrintStream(fileOutputStream);
            pkcs10.print(printStream);
            printStream.close();

            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean createRequest(String certPath, String algBrute, String password, String alias, String sigAlg, String outPath) {
        try {

            String alg = (algBrute.equalsIgnoreCase("JKS")) ? "JKS" : "PKCS12";

            FileInputStream fileCert = new FileInputStream(certPath);
            KeyStore keyStore = KeyStore.getInstance(alg);
            keyStore.load(fileCert,password.toCharArray());
            X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(alias);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias,new KeyStore.PasswordProtection(password.toCharArray()));
            PublicKey publicKeyCsr = x509Certificate.getPublicKey();
            PrivateKey privateKeyCsr = privateKeyEntry.getPrivateKey();
            X509CertImpl x509CertImpl = new X509CertImpl(x509Certificate.getEncoded());
            X509CertInfo x509CertInfo = (X509CertInfo) x509CertImpl.get(X509CertImpl.NAME + "." + X509CertImpl.INFO);
            X500Name x500Name = (X500Name) x509CertInfo.get(X509CertInfo.SUBJECT + "." + CertificateSubjectName.DN_NAME);
            PKCS10 pkcs10 = new PKCS10(publicKeyCsr);
            Signature signature = Signature.getInstance(sigAlg);
            signature.initSign(privateKeyCsr);
            pkcs10.encodeAndSign(x500Name,signature);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(byteArrayOutputStream);
            pkcs10.print(printStream);
            printStream.flush();
            FileOutputStream fileOutputStream = new FileOutputStream(outPath + ".csr");
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.flush();

            fileCert.close();
            fileOutputStream.close();
            return true;



        } catch (Exception ex) {
            return false;
        }
    }

    public static String readRequestContent(String csrFile) {
        try {
            FileInputStream fileInputStream = new FileInputStream(csrFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            fileInputStream.close();
            return stringBuilder.toString();
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean signCertificateByCA(String csrPath, String caPath, String caAlg, String caAlias,String caPassword, String outPath, String bruteTime) {

        try {
            //CSR - public key, x500Name, sigAlg
            FileInputStream fileCsr = new FileInputStream(csrPath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileCsr));
            String line = null;
            ArrayList<String> arrayList = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
                arrayList.add(line);
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i < arrayList.size() - 1; i++) {
                stringBuilder.append(arrayList.get(i));
            }
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] bytesCsr = base64Decoder.decodeBuffer(stringBuilder.toString());
            PKCS10 pkcs10 = new PKCS10(bytesCsr);
            X500Name x500NameCsr = pkcs10.getSubjectName();
            PublicKey publicKeyCsr = pkcs10.getSubjectPublicKeyInfo();

            //CA - certificate, private key, x500Name
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream fileCA = new FileInputStream(caPath);
            keyStore.load(fileCA,caPassword.toCharArray());
            X509Certificate x509CertificateCA = (X509Certificate) keyStore.getCertificate(caAlias);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(caAlias,new KeyStore.PasswordProtection(caPassword.toCharArray()));
            PrivateKey privateKeyCA = privateKeyEntry.getPrivateKey();
            byte[] bytesCA = x509CertificateCA.getEncoded();
            X509CertImpl x509CertImplCA = new X509CertImpl(bytesCA);
            X509CertInfo x509CertInfoCA = (X509CertInfo) x509CertImplCA.get(X509CertImpl.NAME + "." + X509CertImpl.INFO);
            X500Name x500NameCA = (X500Name) x509CertInfoCA.get(X509CertInfo.SUBJECT + "." + CertificateSubjectName.DN_NAME);

            //create ca certificate for csr
            String[] bruteInPieces = bruteTime.split("/");
            Date dateNow = new Date();
            Date dateAfter = new Date();
            dateAfter.setYear(Integer.valueOf(bruteInPieces[2]) - 1900);
            dateAfter.setMonth(Integer.valueOf(bruteInPieces[1]) - 1);
            dateAfter.setDate(Integer.valueOf(bruteInPieces[0]));
            CertificateValidity certificateValidity = new CertificateValidity(dateNow,dateAfter);
            Signature signature = Signature.getInstance(x509CertificateCA.getSigAlgName());
            signature.initSign(privateKeyCA);

            X509CertInfo info = new X509CertInfo();
            info.set(X509CertInfo.VALIDITY,certificateValidity);
            info.set(X509CertInfo.SERIAL_NUMBER,new CertificateSerialNumber(new Random().nextInt() & 0x7fffffff));
            info.set(X509CertInfo.VERSION,new CertificateVersion(CertificateVersion.V3));
            info.set(X509CertInfo.ALGORITHM_ID,new CertificateAlgorithmId(AlgorithmId.get(x509CertificateCA.getSigAlgName())));
            info.set(X509CertInfo.ISSUER,x500NameCA);
            info.set(X509CertInfo.KEY,new CertificateX509Key(publicKeyCsr));
            info.set(X509CertInfo.SUBJECT,x500NameCsr);
            X509CertImpl cert = new X509CertImpl(info);
            cert.sign(privateKeyCA,x509CertificateCA.getSigAlgName());
            //to file
            FileOutputStream fileOutputStream = new FileOutputStream(outPath + ".cer");
            cert.encode(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            //close resources
            fileCsr.close();
            fileCA.close();
            fileOutputStream.close();

            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean importCert(String certSrc, String pass, String certSigned, String newAlias) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream fileSrc = new FileInputStream(certSrc);
            keyStore.load(fileSrc,pass.toCharArray());
            fileSrc.close();

            FileInputStream fileSigned = new FileInputStream(certSigned);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = certificateFactory.generateCertificate(fileSigned);
            fileSigned.close();

            keyStore.setCertificateEntry(newAlias,certificate);
            FileOutputStream fileOutputStream = new FileOutputStream(certSrc); //overwrite source certificate
            keyStore.store(fileOutputStream,pass.toCharArray());
            fileOutputStream.flush();
            fileOutputStream.close();

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean createKey(String alg, int keySize, String outPath) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(alg); //AES, DES, DESede
            keyGenerator.init(keySize,new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            FileOutputStream fileOutputStream = new FileOutputStream(outPath);
            fileOutputStream.write(secretKey.getEncoded());
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean encrypt(String alg, String src, String out, String keyPath) {
        try {
            //get secret key
            FileInputStream fileKey = new FileInputStream(keyPath);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1];
            while (fileKey.read(bytes) > 0) {
                byteArrayOutputStream.write(bytes);
            }
            byteArrayOutputStream.flush();
            SecretKeySpec secretKeySpec = new SecretKeySpec(byteArrayOutputStream.toByteArray(),alg);

            //get original file bytes
            FileInputStream fileSrc = new FileInputStream(src);
            DataInputStream dataSrc = new DataInputStream(fileSrc);
            byte[] bytesOriginal = new byte[(int) new File(src).length()];
            dataSrc.readFully(bytesOriginal);

            //encrypt
            Cipher cipher = Cipher.getInstance(alg);
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
            byte[] bytesEncrypted = cipher.update(bytesOriginal);
            FileOutputStream fileOutputStream = new FileOutputStream(out);
            fileOutputStream.write(bytesEncrypted);
            fileOutputStream.flush();
            fileOutputStream.close();
            dataSrc.close();
            fileSrc.close();
            fileKey.close();

            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean encrypt(String alg, String src, String out, String keyPath, String ivPath) {
        try {
            //get secret key
            FileInputStream fileKey = new FileInputStream(keyPath);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1];
            while (fileKey.read(bytes) > 0) {
                byteArrayOutputStream.write(bytes);
            }
            byteArrayOutputStream.flush();
            SecretKeySpec secretKeySpec = new SecretKeySpec(byteArrayOutputStream.toByteArray(),alg);

            //get original file bytes
            FileInputStream fileSrc = new FileInputStream(src);
            DataInputStream dataSrc = new DataInputStream(fileSrc);
            byte[] bytesOriginal = new byte[(int) new File(src).length()];
            dataSrc.readFully(bytesOriginal);

            //encrypt
            Cipher cipher = Cipher.getInstance(alg + "/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);

            //get and save IV
            byte[] bytesIV = cipher.getIV();
            FileOutputStream fileIV = new FileOutputStream(ivPath);
            fileIV.write(bytesIV);
            fileIV.flush();
            fileIV.close();

            byte[] bytesEncrypted = cipher.update(bytesOriginal);
            FileOutputStream fileOutputStream = new FileOutputStream(out);
            fileOutputStream.write(bytesEncrypted);
            fileOutputStream.flush();
            fileOutputStream.close();
            dataSrc.close();
            fileSrc.close();
            fileKey.close();

            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean decrypt(String alg, String src, String out, String keyPath) {
        try {
            //get secret key
            FileInputStream fileKey = new FileInputStream(keyPath);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1];
            while (fileKey.read(bytes) > 0) {
                byteArrayOutputStream.write(bytes);
            }
            byteArrayOutputStream.flush();
            SecretKeySpec secretKeySpec = new SecretKeySpec(byteArrayOutputStream.toByteArray(),alg);

            //get encrypted file bytes
            FileInputStream fileSrc = new FileInputStream(src);
            DataInputStream dataSrc = new DataInputStream(fileSrc);
            byte[] bytesEncrypted = new byte[(int) new File(src).length()];
            dataSrc.readFully(bytesEncrypted);

            //decrypt
            Cipher cipher = Cipher.getInstance(alg);
            cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
            byte[] bytesOriginal = cipher.update(bytesEncrypted);
            FileOutputStream fileOutputStream = new FileOutputStream(out);
            fileOutputStream.write(bytesOriginal);
            fileOutputStream.flush();
            fileOutputStream.close();
            dataSrc.close();
            fileSrc.close();
            fileKey.close();

            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean decrypt(String alg, String src, String out, String keyPath, String ivPath) {
        try {
            //get secret key
            FileInputStream fileKey = new FileInputStream(keyPath);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1];
            while (fileKey.read(bytes) > 0) {
                byteArrayOutputStream.write(bytes);
            }
            byteArrayOutputStream.flush();
            SecretKeySpec secretKeySpec = new SecretKeySpec(byteArrayOutputStream.toByteArray(),alg);

            //get encrypted file bytes
            FileInputStream fileSrc = new FileInputStream(src);
            DataInputStream dataSrc = new DataInputStream(fileSrc);
            byte[] bytesEncrypted = new byte[(int) new File(src).length()];
            dataSrc.readFully(bytesEncrypted);

            //get IV
            FileInputStream fileIV = new FileInputStream(ivPath);
            DataInputStream dataInputStream = new DataInputStream(fileIV);
            byte[] bytesIV = new byte[(int) new File(ivPath).length()];
            dataInputStream.readFully(bytesIV);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(bytesIV);
            dataInputStream.close();
            fileIV.close();

            //decrypt
            Cipher cipher = Cipher.getInstance(alg + "/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE,secretKeySpec,ivParameterSpec);
            byte[] bytesOriginal = cipher.update(bytesEncrypted);
            FileOutputStream fileOutputStream = new FileOutputStream(out);
            fileOutputStream.write(bytesOriginal);
            fileOutputStream.flush();
            fileOutputStream.close();
            dataSrc.close();
            fileSrc.close();
            fileKey.close();

            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public static String macHash(String macAlg, String cerPath, String src, int mode, String algKey) {
        try {

            byte[] bytesSrc = null;

            switch (mode) {

                case OpenClasses.HASH_STRING:
                    bytesSrc = src.getBytes();
                    break;

                case OpenClasses.HASH_FILE:
                    FileInputStream fileInputStream = new FileInputStream(src);
                    DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                    bytesSrc = new byte[(int) new File(src).length()];
                    dataInputStream.readFully(bytesSrc);
                    dataInputStream.close();
                    fileInputStream.close();
                    break;

                default:
                    break;
            }

            FileInputStream fileCert = new FileInputStream(cerPath);
            DataInputStream dataCert = new DataInputStream(fileCert);
            byte[] bytesCert = new byte[(int)new File(cerPath).length()];
            dataCert.readFully(bytesCert);


            SecretKeySpec secretKeySpec = new SecretKeySpec(bytesCert,algKey);

            Mac mac = Mac.getInstance(macAlg);
            mac.init(secretKeySpec);
            byte[] bytesMac = mac.doFinal(bytesSrc);

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < bytesMac.length; i++) {
                int b = bytesMac[i] & 0xff;
                String hex = Integer.toHexString(b);
                stringBuilder.append(hex);
            }

            return stringBuilder.toString();

        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean makeSign(String sigAlg, String certAlg, String certPath, String pass,String alias, String src, String out) {
        try {
            FileInputStream fileCert = new FileInputStream(certPath);
            KeyStore keyStore = KeyStore.getInstance(certAlg);
            keyStore.load(fileCert,pass.toCharArray());
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias,new KeyStore.PasswordProtection(pass.toCharArray()));

            FileInputStream fileInputStream = new FileInputStream(src);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            byte[] bytesSrc = new byte[(int) new File(src).length()];
            dataInputStream.readFully(bytesSrc);

            Signature signature = Signature.getInstance(sigAlg);
            signature.initSign(privateKeyEntry.getPrivateKey());
            signature.update(bytesSrc);
            byte[] bytesSign = signature.sign();

            FileOutputStream fileOutputStream = new FileOutputStream(out);
            fileOutputStream.write(bytesSign);
            fileOutputStream.flush();

            fileOutputStream.close();
            dataInputStream.close();
            fileInputStream.close();
            fileCert.close();

            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean verifySign(String sigAlg, String certPath, String signPath, String src) {
        try {
            FileInputStream fileInputStream = new FileInputStream(certPath);
            javax.security.cert.X509Certificate x509Certificate = javax.security.cert.X509Certificate.getInstance(fileInputStream);

            FileInputStream fileSrc = new FileInputStream(src);
            DataInputStream dataSrc = new DataInputStream(fileSrc);
            byte[] bytesSrc = new byte[(int) new File(src).length()];
            dataSrc.readFully(bytesSrc);

            FileInputStream fileSign = new FileInputStream(signPath);
            DataInputStream dataSign = new DataInputStream(fileSign);
            byte[] bytesSign = new byte[(int) new File(signPath).length()];
            dataSign.readFully(bytesSign);

            Signature signature = Signature.getInstance(sigAlg);
            signature.initVerify(x509Certificate.getPublicKey());
            signature.update(bytesSrc);
            return signature.verify(bytesSign);

        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean createDH(int mode) {
        try {

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(1024,new SecureRandom());
           KeyPair keyPair = keyPairGenerator.generateKeyPair();

           switch (mode) {
               case NetImplementator.CLIENT_MODE:
                   privateKeyClient = keyPair.getPrivate();
                   publicKeyClient = keyPair.getPublic();

                   break;

                   case NetImplementator.SERVER_MODE:
                       privateKeyServer = keyPair.getPrivate();
                       publicKeyServer = keyPair.getPublic();
                       break;

               default:
                   break;
           }

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean exChangeDH(int mode, byte[] startUp) {
        try {

            if(mode == NetImplementator.SERVER_MODE) {
                KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
                keyAgreement.init(privateKeyServer);
                X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(startUp);
                KeyFactory keyFactory = KeyFactory.getInstance("DH");
                keyAgreement.doPhase(keyFactory.generatePublic(x509EncodedKeySpec),true);
                byte[] bytesSecret = keyAgreement.generateSecret();

                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                byte[] digest = messageDigest.digest(bytesSecret);

                bytesSecret = Arrays.copyOf(digest,128 / Byte.SIZE);

                secretKey = new SecretKeySpec(bytesSecret,"AES");



            } else if(mode == NetImplementator.CLIENT_MODE) {

                KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
                keyAgreement.init(privateKeyClient);
                X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(startUp);
                KeyFactory keyFactory = KeyFactory.getInstance("DH");
                keyAgreement.doPhase(keyFactory.generatePublic(x509EncodedKeySpec),true);
                byte[] bytesSecret = keyAgreement.generateSecret();

                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                byte[] digest = messageDigest.digest(bytesSecret);

                bytesSecret = Arrays.copyOf(digest,128 / Byte.SIZE);

                secretKey = new SecretKeySpec(bytesSecret,"AES");


            }

            return true;



        } catch (Exception ex) {
            return false;
        }
    }

    public static byte[] getServerPublicKey() {
        return publicKeyServer.getEncoded();
    }

    public static byte[] getClientPublicKey() {
        return publicKeyClient.getEncoded();
    }

    public static byte[] toDHEncrypt(byte[] src) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,secretKey);
            return cipher.doFinal(src);
        } catch (Exception ex) {
            return null;
        }
    }

    public static byte[] toDHDecrypt(byte[] src) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,secretKey);
            return cipher.doFinal(src);
        } catch (Exception ex) {
            return null;
        }
    }

    public static byte[] getSecret() {
        return secretKey.getEncoded();
    }

    public static boolean putInImage(String inFile, String outFile, String content, String password) {

        String alg = "PBEWithSHA1AndDESede";
        String hash = "SHA";

        try {
            String src = inFile;
            byte[] bytesImageSrc = OpenClasses.readFile(src);
            String out = outFile;

            byte[] salt = new byte[8];
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            MessageDigest messageDigest = MessageDigest.getInstance(hash);
            messageDigest.update(password.getBytes());
            messageDigest.update(content.getBytes());
            byte[] digest = messageDigest.digest();
            System.arraycopy(digest,0,salt,0,8);

            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt,20);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(alg);
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

            Cipher cipher = Cipher.getInstance(alg);
            cipher.init(Cipher.ENCRYPT_MODE,secretKey,pbeParameterSpec);
            byte[] bytesEnc = cipher.doFinal(content.getBytes());

            FileOutputStream fileOutputStream = new FileOutputStream(out);
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            dataOutputStream.write(bytesImageSrc);
            dataOutputStream.write(salt);
            dataOutputStream.write(bytesEnc);
            dataOutputStream.writeUTF("\n------\n" + bytesImageSrc.length + "," + salt.length + "," + bytesEnc.length);
            dataOutputStream.flush();
            dataOutputStream.close();
            fileOutputStream.close();
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public static String getFromImage(String inFile, String password) {

        String alg = "PBEWithSHA1AndDESede";

        try {
            FileInputStream fileInputStream = new FileInputStream(inFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = null;
            boolean parse = false;

            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                if(parse) {
                    stringBuilder.append(line);
                }

                parse = line.equalsIgnoreCase("------");
            }

            String[] settings = stringBuilder.toString().split(",");

            int imgLen = Integer.valueOf(settings[0]);
            int salten = Integer.valueOf(settings[1]);
            int encLen = Integer.valueOf(settings[2]);

            ByteArrayOutputStream streamSalt = new ByteArrayOutputStream();
            ByteArrayOutputStream streamEnc = new ByteArrayOutputStream();
            byte[] bytes = new byte[1];

            int count = -1;
            FileInputStream fileInputStreamWork = new FileInputStream(inFile);

            while (fileInputStreamWork.read(bytes) > 0) {
                count++;

                if(count < (imgLen)) {
                    //do nothing

                } else if(count >= imgLen && count < (imgLen + salten)) {
                    streamSalt.write(bytes);

                } else if(count >= (imgLen + salten) && count < (imgLen + salten + encLen)) {
                    streamEnc.write(bytes);
                }
            }

            streamSalt.flush();
            streamEnc.flush();

            byte[] salt = streamSalt.toByteArray();
            byte[] enc = streamEnc.toByteArray();

            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(alg);
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt,20);

            Cipher cipher = Cipher.getInstance(alg);
            cipher.init(Cipher.DECRYPT_MODE,secretKey,pbeParameterSpec);
            byte[] dec = cipher.doFinal(enc);

            fileInputStream.close();
            fileInputStreamWork.close();

            return new String(dec,StandardCharsets.UTF_8);

        } catch (Exception ex) {
            return null;
        }
    }



}
