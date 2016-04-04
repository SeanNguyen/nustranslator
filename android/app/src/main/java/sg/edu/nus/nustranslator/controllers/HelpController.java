package sg.edu.nus.nustranslator.controllers;

import java.util.Vector;

import sg.edu.nus.nustranslator.ui.HelpActivity;
import sg.edu.nus.nustranslator.models.AppModel;


public class HelpController {

    private AppModel appModel = AppModel.getInstance();
    private HelpActivity view;

    public HelpController(HelpActivity view) {
        this.view = view;
        Vector<String> languages = appModel.getAllLanguages();
        view.updateLanguageList(languages);
    }

    public void changeLanguage(int index) {
        Vector<String> sentences = appModel.getSentencesByLanguageIndex(index);
        view.updateSentenceList(sentences);
    }
}
