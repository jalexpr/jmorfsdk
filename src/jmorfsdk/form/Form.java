package jmorfsdk.form;

import jmorfsdk.grammeme.forconversion.MorfologyCharacteristicsForConversion;
import java.util.Objects;

/**
 * Словоформа - конкретного словоформа
 *
 * @author AlexP
 */
public class Form {

    private final String strWordform;
    private final MorfologyCharacteristicsForConversion morfCharacteristics;
    private static long sizeForm = 0;

    //если нужно вернуть пустую структуру
    public Form(String strWord) {
        this(strWord, new MorfologyCharacteristicsForConversion());
    }

    public Form(String strWordform, MorfologyCharacteristicsForConversion morfChar) {
        this.strWordform = strWordform;
        this.morfCharacteristics = morfChar;
        Form.sizeForm++;
    }

    public String getStringForm() {
        return strWordform;
    }

    @Override
    public int hashCode() {
        return strWordform.hashCode();
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
        final Form other = (Form) obj;
        return Objects.equals(this.strWordform, other.strWordform);
    }

    public int hashCodeByMorfCharact() {
        return morfCharacteristics.hashCode();
    }

    public long getValue() {
        return morfCharacteristics.getMorfCharacterisics();
    }
}
