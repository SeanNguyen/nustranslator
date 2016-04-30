package sg.edu.nus.nustranslator;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import sg.edu.nus.nustranslator.recognizers.LocalSpeechRecognizer;
import sg.edu.nus.nustranslator.utils.DataManager;


public class AppModel {
    private static AppModel sAppModel;
    private int mNumPairs = 0;
    private int mDataVersion = 0;
    private ArrayList<String> mMainLanguages;
    private ArrayList<String> mLanguages;
    private HashMap<String, ArrayList<String>> mLanguagesWithSortedSentences;

    //constructor
    private AppModel(Context appContext) {
        mMainLanguages = new ArrayList<>();
        mLanguages = new ArrayList<>();
        mLanguagesWithSortedSentences = new HashMap<>();
        DataManager.deserializeData(this, appContext);
    }

    public static AppModel getInstance(Context appContext) {
        if(sAppModel == null) {
            sAppModel = new AppModel(appContext);
        }
        return sAppModel;
    }

    //public methods
    public void resetModel() {
        mNumPairs = 0;
        mDataVersion = 0;
        mLanguages = new ArrayList<>();
        mMainLanguages = new ArrayList<>();
        mLanguagesWithSortedSentences = new HashMap<>();
    }

    public String getTranslation(String input, String originalLanguage, String translationLanguage) {
        if(input.equals("")) {
            return input;
        } else {
            input = input.toLowerCase();
            ArrayList<String> originalSentences = mLanguagesWithSortedSentences.get(originalLanguage);
            ArrayList<String> destinationSentences = mLanguagesWithSortedSentences.get(translationLanguage);

            Collections.sort(originalSentences);
            int index = Collections.binarySearch(originalSentences, input);
            if(index <= -1) {
                return "";
            } else {
                return destinationSentences.get(index);
            }
        }
    }

    public ArrayList<String> getSentencesByLanguageName(String language) {
        if (mLanguagesWithSortedSentences == null) {
            return null;
        }
        return mLanguagesWithSortedSentences.get(language);
    }

    public ArrayList<String> getSentencesByLanguageIndex(int index) {
        if (index < 0 || index > mLanguages.size() - 1) {
            return new ArrayList<>();
        } else {
            String languageName = mLanguages.get(index);
            return getSentencesByLanguageName(languageName);
        }
    }

    public void addMainLanguage(String language, ArrayList<String> sentences) {
        mMainLanguages.add(language);
        mLanguagesWithSortedSentences.put(language, sentences);
    }

    public void addLanguage(String language, ArrayList<String> sentences) {
        mLanguages.add(language);
        mLanguagesWithSortedSentences.put(language, sentences);
    }

    public ArrayList<String> getAllLanguages() {
        return new ArrayList<>(mLanguages);
    }

    public ArrayList<String> getMainLanguages() {
        return new ArrayList<>(mMainLanguages);
    }

    public int getNumLanguages() {
        return mLanguages.size();
    }

    public int getNumPairs() {
        return mNumPairs;
    }

    public void setNumPairs(int n) {
        mNumPairs = n;
    }

    public int getDataVersion() {
        return mDataVersion;
    }

    public void setDataVersion(int version) {
        mDataVersion = version;
    }

    public String matchAgainstKnownWords(String sentence, String language) {
        sentence = sentence.toLowerCase();
        ArrayList<String> knownSentences = getSentencesByLanguageName(language);

        if (sentence.contains(LocalSpeechRecognizer.ACTIVATE_PHRASE.toLowerCase())){
            // activation phrase
            return LocalSpeechRecognizer.ACTIVATE_PHRASE;
        }else if (sentence.contains(LocalSpeechRecognizer.DEACTIVATE_PHRASE.toLowerCase())){
            // deactivation phrase
            return LocalSpeechRecognizer.DEACTIVATE_PHRASE.toLowerCase();
        } else {
            // check word list
            for(int i = 0; i < knownSentences.size(); i++) {
                if (sentence.contains(knownSentences.get(i).toLowerCase())) {
                    return knownSentences.get(i);
                }
            }

            // match with similar sounding words
            return matchAgainstHardcodedList(sentence);
        }
    }

    private String matchAgainstHardcodedList(String sentence) {
        // TODO: migrate this to a db so we can store optimizations in code
        if(sentence.contains("biting")){
            return "Biting surface";
        }else if (sentence.contains("great")){
            return "Bridge";
        }else if (sentence.contains("implants")){
            return "Dental implants";
        }else if (sentence.contains("outer")){
            return "Outer surface";
        }else if (sentence.contains("inner")){
            return "Inner surface";
        }else if (sentence.contains("mandible")){
            return "Protrude mandible";
        }else if (sentence.contains("canal") || sentence.contains("rail")){
            return "Root Canal";
        }else if (sentence.contains("wife them")){
            return "Wisdom Tooth";
        }else if (sentence.contains("life them")){
            return "Wisdom Tooth";
        }else if (sentence.contains("life gum")){
            return "Wisdom Tooth";
        }else if (sentence.contains("had eight horse")){
            return "Halitosis";
        }else if (sentence.contains("team fat men")){
            return "Inflammation";
        }else if (sentence.contains("team men")){
            return "Inflammation";
        }else if ( (sentence.contains("enough") && (sentence.contains("suffix"))
                || sentence.contains("sentence")) ){
            return "Inner surface";
        }else if (sentence.contains("out has")){
            return "Outer surface";
        }else if (sentence.contains("canal") && sentence.contains(("treatment"))) {
            return "Root Canal Treatment";
        }else if (sentence.contains("back area")){
            return "Bacteria";
        }else if (sentence.contains("back carry")){
            return "Bacteria";
        }else if (sentence.contains("bat hear")){
            return "Bacteria";
        }else if (sentence.contains("feed eat")){
            return "Filling";
        }else if (sentence.contains("feed mean")){
            return "Filling";
        }else if (sentence.contains("fear")){
            return "Filling";
        }else if (sentence.contains("noun") && (sentence.contains("base"))){
            return "Filling";
        }else if (sentence.contains("fear")){
            return "Gum Disease";
        }else if (sentence.contains("high") && sentence.contains("cause")){
            return "Halitosis";
        }else if (sentence.contains("decay")){
            return "Tooth Decay";
        }else if (sentence.contains("how")){
            return "Pulp";
        }else if(sentence.contains("pound") && sentence.contains("these")){
            return "Gum Disease";
        }else if (sentence.contains("tall") && sentence.contains("sense")){
            return "Halitosis";
        }else if (sentence.contains("suffix") && sentence.contains("eye")){
            return "Biting surface";
        }else if (sentence.contains("sat")&& sentence.contains("eye")){
            return "Biting surface";
        }else  {
            return "";
        }
    }
}
