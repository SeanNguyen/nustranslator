package sg.edu.nus.nustranslator;

import java.util.Vector;

import sg.edu.nus.nustranslator.ui.HelpActivity;
import sg.edu.nus.nustranslator.AppModel;


public class HelpController {
    // TODO: get rid of this controller, MVC is good enough for such a small list of functions
    private AppModel appModel;
    private HelpActivity view;

    public HelpController(HelpActivity view) {
        this.view = view;
        appModel = AppModel.getInstance(view.getApplicationContext());
        Vector<String> languages = appModel.getAllLanguages();
        view.updateLanguageList(languages);
    }

    public void changeLanguage(int index) {
        Vector<String> sentences = appModel.getSentencesByLanguageIndex(index);
        view.updateSentenceList(sentences);
    }
}
