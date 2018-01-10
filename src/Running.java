
import java.util.List;
import morphologicalstructures.OmoForm;
import jmorfsdk.JMorfSdk;
import grammeme.MorfologyParameters.*;
import jmorfsdk.load.JMorfSdkLoad;

public class Running {

    public static void main(String[] args) throws Exception {

        //Пример загрузки библиотеки
            JMorfSdk jMorfSdk = JMorfSdkLoad.loadFullLibrary();

//        BDInitialFormString.printAll(true);

        System.err.println("");
        //Пример получения характеристик заданой формы
        List<OmoForm> characteristics;

        characteristics = jMorfSdk.getAllCharacteristicsOfForm("гладь");
        characteristics.forEach((form) -> {
            System.out.println(form);
        });

        jMorfSdk.getAllCharacteristicsOfForm("мыла").forEach((form) -> {
            //Пример поиска формы в родительном падеже
            if (form.getTheMorfCharacteristic(Case.IDENTIFIER) == Case.GENITIVE) {
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

        List<OmoForm> characteristics6 = jMorfSdk.getAllCharacteristicsOfForm("подает");
        characteristics6.forEach((form) -> {
            System.out.println(form);
        });

        jMorfSdk.finish();
    }
}
