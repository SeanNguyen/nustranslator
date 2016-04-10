package sg.edu.nus.nustranslator.controllers;

import android.content.Context;
import android.media.AudioRecord;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import sg.edu.nus.nustranslator.ui.MainActivity;
import sg.edu.nus.nustranslator.models.AppModel;
import sg.edu.nus.nustranslator.recognizers.ISpeechRecognizer;
import sg.edu.nus.nustranslator.recognizers.LocalSpeechRecognizer;
import sg.edu.nus.nustranslator.utils.Configurations;


public class MainController {
    //private Streamer audioStreamer = new AudioStreamer();
    //private Streamer textStreamer = new TextStreamer();
    //private AudioRecord recorder;

    private AppModel mAppModel;


    private Timer resetTimer = new Timer();
    private Timer resetTimer1 = new Timer();
    private TimerTask resetTimerTask;
    private TimerTask resetTimerTask1;


    public MainController(Context context) {
        mAppModel = AppModel.getInstance(context);

    }


    private void resetMandarin( ) {
        this.resetTimer1.cancel();
        this.resetTimerTask1 = new TimerTask() {
            @Override
            public void run() {
                    //resetSpeechRecognizer();
            }
        };
        this.resetTimer1 = new Timer();
        this.resetTimer.schedule(resetTimerTask1, 300);
    }

    private void resetTimer( ) {
        this.resetTimer.cancel();
        this.resetTimerTask = new TimerTask() {
            @Override
            public void run() {


                //mSpeechRecognizer.stopListen();

                /*
                String currentString = "";
                if(bestResultCurrent.toLowerCase().equals("translation start")){
                    currentString = "Translation Start";
                }else if (bestResultCurrent.toLowerCase().equals("translation end")){
                    currentString = "Translation End";
                }else{
                    currentString = translatedResult;
                    if(mAppModel.destinationLanguage.toLowerCase().equals("english")){
                        currentString = translatedResult;
                    }
                }

                if(!currentString.equals("")) {

                    HashMap<String, String> text2SpeechParas = new HashMap<>();
                    text2SpeechParas.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, currentString);
                    mTextToSpeech.speak(currentString, TextToSpeech.QUEUE_FLUSH, text2SpeechParas);

//                }
                }*/

            }
        };
        this.resetTimer = new Timer();
        this.resetTimer.schedule(resetTimerTask, Configurations.UX_resetTime);
    }

    public void updateData() {
        AsyncTask<Void, Void, Void> updateTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... args) {
                //dataUtils.updateData(mAppModel, mainActivity);
                return null;
            }

            @Override
            protected void onPostExecute(Void args) {
                //onFinishLoading();
            }
        };
        updateTask.execute();
    }


    public void setOriginalLanguage(final int index) {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                if (index < 0 || index >= mAppModel.getNumberOfLanguage()) {
                   // mAppModel.setOriginalLanguage(Configurations.Empty);
                } else {
                    String language = mAppModel.getAllLanguages().get(index);
                    //mAppModel.setOriginalLanguage(language);
                    //speechRecognizer.setInputLanguage(language, mainActivity);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                //onFinishLoading();
            }
        };
        asyncTask.execute();
    }

    public void setDestinationLanguage(int index) {
        if (index < 0 || index >= this.mAppModel.getNumberOfLanguage()) {
            //this.mAppModel.setDestinationLanguage(Configurations.Empty);
        } else {
            String language = this.mAppModel.getAllLanguages().get(index);
            //this.mAppModel.setDestinationLanguage(language);
        }
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
//                while (mAppModel.getAppState() == States.ACTIVE) {
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


//    private Vector<String> getTopResults(String input) {
//        String originalLanguage = mAppModel.getOriginalLanguage();
//        Vector<String> sentences = mAppModel.getSentencesByLanguageName(originalLanguage);
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

}
