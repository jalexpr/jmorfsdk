package jmorfsdk.form;

import java.util.*;
import java.util.logging.*;

/**
 * Omoform - один string, много характеристик
 *
 * @author AlexP
 */
public class OmoForms extends ArrayList<Form>{

    //если нужно вернуть пустую структуру
    public OmoForms(String strWord) {
        this(new Form(strWord));
    }
    
    public OmoForms(Form wf) {
        super();
        add(wf);
    }
     
    //Копирование колекции
    public OmoForms(OmoForms source){
        super(source);
    }
            
    public String getStringOmoform() {
        try {
            return get(0).getStringForm();
        } catch (IndexOutOfBoundsException e) {
            Logger.getLogger("jmorfsdk.Omoform").log(Level.WARNING, "Омоформа не имеет значений");
            return "";
        }
    }

    public boolean addForm(Form wordform) {
        return add(wordform);
    }
    
    @Override
    public int hashCode(){
        return getStringOmoform().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OmoForms other = (OmoForms) obj;
        return Objects.equals(this.getStringOmoform(), other.getStringOmoform());
    }
}
