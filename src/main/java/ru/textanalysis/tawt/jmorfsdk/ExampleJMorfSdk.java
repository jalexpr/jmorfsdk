package ru.textanalysis.tawt.jmorfsdk;

import ru.textanalysis.tawt.jmorfsdk.loader.JMorfSdkFactory;
import ru.textanalysis.tawt.ms.grammeme.MorfologyParameters.*;
import ru.textanalysis.tawt.ms.grammeme.MorfologyParametersHelper;
import ru.textanalysis.tawt.ms.internal.IOmoForm;
import ru.textanalysis.tawt.ms.loader.BDFormString;
import ru.textanalysis.tawt.ms.storage.OmoFormList;

import java.util.List;

public class ExampleJMorfSdk {
    public static void main(String[] args) throws Exception {
        //Пример загрузки библиотеки
        JMorfSdk jMorfSdk = JMorfSdkFactory.loadFullLibrary();
//        BDInitialFormString.printAll(true);

//        System.err.println("");
//        {
//            Long start = System.currentTimeMillis();
//            for (long i = 0; i < 900_000; i++) {
//                jMorfSdk.getAllCharcteristicsOfForm("стол");
//            }
//            long finish = System.currentTimeMillis();
//            System.out.println(finish - start);
//        }
//        {
//            Long start = System.currentTimeMillis();
//            for(long i = 0; i < 900_000;i++) {
//                jMorfSdk.getDerivativeForm("село", TypeOfSpeech.NOUN);
//            }
//            long finish = System.currentTimeMillis();
//            System.out.println(finish - start);
//        }
        //Пример получения характеристик заданой формы
        List<IOmoForm> characteristics;

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

        List<IOmoForm> characteristics1 = jMorfSdk.getAllCharacteristicsOfForm("замок");
        characteristics1.forEach((form) -> {
            System.out.println(form);
        });

        List<IOmoForm> characteristics2 = jMorfSdk.getAllCharacteristicsOfForm("по");
        characteristics2.forEach((form) -> {
            System.out.println(form);
        });

        //Пример проверки слов в словаре
        if (jMorfSdk.isFormExistsInDictionary("че")) {
            System.out.println("Слово \"че\" найдено");
        } else {
            System.out.println("Слово \"че\" не найдено");
        }

        if (jMorfSdk.isFormExistsInDictionary("чтотакое")) {
            System.out.println("Слово \"чтотакое\" найдено");
        } else {
            System.out.println("Слово \"чтотакое\" не найдено");
        }

//        try{
//            List<IOmoForm> characteristics3 = jMorfSdk.getAllCharacteristicsOfForm("чтотакое");
//                characteristics3.forEach((form) -> {
//                System.out.println(form);
//            });
//        } catch (Exception ex) {
//            Logger.getLogger(Running.class.getName()).log(Level.SEVERE, null, ex);
//        }

        List<IOmoForm> characteristics5 = jMorfSdk.getAllCharacteristicsOfForm("мыл");
        characteristics5.forEach((form) -> {
            System.out.println(form);
        });

        OmoFormList omoFormMama = jMorfSdk.getAllCharacteristicsOfForm("мыла");
        System.out.println("печать из встроенной структуры");
        System.out.println(omoFormMama.get(0).getMyFormString());
        System.out.println(omoFormMama.get(0).isTypeForm());
        System.out.println(omoFormMama.get(0).getInitialFormString());

        OmoFormList omoFormMilo = jMorfSdk.getAllCharacteristicsOfForm("мылo");
        System.out.println("печать из встроенной структуры");
        System.out.println(omoFormMilo.get(0).getMyFormString());
        System.out.println(omoFormMilo.get(0).isTypeForm());
        System.out.println(omoFormMilo.get(0).getInitialFormString());

        OmoFormList omoFormMilO = jMorfSdk.getAllCharacteristicsOfForm("мыло");
        System.out.println("печать из встроенной структуры");
        System.out.println(omoFormMilO.get(0).getMyFormString());
        System.out.println(omoFormMilO.get(0).isTypeForm());
        System.out.println(omoFormMilO.get(0).getInitialFormString());

        int formKey = omoFormMama.get(0).getMyFormKey();
        int formInitialFormKey = omoFormMama.get(0).getInitialFormKey();

        System.out.println("печать из встроенного класса для работы с БД");
        System.out.println(BDFormString.getStringById(formKey));
        System.out.println(BDFormString.getStringById(formInitialFormKey));

        System.out.println("_____");

        IOmoForm omoForm;
        Long morfCharacteristic;

        omoForm = jMorfSdk.getAllCharacteristicsOfForm("выпит").get(0);
        System.out.println("получение конкретной характеристики по идентификатору");
        morfCharacteristic = omoForm.getTheMorfCharacteristics(Voice.IDENTIFIER);
        System.out.println(MorfologyParametersHelper.getParametersName(morfCharacteristic));

        System.out.println("получение конкретной характеристики по классу");
        morfCharacteristic = omoForm.getTheMorfCharacteristics(Voice.class);
        System.out.println(MorfologyParametersHelper.getParametersName(morfCharacteristic));

        System.out.println("_____");

        jMorfSdk.getAllCharacteristicsOfForm("дорогой").forEach((form) -> {
            if (form.isContainsTypeOfSpeech(TypeOfSpeech.NOUN)) {
                System.out.println(form);
            }
            if (form.isContainsMorphCharacteristic(Case.class, Case.GENITIVE)) {
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

        List<IOmoForm> characteristics7 = jMorfSdk.getAllCharacteristicsOfForm("что-то");
        characteristics7.forEach((form) -> {
            System.out.println(form);
        });

        List<IOmoForm> characteristics8 = jMorfSdk.getAllCharacteristicsOfForm("123");
        characteristics8.forEach((form) -> {
            System.out.println(form);
        });

        List<IOmoForm> characteristics9 = jMorfSdk.getAllCharacteristicsOfForm("что-нибудь");
        characteristics9.forEach((form) -> {
            System.out.println(form);
        });

        try {
            List<IOmoForm> characteristics6 = jMorfSdk.getAllCharacteristicsOfForm("подает");
            characteristics6.forEach((form) -> {
                System.out.println(form);
            });
        } catch (Exception ex) {
            System.out.println(ex);
        }

        List<IOmoForm> characteristic7 = jMorfSdk.getAllCharacteristicsOfForm("424132");
        characteristic7.forEach((form) -> {
            System.out.println(form);
        });

        System.out.println("________________________________________");
        List<IOmoForm> characteristics10 = jMorfSdk.getAllCharacteristicsOfForm("мама");
        characteristics10.forEach((form) -> {
            System.out.println(form.getInitialFormString());
        });
        jMorfSdk.finish();
    }
}
