
package jmorfsdk.form;

import jmorfsdk.old.grammeme.OldMorfologyCharacteristics;
import jmorfsdk.old.grammeme.OldMorfologyParameters.*;

public class WordForm extends Form{
    
    private final MainForm mainForm;
    
    public WordForm(String strWord, OldMorfologyCharacteristics morfChar, MainForm mainForm) {
        super(strWord, morfChar);
        this.mainForm = mainForm;
        mainForm.addWordfFormMap(this);
    }
    
    public Post getTypeOfSpeech() {
        return mainForm.getTypeOfSpeech();
    }
}
