/*
 * Copyright (C) 2017  Alexander Porechny alex.porechny@mail.ru
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Attribution-NonCommercial-ShareAlike 3.0 Unported
 * (CC BY-SA 3.0) as published by the Creative Commons.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-SA 3.0)
 * for more details.
 *
 * You should have received a copy of the Attribution-NonCommercial-ShareAlike
 * 3.0 Unported (CC BY-SA 3.0) along with this program.
 * If not, see <https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode>
 *
 *
 * Copyright (C) 2017 Александр Поречный alex.porechny@mail.ru
 *
 * Эта программа свободного ПО: Вы можете распространять и / или изменять ее
 * в соответствии с условиями Attribution-NonCommercial-ShareAlike 3.0 Unported
 * (CC BY-SA 3.0), опубликованными Creative Commons.
 *
 * Эта программа распространяется в надежде, что она будет полезна,
 * но БЕЗ КАКИХ-ЛИБО ГАРАНТИЙ; без подразумеваемой гарантии
 * КОММЕРЧЕСКАЯ ПРИГОДНОСТЬ ИЛИ ПРИГОДНОСТЬ ДЛЯ ОПРЕДЕЛЕННОЙ ЦЕЛИ.
 * См. Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-SA 3.0)
 * для более подробной информации.
 *
 * Вы должны были получить копию Attribution-NonCommercial-ShareAlike 3.0
 * Unported (CC BY-SA 3.0) вместе с этой программой.
 * Если нет, см. <Https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode>
 */
package jmorfsdk.load;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jmorfsdk.JMorfSdk;
import jmorfsdk.form.InitialForm;
import jmorfsdk.form.WordForm;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LoadBasedOnHashCode implements LoadFromFile {

    private int pointer = 0;
    private static String pathHashAndMorfCharacteristics = "dictionary.format.hash+morfCharacteristic.txt";
    private static String pathHashAndInitialFormString = "dictionary.format.hash+initialFormString.txt";
    private static String pathHashAndWordFormString = "dictionary.format.hash+wordFormString.txt";
    private static String encoding = "Windows-1251";
    private static final Integer CONTROLVALUE = 0;//new byte[]{0, 0, 0, 0, 0, 0, 0, 0};

    @Override
    public JMorfSdk loadLibraryForSearchInitialForm() throws Exception {
        JMorfSdk jMorfSdk = new JMorfSdk();
        loadLibraryToFindInitialFormAndMorfCharacteristec(jMorfSdk);
        return jMorfSdk;
    }

    @Override
    public JMorfSdk loadLibraryForSearchForFormByMorphologicalCharacteristics() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JMorfSdk loadFullLibrary() throws Exception {
        return loadLibraryForSearchInitialForm();
    }

    public void loadLibraryToFindInitialFormAndMorfCharacteristec(JMorfSdk jMorfSdk) {
        try {
            FileInputStream inputStreamHashCodeAndMorfCharacteristics = openFileInputStreamFromFile(pathHashAndMorfCharacteristics);
            BufferedReader readerInitialFormHashAndString = openBufferedReaderFromFile(pathHashAndInitialFormString, encoding);
            loadFromFileHashString(jMorfSdk, inputStreamHashCodeAndMorfCharacteristics, readerInitialFormHashAndString);
            closeStream(inputStreamHashCodeAndMorfCharacteristics);
        } catch (Exception ex) {
            Logger.getLogger(LoadBasedOnHashCode.class.getName()).log(Level.SEVERE, "Не удалось загрузить бибилиотек!\n", ex);
        }
    }

    private BufferedReader openBufferedReaderFromFile(String pathLibrary, String encoding) throws Exception {
        try {
            return new BufferedReader(new InputStreamReader(new FileInputStream(pathLibrary), encoding));
        } catch (FileNotFoundException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\nПроверте наличие %s, в случае отсуствия скачайте с репозитория %s\r\n", pathLibrary, MYREPOSITORY);
            Logger.getLogger(LoadBasedOnHashCode.class.getName()).log(Level.SEVERE, messages);
            throw new Exception();
        }
    }

    private void loadFromFileHashString(JMorfSdk jMorfSdk, FileInputStream inputStreamHashCodeAndMorfCharacteristics, BufferedReader readerInitialFormHashAndString) {

        HashMap<Integer, String> initialFormString = loadFormHashAndString(readerInitialFormHashAndString);
        
        byte[] bytesFile;
        try {
            bytesFile = new byte[inputStreamHashCodeAndMorfCharacteristics.available()];

            inputStreamHashCodeAndMorfCharacteristics.read(bytesFile);

            while (pointer < bytesFile.length) {

                int nextHashCode = getHashCodeFromBytes(bytesFile);
                String strWordform = initialFormString.get(nextHashCode);
                byte typeOfSpeech = getTypeOfSpeechFromBytes(bytesFile);
                long morfCharacteristics = getMorfCharacteristicsFromBytes(bytesFile);

                InitialForm initialForm = new InitialForm(strWordform, typeOfSpeech, morfCharacteristics);
                jMorfSdk.addInitialForm(initialForm);
                
                nextHashCode = getHashCodeFromBytes(bytesFile);
                while (nextHashCode != CONTROLVALUE) {
                    morfCharacteristics = getMorfCharacteristicsFromBytes(bytesFile);
                    jMorfSdk.addWordForm(nextHashCode, createWordForm(morfCharacteristics, initialForm));
                    nextHashCode = getHashCodeFromBytes(bytesFile);
                }
                
                pointer += 4;
            }

        } catch (IOException ex) {
            Logger.getLogger(LoadBasedOnHashCode.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            bytesFile = null;
            initialFormString.clear();
            initialFormString = null;
        }
    }
    
    private HashMap<Integer, String> loadFormHashAndString(BufferedReader readerFormHashAndString) {
        HashMap<Integer, String> initialForm = new HashMap<>();
        try {
            while(readerFormHashAndString.ready()) {
                String formString = readerFormHashAndString.readLine();
                initialForm.put(formString.hashCode(), formString);
            }
        } catch (IOException ex) {
            Logger.getLogger(LoadBasedOnHashCode.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return initialForm;
    }

    private FileInputStream openFileInputStreamFromFile(String pathLibrary) throws Exception {
        try {
            return new FileInputStream(pathLibrary);
        } catch (FileNotFoundException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\nПроверте наличие %s, в случае отсуствия скачайте с репозитория %s\r\n", pathLibrary, MYREPOSITORY);
            Logger.getLogger(LoadBasedOnHashCode.class.getName()).log(Level.SEVERE, messages);
            throw new Exception();
        }
    }
    
    private int getHashCodeFromBytes(byte[] bytesFile) {
        return (int) getValueCodeFromBytes(bytesFile, 4);
    }
    
    private long getValueCodeFromBytes(byte[] bytesFile, int countByte) {
        int hashCode = 0;
        for (int i = 0; i < countByte; i++) {
            int f = 0xFF & bytesFile[pointer + countByte - i - 1];
            int g1 = f << (8 * i);
            hashCode |= g1;
        }
        pointer += countByte;
        return hashCode;
    }

    private byte getTypeOfSpeechFromBytes(byte[] bytesFile) {
        byte typeOfSpeech = bytesFile[pointer];
        pointer++;
        return typeOfSpeech;
    }

    private long getMorfCharacteristicsFromBytes(byte[] bytesFile) {
        return getValueCodeFromBytes(bytesFile, 8);
    }

    private WordForm createWordForm(long morfCharacteristics, InitialForm initialForm) {
        return new WordForm(morfCharacteristics, initialForm);
    }

    private void closeStream(FileInputStream inHashCodeAndMorfCharacteristics) {
        try {
            inHashCodeAndMorfCharacteristics.close();
        } catch (IOException ex) {
            Logger.getLogger(LoadBasedOnHashCode.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            inHashCodeAndMorfCharacteristics = null;
        }
    }

    public static void loadProperty() {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse("property.xml");
            Node root = document.getDocumentElement();
            readProperty(root);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            String messages = "Не удается найти property.xml\r\nПрименены параметры по умолчанию!\r\n";
            Logger.getLogger(LoadBasedOnHashCode.class.getName()).log(Level.WARNING, messages);
        }
    }

    public static void readProperty(Node root) {

        NodeList propertys = root.getChildNodes();
        for (int i = 0; i < propertys.getLength(); i++) {
            Node node = propertys.item(i);
            if (node.getNodeType() != Node.TEXT_NODE) {
                switch (node.getNodeName()) {
                    case "pathLibrary":
//                        pathLibrary = node.getChildNodes().item(0).getTextContent();
                        break;
                    case "encoding":
                        encoding = node.getChildNodes().item(0).getTextContent();
                        break;
                    default:
                        readProperty(node);
                }
            }
        }
    }
}
