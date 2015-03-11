package sg.edu.nus.nustranslator.data;

import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

import sg.edu.nus.nustranslator.Configurations;
import sg.edu.nus.nustranslator.model.AppModel;

/**
 * Created by Storm on 3/11/2015.
 */
class DataFetcher {
    public DataFetcher() {
    }

    public boolean fetchData(AppModel model) {
        //the format will be:
        //data version
        //number of language
        //number of sentence in each language
        //Language name
        //sentences
        try {
            @SuppressWarnings("resource")
            Socket socket = new Socket(Configurations.Server_address, Configurations.Server_port);
            Scanner scanner = new Scanner(socket.getInputStream());

            //data version
            String line = scanner.nextLine();
            int dataVersion = Integer.parseInt(line);
            if (model.getDataVersion() >= dataVersion) {
                return true;
            }
            model.setDataVersion(dataVersion);

            //language sentence
            int numberOfLanguage = Integer.parseInt(scanner.nextLine());
            int numberOfPair = Integer.parseInt(scanner.nextLine());
            model.setNumberOfPair(numberOfPair);

            for (int i = 0; i < numberOfLanguage; i++) {
                String language = scanner.nextLine();
                Vector<String> sentences = new Vector<String>();
                for (int j = 0; j < numberOfPair; j++) {
                    String sentence = scanner.nextLine();
                    sentences.add(sentence);
                }
                model.addLanguage(language, sentences);
            }
            scanner.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
