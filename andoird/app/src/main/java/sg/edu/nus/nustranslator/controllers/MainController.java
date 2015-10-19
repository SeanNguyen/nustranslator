package sg.edu.nus.nustranslator.controllers;

import android.media.AudioRecord;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import net.java.frej.fuzzy.Fuzzy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import sg.edu.nus.nustranslator.activities.MainActivity;
import sg.edu.nus.nustranslator.data.DataController;
import sg.edu.nus.nustranslator.models.AppModel;
import sg.edu.nus.nustranslator.models.States;
import sg.edu.nus.nustranslator.recognizers.ISpeechRecognizer;
import sg.edu.nus.nustranslator.recognizers.LocalSpeechRecognizer;
import sg.edu.nus.nustranslator.ultis.Configurations;

/**
 * Created by Storm on 3/5/2015.
 */
public class MainController implements TextToSpeech.OnUtteranceCompletedListener {

    //attributes
    public AppModel appModel = AppModel.getInstance();
    public ISpeechRecognizer speechRecognizer;
    public DataController dataController = new DataController();
    //private Streamer audioStreamer = new AudioStreamer();
    //private Streamer textStreamer = new TextStreamer();
    private MainActivity mainActivity;
    public ArrayList<String> mandainList = new ArrayList();

    private AudioRecord recorder;

    private String lastRecognitionUpdate;
    private Timer resetTimer = new Timer();
    private Timer resetTimer1 = new Timer();
    private TimerTask resetTimerTask;
    private TimerTask resetTimerTask1;
//    private long startTime = 0;

    private TextToSpeech textToSpeech;
    private String translatedResult;

    private String bestResultCurrent;

    private boolean translateState = false;

    //constructor
    public MainController(MainActivity context) {
        this.mainActivity = context;
        this.speechRecognizer = new LocalSpeechRecognizer(context, this);
        this.mandainList.addAll(this.dataController.deserializeData(appModel, context));
        mainActivity.onFinishLoading();
        mainActivity.updateLanguageChoices(appModel.getAllLanguages());

        this.textToSpeech = new TextToSpeech(this.mainActivity.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
        this.textToSpeech.setOnUtteranceCompletedListener(this);
    }

    //Public interface methods
    @Override
    public void onUtteranceCompleted(String utteranceId) {
        if (utteranceId.equals(this.translatedResult)||appModel.destinationLanguage.toLowerCase().equals("mandarin")) {
            this.resetSpeechRecognizer();
            trigger = false;
        }
    }

    //Public methods
    public States changeState() {
        States state = appModel.getAppState();
        if (state == States.ACTIVE) {
            appModel.setAppState(States.INACTIVE);
            this.speechRecognizer.stopListen();
            //this.audioStreamer.stopStream();
            //this.textStreamer.stopStream();
        } else {
            appModel.setAppState(States.ACTIVE);
            speechRecognizer.startListen();
            //String timeStamp = Utilities.getTimeStamp();
            //this.audioStreamer.startStream(timeStamp);
            //this.startTime = System.currentTimeMillis() / 1000;
            //this.textStreamer.startStream(timeStamp);
            //sendAudioData();
        }
        return appModel.getAppState();
    }



    public void onSpeechRecognitionResultUpdate(String input) {
        if (this.lastRecognitionUpdate != null && this.lastRecognitionUpdate.equals(input)) {
            return;
        }
//        if (this.lastRecognitionUpdate != null ) {
//            return;
//        }
        Log.e("Speech Partial Result", input);

        //reset timer
//        resetTimer();
        this.lastRecognitionUpdate = input;

        //get results
        String result = isMatch(input);
        if (result =="") {
            return;
        }
//        Vector<String> topResults = getTopResults(input);
//        this.translatedResult = getTranslation(topResults);
        Vector<String> topResults = new Vector<>();
        topResults.add(result);
        this.translatedResult = getTranslation(topResults);

//        bestResultCurrent = topResults.get(0);
        bestResultCurrent = topResults.get(0);


        if(topResults.get(0).toLowerCase().equals("translation start"))
        {
//            HashMap<String, String> text2SpeechParas = new HashMap<>();
//            text2SpeechParas.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "Translation Start");
//            textToSpeech.speak("Translation Start", TextToSpeech.QUEUE_FLUSH, text2SpeechParas);
            translateState = true;
            resetTimer();
//            mainActivity.updateSpeechRecognitionResult(topResults, translatedResult);
        } else if (topResults.get(0).toLowerCase().equals("translation end"))
        {
//            HashMap<String, String> text2SpeechParas = new HashMap<>();
//            text2SpeechParas.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "Translation End");
//            textToSpeech.speak("Translation End", TextToSpeech.QUEUE_FLUSH, text2SpeechParas);
            translateState = false;
            resetTimer();

        }else if (appModel.destinationLanguage.toLowerCase().equals("mandarin")){
            resetMandarin();
        }else{
            resetTimer();
        }
        mainActivity.updateSpeechRecognitionResult(topResults, translatedResult);


//        speechRecognizer.startListen();
        //long timeFromStart = (System.currentTimeMillis() - this.startTime) / 1000;
        //this.textStreamer.sendData(timeFromStart + ": " + input);

        //update UI

    }


    public void updateData() {
        AsyncTask<Void, Void, Void> updateTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... args) {
                dataController.updateData(appModel, mainActivity);
                return null;
            }

            @Override
            protected void onPostExecute(Void args) {
                mainActivity.onFinishLoading();
                mainActivity.updateLanguageChoices(appModel.getAllLanguages());
            }
        };
        updateTask.execute();
    }

    public void setOriginalLanguage(final int index) {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                if (index < 0 || index >= appModel.getNumberOfLanguage()) {
                    appModel.setOriginalLanguage(Configurations.Empty);
                } else {
                    String language = appModel.getAllLanguages().get(index);
                    appModel.setOriginalLanguage(language);
                    speechRecognizer.setInputLanguage(language, mainActivity);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                mainActivity.onFinishLoading();
            }
        };
        asyncTask.execute();
    }

    public void setDestinationLanguage(int index) {
        if (index < 0 || index >= this.appModel.getNumberOfLanguage()) {
            this.appModel.setDestinationLanguage(Configurations.Empty);
        } else {
            String language = this.appModel.getAllLanguages().get(index);
            this.appModel.setDestinationLanguage(language);
        }
    }

    //Private Helper Methods
    private String isMatch(String sentence) {
        sentence = sentence.toLowerCase();
        String originalLanguage = appModel.getOriginalLanguage();
        Vector<String> sentences = appModel.getSentencesByLanguageName(originalLanguage);
        for(int i =0;i<sentences.size();i++) {
//            if (sentences.indexOf(sentence) > -1) {
//                return true;
//            }
            if (sentence.toLowerCase().contains(sentences.elementAt(i).toLowerCase())) {
                return sentences.elementAt(i);
            }
        }
        return "";
    }

//    private Vector<String> getTopResults(String input) {
//        String originalLanguage = appModel.getOriginalLanguage();
//        Vector<String> sentences = appModel.getSentencesByLanguageName(originalLanguage);
//        Vector<String> topResult = new Vector<String>();
//        if (sentences == null) {
//            return topResult;
//        }
//        for (int i = 0; i < sentences.size(); i++) {
//            if (topResult.size() == 0) {
//                String lowerCasedSentence = sentences.get(i).toLowerCase();
//                double similarity = Fuzzy.similarity(input, lowerCasedSentence);
//                if(similarity<=0.8) {
//                    topResult.add(sentences.get(i));
//                }
//            } else {
//                input = input.toLowerCase();
//                String lowerCasedSentence = sentences.get(i).toLowerCase();
//                double similarity = Fuzzy.similarity(input, lowerCasedSentence);
//                for (int j = topResult.size() - 1; j > -1; j--) {
//                    //note: the closer to 0, the more similar
//                    double topResultSimilarity = Fuzzy.similarity(input, topResult.get(j));
//                    boolean isMoreSimilar = similarity < topResultSimilarity;
//                    if (isMoreSimilar && j == 0) {
//                        if(similarity<0.8) {
//                            topResult.insertElementAt(sentences.get(i), j);
//
//                        }
//                        break;
//                    } else if (!isMoreSimilar && j < 4) {
//                        if(similarity<0.8) {
//                            topResult.insertElementAt(sentences.get(i), j + 1);
//
//                        }
//
//                        break;
//                    }
//                }
//            }
//            if (topResult.size() > 5) {
//                topResult.removeElementAt(4);
//            }
//        }
//        return topResult;
//    }

    private void resetSpeechRecognizer() {
        speechRecognizer.stopListen();
        speechRecognizer.startListen();
    }

    private void resetMandarin( ) {
        this.resetTimer1.cancel();
        this.resetTimerTask1 = new TimerTask() {
            @Override
            public void run() {
                    resetSpeechRecognizer();
            }
        };
        this.resetTimer1 = new Timer();
        this.resetTimer.schedule(resetTimerTask1, 300);
    }

    private int count = 0;
    private void resetTimer( ) {
        this.resetTimer.cancel();
        this.resetTimerTask = new TimerTask() {
            @Override
            public void run() {


                speechRecognizer.stopListen();


                String currentString = "";
                if(bestResultCurrent.toLowerCase().equals("translation start")){
                    currentString = "Translation Start";
                }else if (bestResultCurrent.toLowerCase().equals("translation end")){
                    currentString = "Translation End";
                }else{
                    currentString = translatedResult;
                    if(appModel.destinationLanguage.toLowerCase().equals("english")){
                        currentString = translatedResult;
                    }
                }

//                if(appModel.destinationLanguage.toLowerCase().equals("mandarin")&&(!trigger)){
//                    textToSpeech.playSilence(300, TextToSpeech.QUEUE_FLUSH, null);
//                }else{
                if(!currentString.equals("")) {

                    HashMap<String, String> text2SpeechParas = new HashMap<>();
                    text2SpeechParas.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, currentString);
                    textToSpeech.speak(currentString, TextToSpeech.QUEUE_FLUSH, text2SpeechParas);
                    trigger = true;
//                }
                }

            }
        };
        this.resetTimer = new Timer();
        this.resetTimer.schedule(resetTimerTask, Configurations.UX_resetTime);
    }

    public boolean trigger = false;

//    public void textTospeechTemp(String text){
//        HashMap<String, String> text2SpeechParas = new HashMap<>();
//        text2SpeechParas.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text);
//        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, text2SpeechParas);
//        trigger = true;
//    }
    private String getTranslation(Vector<String> inputs) {
        if (inputs == null || inputs.size() == 0) {
            return "";
        }
        String result;
        String destinationLanguage = this.appModel.getDestinationLanguage();
        return appModel.getTranslation(inputs.firstElement());
    }

//    private byte[] getAudioFromText(String text) {
//        String AUDIO_CHINESE= "http://www.translate.google.com/translate_tts?tl=zh&q=";
//        String AUDIO_ENGLISH = "http://www.translate.google.com/translate_tts?tl=en&q=";
//
//        try {
//            URL url = new URL(AUDIO_ENGLISH + text);
//            DataInputStream in;
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
//
//            if (urlConnection.getResponseCode() == 200) {
//                //get byte array in response
//                in = new DataInputStream(urlConnection.getInputStream());
//            } else {
//                in = new DataInputStream(urlConnection.getErrorStream());
//            }
//
//            //convert input stream to byte array
//            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//            int nRead;
//            byte[] data = new byte[16384];
//
//            while ((nRead = in.read(data, 0, data.length)) != -1) {
//                buffer.write(data, 0, nRead);
//            }
//            buffer.flush();
//            byte[] bytes = buffer.toByteArray();
//
//            //close connection
//            in.close();
//            urlConnection.disconnect();
//            return bytes;
//        } catch (Exception e) {
//
//        }
//        return null;
//    }

//    private void sendAudioData() {
//        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
//                Configurations.Recorder_sampleRate,
//                Configurations.Recorder_channelConfig,
//                Configurations.Recorder_audioFormat,
//                Configurations.Recorder_minBuffSize * 10);
//        recorder.startRecording();
//
//        AsyncTask asyncTask = new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] params) {
//                byte[] buffer = new byte[Configurations.Recorder_minBuffSize];
//                while (appModel.getAppState() == States.ACTIVE) {
//                    recorder.read(buffer, 0, buffer.length);
//                    System.out.println(buffer);
//                    //this.audioStreamer.sendData(buffer);
//                }
//                recorder.stop();
//                recorder.release();
//                return null;
//            }
//        };
//        asyncTask.execute();
//    }
}
