package jmorfsdk;

import jmorfsdk.old.grammeme.OldMorfologyParameters;
import jmorfsdk.old.grammeme.OldMorfologyCharacteristics;
import jmorfsdk.form.MainForm;
import jmorfsdk.form.Form;
import jmorfsdk.form.WordForm;
import jmorfsdk.form.OmoForms;
import jmorfsdk.old.grammeme.OldMorfologyParameters.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

public class JMorfSdk {

    //По этому мапу находим морфологические характеристики слова
    private final HashMap<Integer, OmoForms> omoForms = new HashMap();
    //По этому мапу находим словоформу с заданными характеристиками
    private final HashMap<Integer, MainForm> mainForms = new HashMap();

    private void addOmoForm(OmoForms of) {
        omoForms.put(of.hashCode(), of);
    }

    public void addMainForm(MainForm mf) {
        mainForms.put(mf.hashCode(), mf);
    }

    /**
     * Проверяем существование омоформы для входной формы, если нашли, то
     * добавляем ее туда, иначе создаем новую
     *
     * @param form
     */
    public void addFormInOmoForm(Form form) {
        if (isOmoFormExistForForm(form)) {
            getOmoFormByForm(form).addForm(form);
        } else {
            addOmoForm(new OmoForms(form));
        }
    }

    /**
     * Проверка существования словоформы
     *
     * @param form
     * @return если найде, то возвращаем true, иначе false
     */
    public boolean isOmoFormExistForForm(Form form) {
        return omoForms.containsKey(form.hashCode());
    }

    /**
     *
     * @param form
     * @return возвращаем омоформу для формы, иначе null
     */
    public OmoForms getOmoFormByForm(Form form) {
        return omoForms.get(form.hashCode());
    }

    /**
     * загрузка библиотеки на вход путь к файлу
     * @param pathLibrary
     * @param encoding
     * @return true - удалось загрузить, иначе false
     *
     */
    public boolean start(String pathLibrary, String encoding) {

        BufferedReader buffInput = null;

        try {
            buffInput = new BufferedReader(new InputStreamReader(new FileInputStream(pathLibrary), encoding));

            //пропускаем первую строчку, там хранится сведения о файле
            buffInput.readLine();

            while (buffInput.ready()) {
                addLemmaOldFormat(buffInput.readLine());
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger("Файл не найден" + JMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger("Ошибка при чтении файла " + JMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                buffInput.close();
            } catch (IOException ex) {
                Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return false;
    }

    private void addLemmaOldFormat(String strLemma) {
        //переводим в верхний регистр, чтобы потом enum характеристики определялась по стрингу
        strLemma = strLemma.toUpperCase();

        String[] strForms = strLemma.split("\"");
        String[] mainWordParam = strForms[0].split(" ");

        //первая словоформа всегда в начальной форме добавляем постоянную 
        //характеристику - часть речи, остальные меняются в зависимости от части речи
        MainForm mainForm = new MainForm(mainWordParam[0], Post.valueOf(mainWordParam[1]));
        
        addMainForm(mainForm);

        //остальные будут словоформы с их не постоянными характеристиками
        for (int i = 1; i < strForms.length; i++) {
            String[] strParams = strForms[i].split(" ");

            OldMorfologyCharacteristics morfCharact = new OldMorfologyCharacteristics();

            //в первой словоофрме есть не постоянные характеристики
            for (int j = 2; j < mainWordParam.length; j++) {
                morfCharact.addMorfCharacteristics(OldMorfologyParameters.getEnum(mainWordParam[j]));
            }

            //добавляем остальные характеристики
            for (int j = 1; j < strParams.length; j++) {
                //на случаей если в документе не верная характеристика
                if (!morfCharact.addMorfCharacteristics(OldMorfologyParameters.getEnum(strParams[j]))) {
                    System.err.println("Ошибка загрузки библиотеки!\nНе верный формат параметра: " + strParams[j]);
                }
            }

            addFormInOmoForm(new WordForm(strParams[0], morfCharact, mainForm));
        }
    }

    public static void main(String[] args) {
        JMorfSdk jMorfSdk = new JMorfSdk();
        jMorfSdk.start("dict.opcorpora.xml", "Windows-1251");

        System.out.println("");
    }
}
