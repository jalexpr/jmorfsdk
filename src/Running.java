
import java.io.IOException;
import jmorfsdk.JMorfSdk;
import jmorfsdk.grammeme.MorfologyParameters.Animacy;
import jmorfsdk.load.LoadJMorfSdk;

public class Running {

    public static void main(String[] args) throws IOException {

        JMorfSdk jMorfSdk = LoadJMorfSdk.loadFullLibrary();

        jMorfSdk.getAllCharacteristicsOfForm("мыла").forEach((form) -> {
            System.out.println(form + " Animacy = " + form.getTheMorfCharacteristic(Animacy.INANIMATE));
            if(form.getTheMorfCharacteristic(Animacy.INANIMATE) == Animacy.ANIMATE){

            }
        });



//        System.in.read();

        jMorfSdk.finish();
    }
}
