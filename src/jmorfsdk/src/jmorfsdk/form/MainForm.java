package jmorfsdk.form;

import jmorfsdk.old.grammeme.OldMorfologyCharacteristics;
import jmorfsdk.old.grammeme.OldMorfologyParameters.*;
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
        this(strWord, Post.INDETERMINATELY, new OldMorfologyCharacteristics());
    }
    
    public MainForm(String strForm, Post typeOfSpeech) {
        this(strForm, typeOfSpeech, new OldMorfologyCharacteristics());
    }
    
    public MainForm(String strForm, Post typeOfSpeech, OldMorfologyCharacteristics morfChar) {
        super(strForm.toLowerCase(), morfChar);
        this.typeOfSpeech = typeOfSpeech;
    }

    public void addWordfFormMap(Form wordform) {
        wordFormMap.put(wordform.hashCodeByMorfCharact(), wordform);
    }

    public Post getTypeOfSpeech() {
        return typeOfSpeech;
    }
}
