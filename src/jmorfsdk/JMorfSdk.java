package jmorfsdk;

import jmorfsdk.grammeme.MorfologyParameters;
import jmorfsdk.grammeme.MorfologyCharacteristics;
import jmorfsdk.form.OmoForms;
import jmorfsdk.form.MainForm;
import jmorfsdk.form.WordForm;
import jmorfsdk.form.Form;
import jmorfsdk.grammeme.MorfologyParameters.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

public class JMorfSdk {
    
    //По этому мапу находим морфологические характеристики слова
    private final HashMap<Integer, OmoForms> omoforms = new HashMap();
    //По этому мапу находим словоформу с заданными характеристиками
    private final HashMap<Integer, MainForm> mainforms = new HashMap();
        
    private void addOmoform(OmoForms of) {
        omoforms.put(of.hashCode(), of);
    }
    
    public void addMainForm(MainForm mf) {
        mainforms.put(mf.hashCode(), mf);
        addOmoform(new OmoForms(mf));
    }
    
    /**
     * Ищем ArrayList омоформы для входной словоформы и добавляем ее туда или создаем новую
     * @param wf 
     */
    public void addWordForm(Form wf) {
        if (omoforms.containsKey(wf.hashCode())) {
            omoforms.get(wf.hashCode()).AddWordform(wf);
        } else {
            addOmoform(new OmoForms(wf));
        }
    }

    //Если слово было найдено возвращаем омоформы этого слова, если нет, то пустую структуру, не реализовано до конца
    public OmoForms getNormalWord(String strWord) {
        if (omoforms.containsKey(strWord.hashCode())) {
            return new OmoForms(omoforms.get(strWord.hashCode()));
        } else {
            /*
                добавить вывод неизвестного слова в какой нибудь файл
             */
            return new OmoForms(strWord);
        }
    }
    
    /**
     * загрузка библиотеки на вход путь к файлу
     **/
    public boolean start(String path1) {
        
        BufferedReader buffInput = null;
        try {            
            buffInput = new BufferedReader(new InputStreamReader(new FileInputStream(path1), "Windows-1251"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger("Файл не найден" + JMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //считываем по строчно, так быстрее
        try {
            //пропускаем первую строчку
            buffInput.readLine();
            
            while (buffInput.ready()) {
                
                //переводим в верхний регистр, чтобы потом enum характеристики определялась по стрингу
                String strLine = buffInput.readLine().toUpperCase();
                
                //рабиваем на словоформы
                String strForm[] = strLine.split("\"");

                //разбиваем на стринг слова и его характеристики
                String[] strMainParam = strForm[0].split(" ");

                //первая словоформа всегда в начальной форме добавляем постоянную 
                //характеристику - часть речи, остальные меняются в зависимости от части речи
                MainForm mainForm = new MainForm(strMainParam[0].toLowerCase(),
                        Post.valueOf(strMainParam[1]),
                        new MorfologyCharacteristics());

                addMainForm(mainForm);

                //остальные будут словоформы с их не постоянными характеристиками
                for (int i = 1; i < strForm.length; i++) {
                    String[] strParams = strForm[i].split(" ");

                    MorfologyCharacteristics morfCharact = new MorfologyCharacteristics();

                    //в первой словоофрме есть не постоянные характеристики
                    for (int j = 2; j < strMainParam.length; j++) {
                        morfCharact.addMorfCharacteristics(MorfologyParameters.getEnum(strMainParam[j]));
                    }

                    //добавляем остальные характеристики
                    for (int j = 1; j < strParams.length; j++) {
                        //на случаей если в документе не верная характеристика
                        if(!morfCharact.addMorfCharacteristics(MorfologyParameters.getEnum(strParams[j]))){
                            System.err.println("Ошибка загрузки библиотеки!\nНе верный формат параметра: " + strParams[j]);
                        }
                    }

                    addWordForm(new WordForm(strParams[0], morfCharact, mainForm));
                }
                
            }
        } catch (IOException ex) {
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }

    public static void main(String[] args) {
        JMorfSdk jMorfSdk = new JMorfSdk();
        jMorfSdk.start("dict.opcorpora.xml");
        
        System.out.println("");
    }
}
