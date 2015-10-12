package sg.edu.nus.nustranslator.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.widget.Toast;

import junit.framework.Assert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import sg.edu.nus.nustranslator.R;
import sg.edu.nus.nustranslator.controllers.MainController;
import sg.edu.nus.nustranslator.models.States;
import sg.edu.nus.nustranslator.ultis.Configurations;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class MainActivity extends Activity implements
        RecognitionListener {

    //attributes
    private MainController controller;
    private View loadingView;
    private Spinner originalLanguageSpinner;
    private Spinner destinationLanguageSpinner;
    private String bestResult;
    MediaPlayer mp = null;

    public ArrayList<String> mandainListCurrent = new ArrayList();
    boolean trigger = false;


    private static final String START_TRANSLATION_PHRASE = "start translation";
    private static final String END_TRANSLATION_PHRASE = "end translation";

    private static final String MENU_SEARCH = "menu";

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;
    private static final String KWS_SEARCH = "wakeup";

    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "start";
    private static final String KEYPHRASE1 = "stop";


    //events
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.loadingView = findViewById(R.id.main_loading);
        this.originalLanguageSpinner = (Spinner) findViewById(R.id.originalLanguages_spinner);

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(MainActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();



        mandainListCurrent.add("Abscess");
        mandainListCurrent.add("Bacteria");
        mandainListCurrent.add("Biting surface");
        mandainListCurrent.add("Bridge");
        mandainListCurrent.add("Calculus");
        mandainListCurrent.add("Crown");
        mandainListCurrent.add("Dental Calculus");
        mandainListCurrent.add("Dental Floss");
        mandainListCurrent.add("Dental implants");
        mandainListCurrent.add("Dental plaque");
        mandainListCurrent.add("Dentine");
        mandainListCurrent.add("Enamel");
        mandainListCurrent.add("Filling");
        mandainListCurrent.add("Fluoride");
        mandainListCurrent.add("Gum Disease");
        mandainListCurrent.add("Halitosis");
        mandainListCurrent.add("Impression");
        mandainListCurrent.add("Inflammation");
        mandainListCurrent.add("Inner surface");
        mandainListCurrent.add("Local Anaesthesia");
        mandainListCurrent.add("Mouthrinse");
        mandainListCurrent.add("Outer surface");
        mandainListCurrent.add("Protrude mandible");
        mandainListCurrent.add("Pulp");
        mandainListCurrent.add("Regular check-up");
        mandainListCurrent.add("Root Canal");
        mandainListCurrent.add("Root Canal Treatment");
        mandainListCurrent.add("Scaling");
        mandainListCurrent.add("Sensitive teeth");
        mandainListCurrent.add("Tooth Decay");
        mandainListCurrent.add("Wisdom Tooth");






        this.originalLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                loadingView.setVisibility(View.VISIBLE);
                controller.setOriginalLanguage(position);
//                if(position == -1){
//                    currentOriginalLanguage="english";
//                }else{
//                    currentOriginalLanguage="mandarin";
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                loadingView.setVisibility(View.VISIBLE);
                controller.setOriginalLanguage(-1);
//                currentOriginalLanguage="english";
            }

        });
        this.destinationLanguageSpinner = (Spinner) findViewById(R.id.destinationLanguages_spinner);
        this.destinationLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                controller.setDestinationLanguage(position);
//                if(position == -1){
//                    currentDestinationLanguage="english";
//                }else{
//                    currentDestinationLanguage="mandarin";
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                controller.setDestinationLanguage(-1);
//                currentDestinationLanguage="english";

            }

        });
        controller = new MainController(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_updateData:
                controller.updateData();
                this.loadingView.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_viewAllSentences:
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                //show about
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void playBackVideo(View view) {
//        playMp3(currentDestinationLanguage);
        playMp3("mandarin");

    }

    public void playBackVideo1(View view) {
//        playMp3(currentOriginalLanguage);
        playMp3("english");

    }

    public void playMp3(String language)  {
        int index = -1;
        if (bestResult != ""){

            if(mandainListCurrent.contains(bestResult)){
//            if(controller.dataController.mandainList.contains(bestResult)){
//                Uri mp3 = Uri.parse("android.resource://"
//                        +  "sg.edu.nus.nustranslator/raw/"
//                        + datas.get(position).word);
//                /Users/yumengyin/Desktop/nustranslator/andoird/app/src/main/res/raw/bacteria.mp3
                index = controller.mandainList.indexOf(bestResult);
                if (mp != null) {
                    mp.reset();
                    mp.release();
                }
//                System.out.print("test mp3 path is" + R.raw.test_cbr);
//
//
//                try {
//                    AssetFileDescriptor descriptor = getAssets().openFd("test_cbr.mp3");
//                    long start = descriptor.getStartOffset();
//                    long end = descriptor.getLength();
//
//                    mp.setDataSource(descriptor.getFileDescriptor(), start, end);
//                    mp.prepare();
//
//                    mp.setVolume(1.0f, 1.0f);
//                    mp.start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }




                if(language.equals("mandarin")){
                    playMandarin(bestResult);
                }else{
                    playEnglish(bestResult);
                }
                mp.start();
//                try {
//                    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.test_cbr);
////                    //mp.setDataSource("file:///android_asset/"+"mp3/" + "test_cbr.mp3");
//                    String url= uri.toString();
//                    String[] urlList = url.split("test_cbr.mp3");
//                    Uri uriFinal = Uri.parse(urlList[0]+ "m"+bestResult.toLowerCase().replaceAll(" ", "")+".mp3");
//
//                    mp = MediaPlayer.create(this,uriFinal);
////                    mp.setDataSource(this.getResources().getAssets().open("mp3/" + "test_cbr.mp3"));
////                    mp.prepare();
////                    mp.start();
////                    mp = MediaPlayer.create(this.getResources().getAssets().open("MyFolder/" + "MyFile.db3"));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                mp = MediaPlayer.create(this,R.raw.test_cbr );

            }
        }
        if(index >0){

        }


    }
    public static String[] getAllFilesInAssetByExtension(Context context, String path, String extension){
        Assert.assertNotNull(context);

        try {
            String[] files = context.getAssets().list(path);



            List<String> filesWithExtension = new ArrayList<String>();

            for(String file : files){
                if(file.endsWith(extension)){
                    filesWithExtension.add(file);
                }
            }

            return filesWithExtension.toArray(new String[filesWithExtension.size()]);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
    void playMandarin(String bestResult){
        switch (bestResult){
            case "Wisdom Tooth":
                mp = MediaPlayer.create(this, R.raw.mwisdomtooth);
                break;
            case "Tooth Decay":
                mp = MediaPlayer.create(this, R.raw.mtoothdecay);
                break;
            case "Sensitive teeth":
                mp = MediaPlayer.create(this, R.raw.msensitiveteeth);
                break;
            case "Scaling":
                mp = MediaPlayer.create(this, R.raw.mscaling);
                break;
            case "Root Canal Treatment":
                mp = MediaPlayer.create(this, R.raw.mrootcanaltreatment);
                break;
            case "Root Canal":
                mp = MediaPlayer.create(this, R.raw.mrootcanal);
                break;
            case "Local Anaesthesia":
                mp = MediaPlayer.create(this, R.raw.mlocalanaesthesia);
                break;
            case "Regular check-up":
                mp = MediaPlayer.create(this, R.raw.mregularcheckup);
                break;
            case "Pulp":
                mp = MediaPlayer.create(this, R.raw.mpulp);
                break;
            case "Protrude mandible":
                mp = MediaPlayer.create(this, R.raw.mprotrudemandible);
                break;
            case "Outer surface":
                mp = MediaPlayer.create(this, R.raw.moutersurface);
                break;
            case "Mouthrinse":
                mp = MediaPlayer.create(this, R.raw.mmouthrinse);
                break;
            case "Inner surface":
                mp = MediaPlayer.create(this, R.raw.minnersurface);
                break;
            case "Impression":
                mp = MediaPlayer.create(this, R.raw.mimpression);
                break;
            case "Halitosis":
                mp = MediaPlayer.create(this, R.raw.mhalitosis);
                break;
            case "Gum Disease":
                mp = MediaPlayer.create(this, R.raw.mgumdisease);
                break;
            case "Fluoride":
                mp = MediaPlayer.create(this, R.raw.mfluoride);
                break;
            case "Filling":
                mp = MediaPlayer.create(this, R.raw.mfilling);
                break;
            case "Enamel":
                mp = MediaPlayer.create(this, R.raw.menamel);
                break;
            case "Dentine":
                mp = MediaPlayer.create(this, R.raw.mdentine);
                break;
            case "Dental plaque":
                mp = MediaPlayer.create(this, R.raw.mdentalplaque);
                break;
            case "Dental implants":
                mp = MediaPlayer.create(this, R.raw.mdentalimplants);
                break;
            case "Dental Floss":
                mp = MediaPlayer.create(this, R.raw.mdentalfloss);
                break;
            case "Dental Calculus":
                mp = MediaPlayer.create(this, R.raw.mdentalcalculus);
                break;
            case "Crown":
                mp = MediaPlayer.create(this, R.raw.mcrown);
                break;
            case "Calculus":
                mp = MediaPlayer.create(this, R.raw.mcalculus);
                break;
            case "Bridge":
                mp = MediaPlayer.create(this, R.raw.mbridge);
                break;
            case "Biting surface":
                mp = MediaPlayer.create(this, R.raw.mbitingsurface);
                break;
            case "Bacteria":
                mp = MediaPlayer.create(this, R.raw.mbacteria);
                break;
            case "Abscess":
                mp = MediaPlayer.create(this, R.raw.mabscess);
                break;
            case "Inflammation":
                mp = MediaPlayer.create(this, R.raw.minflammation);
                break;



            default:
                break;

        }
    }
    void playEnglish(String bestResult){
        switch (bestResult){
            case "Wisdom Tooth":
                mp = MediaPlayer.create(this, R.raw.wisdomtooth);
                break;
            case "Tooth Decay":
                mp = MediaPlayer.create(this, R.raw.toothdecay);
                break;
            case "Sensitive teeth":
                mp = MediaPlayer.create(this, R.raw.sensitiveteeth);
                break;
            case "Scaling":
                mp = MediaPlayer.create(this, R.raw.scaling);
                break;
            case "Root Canal Treatment":
                mp = MediaPlayer.create(this, R.raw.rootcanaltreatment);
                break;
            case "Root Canal":
                mp = MediaPlayer.create(this, R.raw.rootcanal);
                break;
            case "Local Anaesthesia":
                mp = MediaPlayer.create(this, R.raw.localanaesthesia);
                break;
            case "Regular check-up":
                mp = MediaPlayer.create(this, R.raw.regularcheckup);
                break;
            case "Pulp":
                mp = MediaPlayer.create(this, R.raw.pulp);
                break;
            case "Protrude mandible":
                mp = MediaPlayer.create(this, R.raw.protrudemandible);
                break;
            case "Outer surface":
                mp = MediaPlayer.create(this, R.raw.outersurface);
                break;
            case "Mouthrinse":
                mp = MediaPlayer.create(this, R.raw.mouthrinse);
                break;
            case "Inner surface":
                mp = MediaPlayer.create(this, R.raw.innersurface);
                break;
            case "Impression":
                mp = MediaPlayer.create(this, R.raw.impression);
                break;
            case "Halitosis":
                mp = MediaPlayer.create(this, R.raw.halitosis);
                break;
            case "Gum Disease":
                mp = MediaPlayer.create(this, R.raw.gumdisease);
                break;
            case "Fluoride":
                mp = MediaPlayer.create(this, R.raw.fluoride);
                break;
            case "Filling":
                mp = MediaPlayer.create(this, R.raw.filling);
                break;
            case "Enamel":
                mp = MediaPlayer.create(this, R.raw.enamel);
                break;
            case "Dentine":
                mp = MediaPlayer.create(this, R.raw.dentine);
                break;
            case "Dental plaque":
                mp = MediaPlayer.create(this, R.raw.dentalplaque);
                break;
            case "Dental implants":
                mp = MediaPlayer.create(this, R.raw.dentalimplants);
                break;
            case "Dental Floss":
                mp = MediaPlayer.create(this, R.raw.dentalfloss);
                break;
            case "Dental Calculus":
                mp = MediaPlayer.create(this, R.raw.dentalcalculus);
                break;
            case "Crown":
                mp = MediaPlayer.create(this, R.raw.crown);
                break;
            case "Calculus":
                mp = MediaPlayer.create(this, R.raw.calculus);
                break;
            case "Bridge":
                mp = MediaPlayer.create(this, R.raw.bridge);
                break;
            case "Biting surface":
                mp = MediaPlayer.create(this, R.raw.bitingsurface);
                break;
            case "Bacteria":
                mp = MediaPlayer.create(this, R.raw.bacteria);
                break;
            case "Abscess":
                mp = MediaPlayer.create(this, R.raw.abscess);
                break;
            case "Inflammation":
                mp = MediaPlayer.create(this, R.raw.inflammation);
                break;

            default:

                break;

        }
    }

    /*
     * functions for mode: instant translation
     *
     */

    public void onSessionButtonClick(View view) {
//        if (mp != null) {
//            mp.reset();
//            mp.release();
//        }
        States currentAppState = controller.changeState();
        setResultView(currentAppState);
        setSessionButtonText(currentAppState);

    }

    private void setResultView(States appState) {
        View languageSelection = findViewById(R.id.languageSelection);
        View session = findViewById(R.id.sessionView);
      //  View sessionViewRecorded = findViewById(R.id.sessionViewRecorded);

        if (appState == States.ACTIVE) {
            TextView topResult = (TextView) findViewById(R.id.firstResult);
            topResult.setText("");
            TextView otherResult = (TextView) findViewById(R.id.otherResults);
            otherResult.setText("");
            TextView translatedResult = (TextView) findViewById(R.id.resultText);
            translatedResult.setText("");

            languageSelection.setVisibility(View.GONE);
            session.setVisibility(View.VISIBLE);
           // sessionViewRecorded.setVisibility(View.GONE);
        } else {
            languageSelection.setVisibility(View.VISIBLE);
            session.setVisibility(View.GONE);
            //sessionViewRecorded.setVisibility(View.GONE);
        }
    }


    public void onFinishLoading() {
        this.loadingView.setVisibility(View.GONE);
    }

    public void updateSpeechRecognitionResult(Vector<String> results, String translatedResult) {
        if (results == null) {
            return;
        }
        //Set speech recognition result
        TextView topResult = (TextView) findViewById(R.id.firstResult);
        TextView similarResults = (TextView) findViewById(R.id.otherResults);
        if (results.size() == 0) {
            topResult.setText("");
            similarResults.setText("");
            return;
        }
        topResult.setText(results.get(0));
        bestResult = results.get(0);


        if (controller.appModel.destinationLanguage.equals("Mandarin")) {
            playMp3("mandarin");
        }


        String similarResultText = "";
        for (int i = 1; i < results.size(); i++) {
            similarResultText += results.get(i) + Configurations.Newline;
        }
        similarResults.setText(similarResultText);

        //set translation result
        TextView translationTextView = (TextView) findViewById(R.id.resultText);
        translationTextView.setText(translatedResult);
    }

    public void updateLanguageChoices(Vector<String> languages) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_layout, languages) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(16);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.originalLanguages_spinner);
        spinner.setAdapter(adapter);
        spinner = (Spinner) findViewById(R.id.destinationLanguages_spinner);
        spinner.setAdapter(adapter);
    }




    private void setSessionButtonText(States appState) {
        if (appState == States.ACTIVE) {
            Button button = (Button) findViewById(R.id.sessionButton);
            button.setText(R.string.button_session_active);
           // Button button1 = (Button) findViewById(R.id.button_using_record);
           // button1.setVisibility(View.INVISIBLE);
        } else {
            Button button = (Button) findViewById(R.id.sessionButton);
            button.setText(R.string.button_session_inactive);
            recognizer.startListening(KWS_SEARCH);
            trigger = false;
            //Button button1 = (Button) findViewById(R.id.button_using_record);
            //button1.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recognizer.cancel();
//        recognizer.shutdown();
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();
        if (text.equals(KEYPHRASE)) {
//            ((TextView) findViewById(R.id.result_text)).setText(text);
            if(trigger!=true) {
                Spinner spinner = (Spinner) findViewById(R.id.originalLanguages_spinner);
                spinner.setSelection(0);
                Spinner spinner1 = (Spinner) findViewById(R.id.destinationLanguages_spinner);
                spinner1.setSelection(1);
                recognizer.cancel();
                recognizer.stop();
                Button button1 = (Button) findViewById(R.id.sessionButton);
                trigger = true;
//                controller = new MainController(this);
                button1.performClick();


            }

        }

    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
//        ((TextView) findViewById(R.id.result_text)).setText("");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName);

    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                        // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .setRawLogDir(assetsDir)

                        // Threshold to tune for keyphrase to balance between false alarms and misses
                .setKeywordThreshold(1e-45f)

                        // Use context-independent phonetic search, context-dependent is too slow for mobile
                .setBoolean("-allphone_ci", true)

                .getRecognizer();
        recognizer.addListener(this);

        /** In your application you might not need to add all those searches.
         * They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
//        recognizer.addKeyphraseSearch(KWS_SEARCH, "stop");


    }



}
