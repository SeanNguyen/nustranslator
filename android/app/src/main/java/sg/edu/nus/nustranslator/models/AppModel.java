package sg.edu.nus.nustranslator.models;

import android.content.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import sg.edu.nus.nustranslator.utils.DataUtils;


public class AppModel {

    private static AppModel sAppModel;
    private int numberOfPair = 0;
    private int dataVersion = 0;
    private Vector<String> languages = new Vector<String>();
    private HashMap<String, Vector<String>> languageAndSortedSentencesTable = new HashMap<String, Vector<String>>();

    //constructor
    private AppModel() {
    }

    public static AppModel getInstance(Context appContext) {
        if(sAppModel == null) {
            sAppModel = new AppModel();
            DataUtils.deserializeData(sAppModel, appContext);
        }
        return sAppModel;
    }

    //public methods
    public void resetModel() {
        this.numberOfPair = 0;
        this.dataVersion = 0;
        this.languages = new Vector<>();
        this.languageAndSortedSentencesTable = new HashMap<>();
    }

    public String getTranslation(String input, String originalLanguage, String translationLanguage) {
        input = input.toLowerCase();
        Vector<String> originalSentences = this.languageAndSortedSentencesTable.get(originalLanguage);
        Vector<String> destinationSentences = this.languageAndSortedSentencesTable.get(translationLanguage);

        Collections.sort(originalSentences);
        int index = Collections.binarySearch(originalSentences, input);
        if(index <= -1) {
            return "";
        } else {
            return destinationSentences.get(index);
        }
    }

    public Vector<String> getSentencesByLanguageName(String language) {
        if (this.languageAndSortedSentencesTable == null) {
            return null;
        }
        return this.languageAndSortedSentencesTable.get(language);
    }

    public Vector<String> getSentencesByLanguageIndex(int index) {
        if (index < 0 || index > this.languages.size() - 1) {
            return new Vector<>();
        } else {
            String languageName = this.languages.get(index);
            return getSentencesByLanguageName(languageName);
        }
    }

    public void addLanguage(String language, Vector<String> sentences) {
        this.languages.add(language);
        this.languageAndSortedSentencesTable.put(language, sentences);
    }

    public Vector<String> getAllLanguages() {
        return this.languages;
    }

    public int getNumberOfLanguage() {
        return this.languages.size();
    }

    public int getNumberOfPair() {
        return this.numberOfPair;
    }

    public void setNumberOfPair(int n) {
        this.numberOfPair = n;
    }

    public int getDataVersion() {
        return this.dataVersion;
    }

    public void setDataVersion(int version) {
        this.dataVersion = version;
    }

}
