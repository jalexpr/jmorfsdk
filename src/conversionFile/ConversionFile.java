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
 * Thanks to Sergey Politsyn and Katherine Politsyn for their help in the development of the library.
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
 * Если нет, см. <https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode>
 *
 * Благодарим Полицыных Сергея и Екатерину за оказание помощи в разработке библиотеки.
 */
package conversionFile;

import jmorfsdk.BDSqlite;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmorfsdk.load.FileOpen;
import jmorfsdk.load.Property;

public class ConversionFile {

    private static final byte[] CONTROLVALUE = new byte[]{-1, -1, -1, -1};
    private BufferedReader inReader;
    private FileOutputStream outHashCodeAndMorfCharacteristics;
    private BufferedWriter outInitialFormString;
    private HashMap<Integer, IdAndString> stringWordFormAndId;

    public ConversionFile(String inPath, String hashCodeAndMorfCharacteristicsPath, String pathStringInitialForm, String pathStringWordForm) {
        init(inPath, hashCodeAndMorfCharacteristicsPath, pathStringInitialForm, pathStringWordForm);
    }

    private void init(String inPath, String hashCodeAndMorfCharacteristicsPath, String pathStringInitialForm, String pathStringWordForm) {
        inReader = FileOpen.openBufferedReaderStream(inPath, Property.encoding);
        outHashCodeAndMorfCharacteristics = FileOpen.openFileInputStream(hashCodeAndMorfCharacteristicsPath);
        outInitialFormString = FileOpen.openBufferedWriterStream(pathStringInitialForm, Property.encoding);
        stringWordFormAndId = generateMapIdAndString(FileOpen.openBufferedReaderStream(pathStringWordForm, Property.encoding));
    }
    
    private HashMap<Integer, IdAndString> generateMapIdAndString(BufferedReader outReader) {
        HashMap<Integer, IdAndString> mapStringAndId = new HashMap<>();
        try {
            int id = 0;
            while (outReader.ready()) {
                id++;
                IdAndString stringAndID = new IdAndString(outReader.readLine(), id);
                mapStringAndId.put(stringAndID.hashCode(), stringAndID);
            }
        } catch (IOException ex) {
            Logger.getLogger(ConversionFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mapStringAndId;
    }
    
    private void saveInBD(String nameBD) {
        nameBD = nameBD + ".bd";
        BDSqlite outBD = new BDSqlite(nameBD);
        outBD.execute("CREATE TABLE if not exists 'WordForm' ('id' INTEGER NOT NULL, 'StringForm' TEXT NOT NULL, PRIMARY KEY('id'))");
        saveStringAndIdInBD(stringWordFormAndId, outBD);
        outBD.closeDB();
    }

    private void saveStringAndIdInBD(HashMap<Integer, IdAndString> stringWordFormAndId, BDSqlite outDBWordFormString) {
        
        outDBWordFormString.execute("BEGIN TRANSACTION");
        for (Object obj : stringWordFormAndId.values()) {
            IdAndString idAndString = (IdAndString) obj;
            outDBWordFormString.execute(String.format("INSERT INTO 'WordForm' ('id','StringForm') VALUES (%d, '%s'); ", idAndString.myId, idAndString.myString));
        }
        outDBWordFormString.execute("END TRANSACTION");
    }

    public void conversionFile() {
        try {
            //Пропускаем первую строчку, в которой хранится системная информация
            inReader.readLine();
            while (inReader.ready()) {
                saveLemma(inReader.readLine());
            }
            
        } catch (IOException ex) {
            Logger.getLogger(ConversionFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveLemma(String strForms) {
        saveInitialForm(strForms);
        saveWordForms(strForms);
        saveEndLemma();
    }

    private void saveInitialForm(String strForms) {
        String strInitialForm;
        if (strForms.contains("\"")) {
            strInitialForm = strForms.substring(0, strForms.indexOf("\""));
        } else {
            strInitialForm = strForms;
        }
        String[] initialFormParameters = strInitialForm.split(" ");

        try {
            int hashCodeForm = initialFormParameters[0].hashCode();
            outHashCodeAndMorfCharacteristics.write(getBytes(hashCodeForm));
            outHashCodeAndMorfCharacteristics.write(Byte.decode("0x" + initialFormParameters[1]));
            outHashCodeAndMorfCharacteristics.write(getBytes(new BigInteger(initialFormParameters[2], 16).longValue()));

            outInitialFormString.write(initialFormParameters[0]);
            outInitialFormString.newLine();
        } catch (IOException ex) {
            Logger.getLogger(ConversionFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static byte[] getBytes(int value) {
        byte[] bytes = new byte[]{
            (byte) (value >> 24),
            (byte) (value >> 16),
            (byte) (value >> 8),
            (byte) (value)
        };
        return bytes;
    }

    private static byte[] getBytes(long value) {
        byte[] bytes = new byte[]{
            (byte) (value >> 56),
            (byte) (value >> 48),
            (byte) (value >> 40),
            (byte) (value >> 32),
            (byte) (value >> 24),
            (byte) (value >> 16),
            (byte) (value >> 8),
            (byte) (value)
        };
        return bytes;
    }

    private void saveWordForms(String strLemma) {

        String[] arrayWordForms = strLemma.split("\"");

        for (int i = 1; i < arrayWordForms.length; i++) {
            saveForm(arrayWordForms[i]);
        }
    }

    private void saveForm(String strForm) {

        String[] wordlFormParameters = strForm.split(" ");
        int hashCodeForm = 0;
        try {
            hashCodeForm = wordlFormParameters[0].hashCode();
            outHashCodeAndMorfCharacteristics.write(getBytes(hashCodeForm));
            outHashCodeAndMorfCharacteristics.write(getBytes(new BigInteger(wordlFormParameters[1], 16).longValue()));
            outHashCodeAndMorfCharacteristics.write(getBytes(stringWordFormAndId.get(hashCodeForm).myId));
        } catch (IOException ex) {
            Logger.getLogger(ConversionFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ConversionFile.class.getName()).log(Level.SEVERE, strForm, ex);
            System.err.println(stringWordFormAndId.get(hashCodeForm));
        }
    }

    private void saveEndLemma() {
        try {
            outHashCodeAndMorfCharacteristics.write(CONTROLVALUE);
        } catch (IOException ex) {
            Logger.getLogger(ConversionFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void closeFiles() {
        FileOpen.closeFile(inReader);
        FileOpen.closeFile(outHashCodeAndMorfCharacteristics);
        FileOpen.closeFile(outInitialFormString);
    }
    
    private class IdAndString {

        public final String myString;
        public final int myId;

        public IdAndString(String string, int id) {
            myString = string;
            myId = id;
        }

        @Override
        public int hashCode() {
            return myString.hashCode(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final IdAndString other = (IdAndString) obj;
            if (!this.myString.equals(other.myString)) {
                return false;
            }
            return true;
        }
    }
    
    public static void main(String[] args) {
        String inPath = "dictionary.format.number.txt";

        ConversionFile converFile = new ConversionFile(inPath, Property.pathHashAndMorfCharacteristics, Property.pathInitialFormString, Property.pathWordFormString);
        converFile.saveInBD(Property.pathWordFormString);
        converFile.conversionFile();
        converFile.closeFiles();
    }
}
