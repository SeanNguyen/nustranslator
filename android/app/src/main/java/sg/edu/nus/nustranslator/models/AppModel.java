package sg.edu.nus.nustranslator.models;

import java.util.HashMap;
import java.util.Vector;


public class AppModel {

    private static AppModel sAppModel;

    private int numberOfPair = 0;
    private int dataVersion = 0;
    private Vector<String> languages = new Vector<String>();
    private HashMap<String, Vector<String>> languageSentencesMap = new HashMap<String, Vector<String>>();

    //constructor
    private AppModel() {
    }

    public static AppModel getInstance() {
        if(sAppModel == null) {
            sAppModel = new AppModel();
        }
        return sAppModel;
    }

    //public methods
    public void resetModel() {
        this.numberOfPair = 0;
        this.dataVersion = 0;
        this.languages = new Vector<>();
        this.languageSentencesMap = new HashMap<>();
    }

    public String getTranslation(String input, String originalLanguage, String translationLanguage) {
        Vector<String> originalSentences = this.languageSentencesMap.get(originalLanguage);
        Vector<String> destinationSentences = this.languageSentencesMap.get(translationLanguage);
        int index = originalSentences.indexOf(input);
        String result = destinationSentences.get(index);
        return result;
    }

    public Vector<String> getSentencesByLanguageName(String language) {
        if (this.languageSentencesMap == null) {
            return null;
        }
        return this.languageSentencesMap.get(language);
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
        this.languageSentencesMap.put(language, sentences);
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
