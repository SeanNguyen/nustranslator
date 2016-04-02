package sg.edu.nus.nustranslator.models;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Storm on 3/5/2015.
 */
public class AppModel {

    private static AppModel instance = new AppModel();

    private States appState = States.INACTIVE;
    private int numberOfPair = 0;
    private int dataVersion = 0;
    private Vector<String> languages = new Vector<String>();
    private HashMap<String, Vector<String>> languageSentencesMap = new HashMap<String, Vector<String>>();

    public String originalLanguage;
    public String destinationLanguage;

    //constructor
    private AppModel() {
    }

    public static AppModel getInstance() {
        return instance;
    }

    //public methods
    public void resetModel() {
        this.numberOfPair = 0;
        this.dataVersion = 0;
        this.languages = new Vector<>();
        this.languageSentencesMap = new HashMap<>();
        this.originalLanguage = "";
        this.destinationLanguage = "";
    }

    public States getAppState() {
        return this.appState;
    }

    public void setAppState(States state) {
        this.appState = state;
    }

    public String getTranslation(String input) {
        Vector<String> originalSentences = this.languageSentencesMap.get(this.originalLanguage);
        Vector<String> destinationSentences = this.languageSentencesMap.get(this.destinationLanguage);
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

    public String getOriginalLanguage() {
        return this.originalLanguage;
    }

    public String getDestinationLanguage() {
        return this.destinationLanguage;
    }

    public void setOriginalLanguage(String lang) {
        this.originalLanguage = lang;
    }

    public void setDestinationLanguage(String lang) {
        this.destinationLanguage = lang;
    }
}
