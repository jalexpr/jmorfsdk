/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmorfsdk.conversiontocompressedformat;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * добавить замену 1PER на PER1, 2PER на PER2, 3PER на PER3
 * @author U_M0XEF
 */
public class ConversionFormat {

    private final String path;
    private BufferedReader bufRead;
    private BufferedWriter bufWrit;
    
    public ConversionFormat(String path) {
        this.path = path;
    }
    
    public void shortTag(StringBuilder strBuf, String tagSource, String tagShort){
        int lengthTagSource = tagSource.length();
        
        int start;

        while ((start = strBuf.indexOf(tagSource)) > -1) {

            strBuf.replace(start, (start + lengthTagSource), tagShort);
        }
    }
            
    public void shortTag() {

        StringBuilder strBuf;
        
        try {
            bufRead = new BufferedReader(new InputStreamReader(new FileInputStream(path), "Windows-1251"));
            bufWrit = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out_" + path), "Windows-1251"));
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(ConversionFormat.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Не удалось загрузить файл");
            System.err.println("Неверная кодировка");
        }
        
        try { 
            strBuf = new StringBuilder(bufRead.readLine());
            bufWrit.write(strBuf + "\n");
            strBuf = new StringBuilder(bufRead.readLine());
            bufWrit.write(strBuf + "\n");
            strBuf = new StringBuilder(bufRead.readLine());
            bufWrit.write(strBuf + "\n");
        } catch (IOException ex) {
            Logger.getLogger(ConversionFormat.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            while (bufRead.ready()) {
                strBuf = new StringBuilder(bufRead.readLine());
                
                if (!strBuf.equals("lammata")){
                    shortTag(strBuf, "lemma", "");
                    shortTag(strBuf, "<f ", "");
                    shortTag(strBuf, " f>", "");
                    shortTag(strBuf, " ", "");
                    shortTag(strBuf, ">", "");
                    shortTag(strBuf, "<", "");
                    shortTag(strBuf, "/", "");
                    shortTag(strBuf, "id=\"", "");
                    shortTag(strBuf, "\"gv=\"", " ");
                    shortTag(strBuf, "\"ft=\"", "\"");
                    shortTag(strBuf, "-", "_");
                    shortTag(strBuf, "1per", "per1");
                    shortTag(strBuf, "2per", "per2");
                    shortTag(strBuf, "3per", "per3");
                    shortTag(strBuf, "\"f", "");
                    
                    int indexLt = strBuf.indexOf("\"lt=\"");
                    if (indexLt > -1){
                        shortTag(strBuf, strBuf.substring(0, indexLt), "");
                    }

                    shortTag(strBuf, "\"lt=\"", "\"");
                    strBuf.deleteCharAt(0);
                }
                
                bufWrit.write(strBuf + "\n");
                bufWrit.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(ConversionFormat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void conversionUtf8GoAnsi() {

        try {
            bufRead = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
            bufWrit = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out_" + path), "Windows-1251"));
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(ConversionFormat.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Не удалось загрузить файл");
            System.err.println("Неверная кодировка");
        }
        
        try {
            while (bufRead.ready()) {
                String str = bufRead.readLine();
                //System.out.println(str);
                bufWrit.write(str + "\n");

                bufWrit.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(ConversionFormat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        ConversionFormat convFormat = new ConversionFormat("dict.opcorpora.xml");
        //convFormat.conversionUtf8GoAnsi();
        convFormat.shortTag();
    }

}
 