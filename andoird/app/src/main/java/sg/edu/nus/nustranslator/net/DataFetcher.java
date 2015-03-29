package sg.edu.nus.nustranslator.net;

import android.util.Log;

import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

import sg.edu.nus.nustranslator.ultis.Configurations;
import sg.edu.nus.nustranslator.models.AppModel;

/**
 * Created by Storm on 3/11/2015.
 */
public class DataFetcher {
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
                Log.e("FETCH DATA", "Data is up to date");
                return true;
            }

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
            model.setDataVersion(dataVersion);

            //logging
            Log.e("FETCH DATA", "Finish Fetching Data");
            Log.e("FETCH DATA", "Data Version: " + dataVersion);
            Log.e("FETCH DATA", "Number of language: " + numberOfLanguage);
            Log.e("FETCH DATA", "Number of sentence each: " + numberOfPair);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FETCH DATA", "Fail Fetching Data");
            return false;
        }
    }
}
