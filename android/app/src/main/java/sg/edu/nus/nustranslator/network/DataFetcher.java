package sg.edu.nus.nustranslator.network;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import sg.edu.nus.nustranslator.AppModel;
import sg.edu.nus.nustranslator.Configurations;


public class DataFetcher {
    public DataFetcher() {
    }

    public boolean fetchData(AppModel model) {
        try {
            String jsonText;
            jsonText = querySentences();
            return parseJsonSentences(model, jsonText);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String queryDict(String language) {
        String url = "http://" + Configurations.Server_address + ":" + Configurations.Server_port + "/api/sync/dict";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        try {
            // Add your
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("language", language));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            return EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public String queryLanguageModel(String language) {
        String url = "http://" + Configurations.Server_address + ":" + Configurations.Server_port + "/api/sync/language_model";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        try {
            // Add your
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("language", language));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            return EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
    //private helper methods

    private boolean parseJsonSentences(AppModel model, String input) throws JSONException {
        JSONObject jsonObject = new JSONObject(input);
        String version = jsonObject.getString("version");
        int dataVersion = Integer.parseInt(version);
        if (model.getDataVersion() >= dataVersion) {
            Log.e("FETCH DATA", "Data is up to date");
                return true;
        }
        model.resetModel();
        JSONArray arr = jsonObject.getJSONArray("data");
        int numberOfPair = 0;
        for (int i = 0; i < arr.length(); i++) {
            String language = arr.getJSONObject(i).getString("language");
            String sentences = arr.getJSONObject(i).getString("sentences");
            ArrayList<String> sentenceList = new ArrayList<>(Arrays.asList(sentences.split(Configurations.Newline)));
            model.addLanguage(language, sentenceList);
            numberOfPair = sentenceList.size();
        }
        model.setNumPairs(numberOfPair);
        model.setDataVersion(dataVersion);
        return true;
    }

    private String querySentences() throws Exception{
        String url = "http://" + Configurations.Server_address + ":" + Configurations.Server_port + "/api/sync/sentences";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

}
