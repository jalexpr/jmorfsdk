package jmorfsdk.grammeme.forconversion;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.*;

/**
 *
 * @author Alex Porechny alex.porechny@mail.ru
 */
public class MorfologyCharacteristicsForConversion {

    private static final HashMap<String, MyField> MAPMYFIELD;
    private long morfCharacterisics = 0l;

    static {
        Field[] fields = getFieldsClass(MorfologyParametersForConversion.class);
        MAPMYFIELD = getHashMapField(fields);
    }

    private static Field[] getFieldsClass(Class cl) {
        return cl.getFields();
    }

    private static HashMap<String, MyField> getHashMapField(Field[] fields) {

        HashMap<String, MyField> hashMap = new HashMap<>();
        for (Field f : fields) {
            MyField myField = new MyField(f);
            hashMap.put(myField.getName(), new MyField(f));
        }
        return hashMap;
    }

    public void addMorfCharacteristics(String nameMorfParameters) throws Exception {
        if(!isAlreadyAdded(nameMorfParameters)){
            morfCharacterisics |= getValueMorfParameter(nameMorfParameters);
        } else {
            throw new Exception("Попытка добавить уже добавленную характеристику "
                    + "или характеристику из той же группы, которая добавлена!\n"
                    + "nameMorfParameters = " + nameMorfParameters
                    + "\n" + Long.toBinaryString(morfCharacterisics));
        }
    }

    public static byte addMorfCharacteristicsPost(String nameMorfParameters) throws Exception {
        return (byte) getValueMorfParameter(nameMorfParameters);
    }

    private boolean isAlreadyAdded(String nameMorfParameters) {
        if (!nameMorfParameters.equals("INAN") && !nameMorfParameters.equals("ANIM")){
            long newMorfParameter = getValueMorfParameter(nameMorfParameters);
            long disjunction = morfCharacterisics & newMorfParameter;
            long conjunction = morfCharacterisics | newMorfParameter;
            long strictDisjunction = conjunction ^ newMorfParameter;
            boolean isNotYetAdded = disjunction == 0 &&   strictDisjunction == morfCharacterisics;
            return !isNotYetAdded;
        } else {
            return false;
        }
    }

    public static long getValueMorfParameter(String nameMorfCharacterisics) {

        long value = 0L;
        try{
            value = MAPMYFIELD.get(nameMorfCharacterisics).getValue();
        } catch (NullPointerException e){
            System.err.println(nameMorfCharacterisics);
        }

        return value;
    }

    public long getMorfCharacterisics() {
        return morfCharacterisics;
    }

    private static class MyField {

        private String name;
        private long value;

        public MyField(Field f) {
            name = f.getName();
            try {
                value = f.getLong(f);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(MorfologyCharacteristicsForConversion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public int hashCode() {
            return name.hashCode();
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
            final MyField other = (MyField) obj;
            return Objects.equals(this.name, other.name);
        }

        public String getName() {
            return name;
        }

        public long getValue() {
            return value;
        }
    }
}
