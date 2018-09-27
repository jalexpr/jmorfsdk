import ru.textanalysis.tfwwt.jmorfsdk.jmorfsdk.JMorfSdk;
import ru.textanalysis.tfwwt.jmorfsdk.jmorfsdk.load.JMorfSdkLoad;
import ru.textanalysis.tfwwt.morphological.structures.grammeme.MorfologyParameters.*;
import ru.textanalysis.tfwwt.morphological.structures.grammeme.MorfologyParametersHelper;
import ru.textanalysis.tfwwt.morphological.structures.internal.OmoForm;
import ru.textanalysis.tfwwt.morphological.structures.load.BDFormString;
import ru.textanalysis.tfwwt.morphological.structures.storage.OmoFormList;

import java.util.List;


public class Running {

    public static void main(String[] args) throws Exception {

        //Пример загрузки библиотеки
        JMorfSdk jMorfSdk = JMorfSdkLoad.loadFullLibrary();
//        BDInitialFormString.printAll(true);

        System.err.println("");
        //Пример получения характеристик заданой формы
        List<OmoForm> characteristics;

        characteristics = jMorfSdk.getAllCharacteristicsOfForm("мама");
        characteristics.forEach((form) -> {
            System.out.println(form);
        });

        jMorfSdk.getAllCharacteristicsOfForm("мыла").forEach((form) -> {
            //Пример поиска формы в родительном падеже
            if (form.getTheMorfCharacteristics(Case.IDENTIFIER) == Case.GENITIVE) {
                System.out.println("Форма в родительном падеже " + form);
            }

            //Пример поиска формы с частью речи глагол
            if (form.getTypeOfSpeech() == TypeOfSpeech.VERB) {
                System.out.println("Форма с глаголом найдена " + form);
            }
        });

        List<OmoForm> characteristics1 = jMorfSdk.getAllCharacteristicsOfForm("замок");
        characteristics1.forEach((form) -> {
            System.out.println(form);
        });

        List<OmoForm> characteristics2 = jMorfSdk.getAllCharacteristicsOfForm("по");
        characteristics2.forEach((form) -> {
            System.out.println(form);
        });

        //Пример проверки слов в словаре
        if(jMorfSdk.isFormExistsInDictionary("че")) {
            System.out.println("Слово \"че\" найдено");
        } else {
            System.out.println("Слово \"че\" не найдено");
        }

        if(jMorfSdk.isFormExistsInDictionary("чтотакое")) {
            System.out.println("Слово \"чтотакое\" найдено");
        } else {
            System.out.println("Слово \"чтотакое\" не найдено");
        }

//        try{
//            List<OmoForm> characteristics3 = jMorfSdk.getAllCharacteristicsOfForm("чтотакое");
//                characteristics3.forEach((form) -> {
//                System.out.println(form);
//            });
//        } catch (Exception ex) {
//            Logger.getLogger(Running.class.getName()).log(Level.SEVERE, null, ex);
//        }

        List<OmoForm> characteristics5 = jMorfSdk.getAllCharacteristicsOfForm("мыл");
        characteristics5.forEach((form) -> {
            System.out.println(form);
        });

        OmoFormList omoFormMama = jMorfSdk.getAllCharacteristicsOfForm("мыла");
        System.out.println("печать из встроенной структуры");
        System.out.println(omoFormMama.get(0).getMyFormString());
        System.out.println(omoFormMama.get(0).getInitialFormString());


        int formKey = omoFormMama.get(0).getMyFormKey();
        int formInitialFormKey = omoFormMama.get(0).getInitialFormKey();

        System.out.println("печать из встроенного класса для работы с БД");
        System.out.println(BDFormString.getStringById(formKey));
        System.out.println(BDFormString.getStringById(formInitialFormKey));

        System.out.println("_____");

        OmoForm omoForm;
        Long morfCharacteristic;

        omoForm = jMorfSdk.getAllCharacteristicsOfForm("выпит").get(0);
        System.out.println("получение конкретной характеристики по идентификатору");
        morfCharacteristic = omoForm.getTheMorfCharacteristics(Voice.IDENTIFIER);
        System.out.println(MorfologyParametersHelper.getParametersName(morfCharacteristic));

        System.out.println("получение конкретной характеристики по классу");
        morfCharacteristic = omoForm.getTheMorfCharacteristics(Voice.class);
        System.out.println(MorfologyParametersHelper.getParametersName(morfCharacteristic));

        System.out.println("_____");

        jMorfSdk.getAllCharacteristicsOfForm("село").forEach((form) -> {
            if(form.getTheMorfCharacteristics(Time.class) == Time.PAST) {
                System.out.println(form);
            }
        });

        System.out.println(Time.PAST);

        System.out.println("_____");

        jMorfSdk.getDerivativeForm("мыло", TypeOfSpeech.NOUN).forEach((wordString) -> {
            System.out.println(wordString);
        });

        System.out.println("_____");

        jMorfSdk.getDerivativeForm("мыло", TypeOfSpeech.NOUN,
                    Numbers.SINGULAR).forEach((wordString) -> {
            System.out.println(wordString);
        });

        List<OmoForm> characteristics7 = jMorfSdk.getAllCharacteristicsOfForm("что-то");
        characteristics7.forEach((form) -> {
            System.out.println(form);
        });

        List<OmoForm> characteristics8 = jMorfSdk.getAllCharacteristicsOfForm("123");
        characteristics8.forEach((form) -> {
            System.out.println(form);
        });

        List<OmoForm> characteristics9 = jMorfSdk.getAllCharacteristicsOfForm("что-нибудь");
        characteristics9.forEach((form) -> {
            System.out.println(form);
        });

        try {
            List<OmoForm> characteristics6 = jMorfSdk.getAllCharacteristicsOfForm("подает");
            characteristics6.forEach((form) -> {
                System.out.println(form);
            });
        } catch (Exception ex) {
            System.out.println(ex);
        }

        System.out.println("________________________________________");
        List<OmoForm> characteristics10 = jMorfSdk.getAllCharacteristicsOfForm("мама");
        characteristics10.forEach((form) -> {
            System.out.println(form.getInitialFormString());
        });
        jMorfSdk.finish();
    }
}
