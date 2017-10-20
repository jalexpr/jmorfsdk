package jmorfsdk.form;

import java.util.HashMap;
import jmorfsdk.grammeme.forconversion.MorfologyCharacteristicsForConversion;
//import java.util.HashMap;

/**
 * Начальная словоформа слова.
 *
 * @author AlexP
 */
public class MainForm extends Form {

    private final HashMap<Integer, Form> wordFormMap = new HashMap<>();
    private final byte typeOfSpeech;

    //если нужно вернуть пустую структуру
    public MainForm(String strWord){
        this(strWord, (byte) 0, new MorfologyCharacteristicsForConversion());
    }

    public MainForm(String strForm, String typeOfSpeech) {
        this(strForm, Byte.valueOf(typeOfSpeech), new MorfologyCharacteristicsForConversion());
    }

    public MainForm(String strForm, byte typeOfSpeech) {
        this(strForm, typeOfSpeech, new MorfologyCharacteristicsForConversion());
    }

    public MainForm(String strForm, byte typeOfSpeech, MorfologyCharacteristicsForConversion morfChar) {
        super(strForm.toLowerCase(), morfChar);
        this.typeOfSpeech = typeOfSpeech;
    }

    public void addWordfFormMap(Form wordform) {
        wordFormMap.put(wordform.hashCodeByMorfCharact(), wordform);
    }

    public byte getTypeOfSpeech() {
        return typeOfSpeech;
    }

    public HashMap<Integer, Form> getWordFormMap() {
        return wordFormMap;
    }
}
