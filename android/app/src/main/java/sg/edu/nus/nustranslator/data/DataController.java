package sg.edu.nus.nustranslator.data;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.Vector;

import sg.edu.nus.nustranslator.models.AppModel;
import sg.edu.nus.nustranslator.net.DataFetcher;
import sg.edu.nus.nustranslator.utils.Configurations;

import java.util.ArrayList;


public class DataController {
    //Attributes
    DataFetcher dataFetcher = new DataFetcher();

    public ArrayList<String> mandainList = new ArrayList();
    //Constructor
    public DataController() {
    }

    //Public Methods
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


    public ArrayList<String> deserializeData(AppModel model, Context context) {
        //the format will be:
        //data version
        //number of language
        //number of sentence in each language
        //Language name
        //sentences
        model.resetModel();

        try {
            Scanner scanner = new Scanner(context.getResources().getAssets().open(Configurations.Data_fileName_dir + Configurations.Data_fileName_sentences));
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
                    if(language.equals("English")){
                        mandainList.add(sentence);
                    }
                }
                model.addLanguage(language, sentences);
            }
            scanner.close();
        } catch (IOException e) {
            System.out.print("didn't find the data.txt");
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return mandainList;
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
           // BufferedWriter outputStream = new BufferedWriter(
           //         new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE)));
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
