
package jmorfsdk.form;

import jmorfsdk.grammeme.forconversion.MorfologyCharacteristicsForConversion;

public class WordForm extends Form{
    
    private final MainForm mainForm;
    
    public WordForm(String strWord, MorfologyCharacteristicsForConversion morfChar, MainForm mainForm) {
        super(strWord, morfChar);
        this.mainForm = mainForm;
        mainForm.addWordfFormMap(this);
    }
    
    public byte getTypeOfSpeech() {
        return mainForm.getTypeOfSpeech();
    }
}
