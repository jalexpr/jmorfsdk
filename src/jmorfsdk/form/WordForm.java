
package jmorfsdk.form;

import jmorfsdk.grammeme.MorfologyCharacteristics;
import jmorfsdk.grammeme.MorfologyParameters.*;

public class WordForm extends Form{
    
    private final MainForm mainForm;
    
    public WordForm(String strWord, MorfologyCharacteristics morfChar, MainForm mainForm) {
        super(strWord, morfChar);
        this.mainForm = mainForm;
        mainForm.addWordfFormMap(this);
    }
    
    public Post getTypeOfSpeech() {
        return mainForm.getTypeOfSpeech();
    }
}
