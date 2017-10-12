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
        add(wf);
    }
    
    //копирование офомормы
    public OmoForms(OmoForms omoform){
        
    }
    
    public String getStringOmoform() {
        try {
            return get(0).getStringForm();
        } catch (IndexOutOfBoundsException e) {
            Logger.getLogger("jmorfsdk.Omoform").log(Level.WARNING, "Омоформа не имеет значений");
            return "";
        }
    }

    public boolean AddWordform(Form wordform) {
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
        if (!Objects.equals(this.getStringOmoform(), other.getStringOmoform())) {
            return false;
        }
        return true;
    }
    
    @Override
    public Iterator iterator() {
        return new Iterator() {

            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < OmoForms.this.size();
            }

            @Override
            public Form next() {
                Form result = OmoForms.this.get(current);
                if (!hasNext()) {
                    throw new IndexOutOfBoundsException("End of list.");
                }
                current++;
                return result;
            }
        };
    }
}
