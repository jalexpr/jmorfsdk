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
 * Благодарим Сергея и Екатерину Полицыных за оказание помощи в разработке библиотеки.
 */
package jmorfsdk.load;

import conversionFile.ConversionFile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import static jmorfsdk.load.Load.MYREPOSITORY;

public class FileOpen {

    public static Scanner openScannerFromZipFile(String pathZipFile, String pathFile, String encoding) throws Exception {
        try {
            return new Scanner(openZipFile(pathZipFile, pathFile), encoding);
        } catch (FileNotFoundException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\nПроверте наличие %s, в случае отсуствия скачайте с репозитория %s\r\n", pathFile, MYREPOSITORY);
            Logger.getLogger(LoadFromFileAndBD.class.getName()).log(Level.SEVERE, messages, ex);
            throw new Exception();
        }
    }

    public static ZipInputStream openZipFile(String zipPath, String nameLibrary) throws IOException {
        ZipInputStream zin = new ZipInputStream(new FileInputStream(new File(zipPath)));
        for (ZipEntry e; (e = zin.getNextEntry()) != null;) {
            if (e.getName().equals(nameLibrary)) {
                return zin;
            }
        }
        throw new EOFException("Cannot find " + nameLibrary);
    }

    public static BufferedReader openBufferedReaderStream(String pathFile) {
        return openBufferedReaderStream(pathFile, "UTF-8");
    }
    
    public static BufferedReader openBufferedReaderStream(String pathFile, String encoding) {

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(pathFile), encoding));
        } catch (FileNotFoundException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\nПроверте наличие %s\r\n", pathFile);
            Logger.getLogger(ConversionFile.class.getName()).log(Level.SEVERE, messages, ex);
        } catch (UnsupportedEncodingException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\n1)Проверте кодировку %s в соотвевствии с параметрами в property.xml.\r\n2)При отсутствии property.xml кодировка по умолчанию %s\r\n\r\n",
                    pathFile, encoding);
            Logger.getLogger(ConversionFile.class.getName()).log(Level.SEVERE, messages, ex);
        }

        return bufferedReader;
    }

    public static FileOutputStream openFileInputStream(String pathFile) {

        FileOutputStream fileInputStream = null;
        try {
            fileInputStream = new FileOutputStream(pathFile);
        } catch (FileNotFoundException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\nПроверте наличие %s\r\n", pathFile);
            Logger.getLogger(ConversionFile.class.getName()).log(Level.SEVERE, messages, ex);
        }

        return fileInputStream;
    }

    public static BufferedWriter openBufferedWriterStream(String pathFile, String encoding) {

        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathFile), encoding));
        } catch (FileNotFoundException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\nПроверте наличие %s\r\n", pathFile);
            Logger.getLogger(ConversionFile.class.getName()).log(Level.SEVERE, messages, ex);
        } catch (UnsupportedEncodingException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\n1)Проверте кодировку %s в соотвевствии с параметрами в property.xml.\r\n2)При отсутствии property.xml кодировка по умолчанию %s\r\n\r\n",
                    pathFile, encoding);
            Logger.getLogger(ConversionFile.class.getName()).log(Level.SEVERE, messages, ex);
        }

        return bufferedWriter;
    }

    public static String readLine(BufferedReader bufferedReader) {
        try {
            return bufferedReader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(FileOpen.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    public static void closeFile(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(LoadFromFileAndBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void closeFile(OutputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(LoadFromFileAndBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void closeFile(Scanner scanner) {
        scanner.close();
    }

    public static void closeFile(Reader reader) {
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(FileOpen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void closeFile(Writer writer) {
        try {
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(FileOpen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
