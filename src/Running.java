
import java.io.IOException;
import java.util.ArrayList;
import jmorfsdk.AllCharacteristicsOfForm;
import jmorfsdk.JMorfSdk;
import jmorfsdk.grammeme.MorfologyParameters.*;
import jmorfsdk.load.LoadJMorfSdk;

public class Running {

    public static void main(String[] args) throws IOException, Exception {

        //Пример загрузки библиотеки
        JMorfSdk jMorfSdk = LoadJMorfSdk.loadInAnalysisMode();

        System.err.println("");
        //Пример получения характеристик заданой формы
        ArrayList<AllCharacteristicsOfForm> characteristics = jMorfSdk.getAllCharacteristicsOfForm("гладь");
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

        ArrayList<AllCharacteristicsOfForm> characteristics1 = jMorfSdk.getAllCharacteristicsOfForm("замок");
        characteristics1.forEach((form) -> {
            System.out.println(form);
        });

        ArrayList<AllCharacteristicsOfForm> characteristics2 = jMorfSdk.getAllCharacteristicsOfForm("по");
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

//        jMorfSdk.getDerivativeForm("мыло", Animacy.INANIMATE | Gender.MANS);

        jMorfSdk.finish();
    }
}
