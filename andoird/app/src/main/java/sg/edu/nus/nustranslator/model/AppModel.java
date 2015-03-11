package sg.edu.nus.nustranslator.model;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Storm on 3/5/2015.
 */
public class AppModel {

    private States appState = States.INACTIVE;
    private int numberOfPair = 0;
    private int dataVersion = 0;
    private Vector<String> languages = new Vector<String>();
    private HashMap<String, Vector<String>> languageSentencesMap = new HashMap<String, Vector<String>>();

    private String originalLanguage = "English";
    private String destinationLanguage = "Vietnamese";

    //constructor
    public AppModel() {
    }

    //public methods
    public States getAppState() {
        return this.appState;
    }

    public void setAppState(States state) {
        this.appState = state;
    }

    public Vector<String> getSentencesOfLanguage(String language) {
        if (this.languageSentencesMap == null) {
            return null;
        }
        return this.languageSentencesMap.get(language);
    }

    public void addLanguage (String language, Vector<String> sentences) {
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
        return  this.destinationLanguage;
    }

    public void setOriginalLanguage(String lang) {
        this.originalLanguage = lang;
    }

    public void setDestinationLanguage(String lang) {
        this.destinationLanguage = lang;
    }
}
