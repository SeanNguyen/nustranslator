package sg.edu.nus.nustranslator.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import sg.edu.nus.nustranslator.AppModel;
import sg.edu.nus.nustranslator.Configurations;
import sg.edu.nus.nustranslator.network.DataFetcher;

/**
 * Possible TODO: migrate to json
 *
 * data format is as follows:
 * data version
 * number of languages
 * number of sentences in each language
 * language name
 * sentences (in sorted order)
 */
public class DataManager {
    public static void serializeData(AppModel model, Context context) {
        String fileName = Configurations.Data_fileName_sentences;
        int noOfLanguage = model.getNumLanguages();
        int noOfPair = model.getNumPairs();
        ArrayList<String> languages = model.getAllLanguages();
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
                ArrayList<String> sentences = model.getSentencesByLanguageName(language);
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

    public static void deserializeData(AppModel model, Context context) {
        model.resetModel();

        Scanner scanner = null;
        try {
            scanner = new Scanner(context.getResources()
                                        .getAssets()
                                        .open(Configurations.Data_fileName_dir
                                                + Configurations.Data_fileName_sentences));
            int dataVersion = Integer.parseInt(scanner.nextLine());
            model.setDataVersion(dataVersion);

            int numLanguages = Integer.parseInt(scanner.nextLine());
            int numPairs = Integer.parseInt(scanner.nextLine());
            model.setNumPairs(numPairs);

            for (int i = 0; i <numLanguages; i++) {
                String language = scanner.nextLine();
                ArrayList<String> sentences = new ArrayList<>();
                for (int j = 0; j < numPairs; j++) {
                    String sentence = scanner.nextLine();
                    sentences.add(sentence.toLowerCase());
                }

                if(i == 0) {
                    // TODO: note that first language is set to be main language for now
                    model.addMainLanguage(language, sentences);
                    model.addLanguage(language, sentences);
                } else {
                    model.addLanguage(language, sentences);
                }
            }
        } catch (IOException e) {
            Log.e(DataManager.class.getSimpleName(), "Couldn't find data.txt");
            e.printStackTrace();
        } finally {
            if(scanner != null) {
                scanner.close();
            }
        }
    }

    public static void updateData(AppModel model, Context context) {
        DataFetcher dataFetcher = new DataFetcher();
        dataFetcher.fetchData(model);
        serializeData(model, context);

        ArrayList<String> languages = model.getAllLanguages();
        for (int i = 0; i < languages.size(); i++) {
            String language = languages.get(i).toLowerCase();
            String dictContent = dataFetcher.queryDict(language);
            String languageModelContent = dataFetcher.queryLanguageModel((language));
            saveDict(language, dictContent, context);
            saveLanguageModel(language, languageModelContent, context);
        }
    }

    private static void saveDict(String language, String content, Context context) {
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

    private static void saveLanguageModel(String language, String content, Context context) {
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
