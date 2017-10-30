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
package jmorfsdk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import jmorfsdk.form.MainForm;
import jmorfsdk.form.OmoForms;
import jmorfsdk.form.WordForm;
import jmorfsdk.form.Form;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class JMorfSdk {

    private HashMap<Integer, OmoForms> omoForms = new HashMap();
    private HashMap<Integer, MainForm> mainForms = new HashMap();
    private ArrayList<MainForm> arrMainForms = new ArrayList();
    private boolean isOutputMessagesToConsole = true;
    private static String pathLibrary = "dictionary.format.number.txt";
    private static String encoding = "Windows-1251";
    private final static String MYREPOSITORY  = "https://github.com/jalexpr/JMorfSdk/";

    static {
        loadProperty();
    }

    public JMorfSdk() {
    }

    public JMorfSdk(boolean isOutputMessagesToConsole) {
        this.isOutputMessagesToConsole = isOutputMessagesToConsole;
    }

    public void start() {

        outputMessagesToConsole("Старт загрузки библиотеки");

        BufferedReader buffInput = openStreamFromFile();
        loadFromFile(buffInput);
        closeStream(buffInput);
        outputMessagesToConsole("Библиотека готова к работе.");
    }

    private BufferedReader openStreamFromFile() {

        BufferedReader buffInput = null;
        try {
            buffInput = new BufferedReader(new InputStreamReader(new FileInputStream(pathLibrary), encoding));
        } catch (FileNotFoundException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\nПроверте наличие %s, в случае отсуствия скачайте с репозитория %s\r\n", pathLibrary, MYREPOSITORY);
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, messages);
            System.exit(0);
        } catch (UnsupportedEncodingException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\n1)Проверте кодировку %s в соотвевствии с параметрами в property.xml.\r\n2)При отсутствии property.xml кодировка по умолчанию %s\r\n3)В случае отсуствия файлов, скачайте с репозитория %s\r\n",
                    pathLibrary, encoding, MYREPOSITORY);
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, messages);
        }

        return buffInput;
    }

    private void closeStream(BufferedReader buffInput) {
        try {
            buffInput.close();
        } catch (IOException ex) {
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadFromFile(BufferedReader buffInput) {

        try {
            buffInput.readLine();
            int count = 0;
            while (buffInput.ready() && count < 1) {
                addLemma(buffInput.readLine());
                 count++;
            }
        } catch (IOException ex) {
            String messages = String.format("Проблема чтения с файла: %s\r\nСкачайте файл с репозитория %s", pathLibrary, MYREPOSITORY);
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, messages);
        }
    }

    private void addLemma(String strLemma) {
        MainForm mainForm = createMainForm(strLemma);
        addMainForm(mainForm);
        addWordForms(strLemma, mainForm);
    }

    private void addMainForm(MainForm mf) {
        mainForms.put(mf.hashCode(), mf);
        arrMainForms.add(mf);
    }

    private MainForm createMainForm(String strForms) {
        String mainWordForm;
        if (strForms.contains("\"")) {
            mainWordForm = strForms.substring(0, strForms.indexOf("\""));
        } else {
            mainWordForm = strForms;
        }
        String[] mainWordParameters = mainWordForm.split(" ");
        return new MainForm(mainWordParameters[0], Byte.decode("0x" + mainWordParameters[1]),
                new BigInteger(mainWordParameters[2], 16).longValue());
    }

    private void addWordForms(String strLemma, MainForm mainForm) {

        String[] arrayWordForms = strLemma.split("\"");

        for (int i = 1; i < arrayWordForms.length; i++) {
            WordForm wordForm = createWordForm(arrayWordForms[i], mainForm);
            addFormInOmoForm(wordForm);
        }
    }

    private void addFormInOmoForm(Form form) {
        if (isOmoFormExistForForm(form)) {
            getOmoFormByForm(form).addForm(form);
        } else {
            addOmoForm(new OmoForms(form));
        }
    }

    private boolean isOmoFormExistForForm(Form form) {
        return omoForms.containsKey(form.hashCode());
    }

    private OmoForms getOmoFormByForm(Form form) {
        return omoForms.get(form.hashCode());
    }

    private void addOmoForm(OmoForms of) {
        omoForms.put(of.hashCode(), of);
    }

    private WordForm createWordForm(String strForm, MainForm mainForm) {
        String[] mainWordParam = strForm.split(" ");
        return new WordForm(mainWordParam[0], new BigInteger(mainWordParam[1], 16).longValue(), mainForm);
    }

    public void finish() {
        omoForms.clear();
        mainForms.clear();
        omoForms = null;
        mainForms = null;
    }

    private static void loadProperty() {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse("property.xml");
            Node root = document.getDocumentElement();
            readProperty(root);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            String messages = "Не удается найти property.xml\r\nПрименены параметры по умолчанию!\r\n";
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.WARNING, messages);
        }
    }

    private static void readProperty(Node root) {

        NodeList propertys = root.getChildNodes();
        for (int i = 0; i < propertys.getLength(); i++) {
            Node node = propertys.item(i);
            if (node.getNodeType() != Node.TEXT_NODE) {
                switch (node.getNodeName()) {
                    case "pathLibrary":
                        pathLibrary = node.getChildNodes().item(0).getTextContent();
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

    private void outputMessagesToConsole(String messages) {
        if (isOutputMessagesToConsole) {
            System.out.println(messages);
        }
    }

    public void newSeva() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("dictionary.format.hash+number.txt")));
            for (MainForm mainForm : arrMainForms) {

//                bufferedWriter.write(mainForm.getStringForm());
//                bufferedWriter.write(mainForm.getStringForm().hashCode());
//                bufferedWriter.newLine();
//                bufferedWriter.write(mainForm.getTypeOfSpeech());
//                bufferedWriter.newLine();
                bufferedWriter.write(Long.bitCount(mainForm.getMorfCharacteristic()));

                for (WordForm wordForm : mainForm.getWordFormList()) {
//                    addLine += " " + wordForm.getStringForm()+ " " + Integer.toHexString(wordForm.getStringForm().hashCode()) + " "
//                        + confertMorfCharack(wordForm.getMorfCharacteristic());
//                    count++;
                }
//                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        } catch (Exception ex) {
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String confertMorfCharack(long value) {
        return Long.toBinaryString(value);
    }
}
