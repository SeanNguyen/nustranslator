package sg.edu.nus.nustranslator.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.net.Uri;
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

import junit.framework.Assert;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import sg.edu.nus.nustranslator.R;
import sg.edu.nus.nustranslator.controllers.MainController;
import sg.edu.nus.nustranslator.models.States;
import sg.edu.nus.nustranslator.ultis.Configurations;

public class MainActivity extends Activity {

    //attributes
    private MainController controller;
    private View loadingView;
    private Spinner originalLanguageSpinner;
    private Spinner destinationLanguageSpinner;
    private String bestResult;
    private String similarResultText;
    private String translatedResult;
    MediaPlayer mp = null;
    private boolean translateState = false;

    //events
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.loadingView = findViewById(R.id.main_loading);
        this.originalLanguageSpinner = (Spinner) findViewById(R.id.originalLanguages_spinner);

        translateState = false;
        bestResult = "";
        similarResultText = "";
        translatedResult = "";

        mp = new MediaPlayer();
//        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//
//            @Override
//            public void onPrepared(MediaPlayer player) {
//                player.start();
//            }
//
//        });

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
        if (mp != null) {
            mp.reset();
            mp.release();
        }
        if (bestResult != ""&&language.equals("mandarin")){
            if(controller.mandainList.contains(bestResult)){
                index = controller.mandainList.indexOf(bestResult);

                String musicname = "";
                if(language.equals("mandarin")){
//                    playMandarin(bestResult);
                    musicname = "m"+bestResult.toLowerCase().replaceAll(" ", "")+".mp3";
                }else{
//                    playEnglish(bestResult);
                    musicname = bestResult.toLowerCase().replaceAll(" ", "")+".mp3";
                }
                mp = new MediaPlayer();
                try {

                    AssetFileDescriptor descriptor = this.getAssets().openFd(musicname);
                    mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                    descriptor.close();

                    mp.prepare();

//                    mp.prepareAsync();
                    mp.setVolume(10f, 10f);
                    mp.setLooping(false);
                    mp.start();
                } catch (IllegalStateException e) {

                    e.printStackTrace();

                } catch (IOException e) {

                    e.printStackTrace();

                }



            }
        }
        if(index >0){

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

    public void updateSpeechRecognitionResult(Vector<String> results, String translatedResultTemp) {
        TextView topResult = (TextView) findViewById(R.id.firstResult);
        TextView similarResults = (TextView) findViewById(R.id.otherResults);
        TextView translationTextView = (TextView) findViewById(R.id.resultText);

        if (results == null) {
            topResult.setText(bestResult);
            similarResults.setText(similarResultText);
            translationTextView.setText(translatedResult);
            return;
        }
        //Set speech recognition result

        if (results.size() == 0) {
//            topResult.setText("");
//            similarResults.setText("");
            return;
        }
        String topResultTemp = results.get(0);

        String similarResultTextTemp = "";
        if(results.size()>1) {
            for (int i = 1; i < results.size(); i++) {
                similarResultTextTemp += results.get(i) + Configurations.Newline;
            }
        }


        if (topResultTemp.equals("Translation Start")) {
//                controller.speechRecognizer.startListen();
            translateState = true;
            bestResult = topResultTemp;
            similarResultText = similarResultTextTemp;
            translatedResult = translatedResultTemp;

            topResult.setText(bestResult);
            similarResults.setText(similarResultText);
            translationTextView.setText(translatedResult);
            controller.textTospeechTemp("Translation Start");
            return;
        } else if (topResultTemp.equals("Translation End")) {
//                controller.speechRecognizer.stopListen();
            translateState = false;
            bestResult = topResultTemp;
            similarResultText = similarResultTextTemp;
            translatedResult = translatedResultTemp;

            topResult.setText(bestResult);
            similarResults.setText(similarResultText);
            translationTextView.setText(translatedResult);
            controller.textTospeechTemp("Translation End");
            return;
        }
        if (translateState) {

            bestResult = topResultTemp;
            similarResultText = similarResultTextTemp;
            translatedResult = translatedResultTemp;

            playMp3(controller.appModel.destinationLanguage.toLowerCase());


            topResult.setText(bestResult);
            similarResults.setText(similarResultText);


            translationTextView.setText(translatedResult);

        }else{
            if(!bestResult.equals(null)) {

                topResult.setText(bestResult);
                similarResults.setText(similarResultText);
                translationTextView.setText(translatedResult);
            }
        }


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
            //Button button1 = (Button) findViewById(R.id.button_using_record);
            //button1.setVisibility(View.VISIBLE);
        }
    }



}