package sg.edu.nus.nustranslator;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import sg.edu.nus.nustranslator.utils.DataManager;


public class AppModel {
    private static AppModel sAppModel;
    private int mNumPairs = 0;
    private int mDataVersion = 0;
    private ArrayList<String> mMainLanguages;
    private ArrayList<String> mLanguages;
    private HashMap<String, Vector<String>> mLanguagesWithSortedSentences;

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
            Vector<String> originalSentences = mLanguagesWithSortedSentences.get(originalLanguage);
            Vector<String> destinationSentences = mLanguagesWithSortedSentences.get(translationLanguage);

            Collections.sort(originalSentences);
            int index = Collections.binarySearch(originalSentences, input);
            if(index <= -1) {
                return "";
            } else {
                return destinationSentences.get(index);
            }
        }
    }

    public Vector<String> getSentencesByLanguageName(String language) {
        if (mLanguagesWithSortedSentences == null) {
            return null;
        }
        return mLanguagesWithSortedSentences.get(language);
    }

    public Vector<String> getSentencesByLanguageIndex(int index) {
        if (index < 0 || index > mLanguages.size() - 1) {
            return new Vector<>();
        } else {
            String languageName = mLanguages.get(index);
            return getSentencesByLanguageName(languageName);
        }
    }

    public void addMainLanguage(String language, Vector<String> sentences) {
        mMainLanguages.add(language);
        mLanguagesWithSortedSentences.put(language, sentences);
    }

    public void addLanguage(String language, Vector<String> sentences) {
        mLanguages.add(language);
        mLanguagesWithSortedSentences.put(language, sentences);
    }

    public Vector<String> getAllLanguages() {
        return new Vector<String>(mLanguages);
    }

    public Vector<String> getMainLanguages() {
        return new Vector<String>(mMainLanguages);
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

}
