package jmorfsdk.form;

import jmorfsdk.grammeme.MorfologyCharacteristics;
import jmorfsdk.grammeme.MorfologyParameters.*;
import java.util.HashMap;

/**
 * Начальная словоформа слова.
 *
 * @author AlexP
 */
public class MainForm extends Form {

    private final HashMap<Integer, Form> wordFormMap = new HashMap<>();
    private final Post typeOfSpeech;
    
    //если нужно вернуть пустую структуру
    public MainForm(String strWord){
        this(strWord, Post.INDETERMINATELY, new MorfologyCharacteristics());
    }
    
    public MainForm(String strForm, Post typeOfSpeech, MorfologyCharacteristics morfChar) {
        super(strForm, morfChar);
        this.typeOfSpeech = typeOfSpeech;
    }

    public void addWordfFormMap(Form wordform) {
        wordFormMap.put(wordform.hashCodeByMorfCharact(), wordform);
    }

    public Post getTypeOfSpeech() {
        return typeOfSpeech;
    }
}
