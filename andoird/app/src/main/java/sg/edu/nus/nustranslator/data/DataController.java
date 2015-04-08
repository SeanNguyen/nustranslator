package sg.edu.nus.nustranslator.data;

import android.content.Context;
import android.media.AudioRecord;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.Vector;

import sg.edu.nus.nustranslator.net.AudioStreamer;
import sg.edu.nus.nustranslator.net.DataFetcher;
import sg.edu.nus.nustranslator.ultis.Configurations;
import sg.edu.nus.nustranslator.models.AppModel;

/**
 * Created by Storm on 3/6/2015.
 */
public class DataController {
    //Attributes
    AudioStreamer audioStreamer = new AudioStreamer();
    DataFetcher dataFetcher = new DataFetcher();

    //Constructor
    public DataController() {
    }

    //Public Methods
    public void startAudioStream(AudioRecord recorder, Context context) {
        //Check Network Status
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            audioStreamer.startStream(recorder);
        } else {
            // display error
        }
    }

    public void stopStream() {
        audioStreamer.stopStream();
    }

    public void serializeData(AppModel model, Context context) {
        //the format will be:
        //data version
        //number of language
        //number of sentence in each language
        //Language name
        //sentences
        String fileName = Configurations.Data_fileName_sentences;
        int noOfLanguage = model.getNumberOfLanguage();
        int noOfPair = model.getNumberOfPair();
        Vector<String> languages = model.getAllLanguages();
        try {
            BufferedWriter outputStream = new BufferedWriter(
                    new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE)));
            outputStream.write(String.valueOf(model.getDataVersion()));
            outputStream.newLine();
            outputStream.write(String.valueOf(noOfLanguage));
            outputStream.newLine();
            outputStream.write(String.valueOf(noOfPair));
            outputStream.newLine();
            for (int i = 0; i < noOfLanguage; i++) {
                String language = languages.get(i);
                outputStream.write(language);
                outputStream.newLine();
                Vector<String> sentences = model.getSentencesByLanguageName(language);
                for (int j = 0; j < noOfPair; j++) {
                    outputStream.write(sentences.get(j));
                    outputStream.newLine();
                }
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deserializeData(AppModel model, Context context) {
        //the format will be:
        //data version
        //number of language
        //number of sentence in each language
        //Language name
        //sentences
        model.resetModel();
        String fileName = Configurations.Data_fileName_sentences;
        Vector<String> languages = model.getAllLanguages();
        try {
            Scanner scanner = new Scanner(context.openFileInput(fileName));
            int dataVersion = Integer.parseInt(scanner.nextLine());
            model.setDataVersion(dataVersion);

            int noOfLanguage = Integer.parseInt(scanner.nextLine());
            int noOfPair = Integer.parseInt(scanner.nextLine());
            model.setNumberOfPair(noOfPair);

            for (int i = 0; i <noOfLanguage; i++) {
                String language = scanner.nextLine();
                Vector<String> sentences = new Vector<String>();
                for (int j = 0; j < noOfPair; j++) {
                    String sentence = scanner.nextLine();
                    sentences.add(sentence);
                }
                model.addLanguage(language, sentences);
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateData(AppModel model, Context context) {
        dataFetcher.fetchData(model);
        serializeData(model, context);

        Vector<String> languages = model.getAllLanguages();
        for (int i = 0; i < languages.size(); i++) {
            String language = languages.get(i).toLowerCase();
            String dictContent = dataFetcher.queryDict(language);
            String languageModelContent = dataFetcher.queryLanguageModel((language));
            saveDict(language, dictContent, context);
            saveLanguageModel(language, languageModelContent, context);
        }
    }

    //private
    private void saveDict(String language, String content, Context context) {
        try {
            String fileName = language + Configurations.Data_fileName_dict_ext;
            BufferedWriter outputStream = new BufferedWriter(
                    new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE)));
            outputStream.write(content);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveLanguageModel(String language, String content, Context context) {
        try {
            String fileName = language + Configurations.Data_fileName_languageModel_ext;
            BufferedWriter outputStream = new BufferedWriter(
                    new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE)));
            outputStream.write(content);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
