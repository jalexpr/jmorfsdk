import org.tfwwt.jmorfsdk.JMorfSdk;
import org.tfwwt.jmorfsdk.load.JMorfSdkLoad;

import java.util.Date;
import java.util.List;
//import parserstring.Parser;

public class MultithreadingTestJMorfSdk {

    public static void main(String[] args) throws InterruptedException {

        JMorfSdk jMorfSdk = JMorfSdkLoad.loadFullLibrary();
        List<List<List<String>>> listString = null;
//        List<List<List<String>>> listString = Parser.parserParagraph(FileOpen.readLine(FileOpen.openBufferedReaderStream("TextTest_1502_Word.txt")).toLowerCase());

        int count = 0;
        for(List<List<String>> sents : listString) {
            count = sents.stream().map((words) -> words.size()).reduce(count, Integer::sum);
        }

        int pov = 100000;

//        long time = oneThread(jMorfSdk, pov, listString);
        long time = miltThread(jMorfSdk, pov, listString);

        System.out.println(String.format("Время чтения %d слова %d раз : %d мс", count, pov, time));
    }

    private static long oneThread(JMorfSdk jMorfSdk, int pov, List<List<List<String>>> listString) {

        long timeStart = new Date().getTime();

        for(int i = 0; i < pov; i++) {
            listString.forEach((sents) -> {
                sents.forEach((words) -> {
                    words.forEach((word) -> {
                        try {
                            jMorfSdk.getAllCharacteristicsOfForm(word);
                        } catch (Exception ex) {
//                            Logger.getLogger(MultithreadingTestJMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                });
            });
        }

        long timeEnd = new Date().getTime();

        return timeEnd - timeStart;
    }

    private static long miltThread(JMorfSdk jMorfSdk, int pov, List<List<List<String>>> listString) {

        long timeStart = new Date().getTime();

        for(int i = 0; i < pov; i++) {

            Runnable task = () -> {
                listString.forEach((sents) -> {
                        sents.forEach((words) -> {
                                words.forEach((word) -> {
                                    try {
                                        jMorfSdk.getAllCharacteristicsOfForm(word);
                                    } catch (Exception ex) {
//                                        Logger.getLogger(MultithreadingTestJMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                });
                        });
                });
            };
            Thread thread = new Thread(task);
            thread.start();
        }

        long timeEnd = new Date().getTime();

        return timeEnd - timeStart;
    }
}
