package jmorfsdk;

import jmorfsdk.grammeme.forconversion.MorfologyCharacteristicsForConversion;
import jmorfsdk.form.*;
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
     *
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

            int count = 0;
            while (buffInput.ready() && count < 50000) {
                String line = buffInput.readLine();
                try {
                    addLemmaOldFormat(line);
                } catch (Exception ex) {
                    System.err.println(line);
                    Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
                    // System.exit(0);
                }
                //count++;
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

    private void addLemmaOldFormat(String strLemma) throws Exception {
        //переводим в верхний регистр, чтобы потом enum характеристики определялась по стрингу
        strLemma = strLemma.toUpperCase();

        String[] strForms = strLemma.split("\"");
        String[] mainWordParam = strForms[0].split(" ");

        //первая словоформа всегда в начальной форме добавляем постоянную
        //характеристику - часть речи, остальные меняются в зависимости от части речи
        MainForm mainForm;
        if (mainWordParam.length > 1) {
            mainForm = new MainForm(mainWordParam[0], MorfologyCharacteristicsForConversion.addMorfCharacteristicsPost(mainWordParam[1]));
        } else {
            mainForm = new MainForm(mainWordParam[0], Byte.parseByte("0"));
        }

        addMainForm(mainForm);

        //остальные будут словоформы с их не постоянными характеристиками
        for (int i = 1; i < strForms.length; i++) {
            String[] strParams = strForms[i].split(" ");

            MorfologyCharacteristicsForConversion morfCharact = new MorfologyCharacteristicsForConversion();

            //в первой словоофрме есть не постоянные характеристики
            for (int j = 2; j < mainWordParam.length; j++) {
                morfCharact.addMorfCharacteristics(mainWordParam[j]);
            }

            //добавляем остальные характеристики
            for (int j = 1; j < strParams.length; j++) {
                //на случаей если в документе не верная характеристика
                morfCharact.addMorfCharacteristics(strParams[j]);
            }

            addFormInOmoForm(new WordForm(strParams[0], morfCharact, mainForm));
        }
    }

    public void save() {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("dictionary.txt"), "Windows-1251"));

            for (MainForm mainForm : mainForms.values()) {
                StringBuffer sb = new StringBuffer();
                sb.append(mainForm.getStringForm()).append(" ").append(Long.toHexString(mainForm.getTypeOfSpeech()))
                            .append("\"");
                for (Form form : mainForm.getWordFormMap().values()) {
                    sb.append(form.getStringForm()).append(" ").append(Long.toHexString(form.getValue()))
                            .append("\"");
                }
                bw.write(sb.toString() + "\n");
            }

            bw.flush();

            bw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        JMorfSdk jMorfSdk = new JMorfSdk();
        jMorfSdk.start("dict.opcorpora.xml", "Windows-1251");
        jMorfSdk.save();

        System.out.println("");
    }
}
