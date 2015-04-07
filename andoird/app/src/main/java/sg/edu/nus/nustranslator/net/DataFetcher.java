package sg.edu.nus.nustranslator.net;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Vector;

import sg.edu.nus.nustranslator.models.AppModel;
import sg.edu.nus.nustranslator.ultis.Configurations;

/**
 * Created by Storm on 3/11/2015.
 */
public class DataFetcher {
    public DataFetcher() {
    }

    public boolean fetchData(AppModel model) {
        try {
            String jsonText;
            jsonText = queryFromServer();
            return parseJson(model, jsonText);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean parseJson(AppModel model, String input) throws JSONException {
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
            Vector<String> sentenceList = new Vector(Arrays.asList(sentences.split(Configurations.Newline)));
            model.addLanguage(language, sentenceList);
            numberOfPair = sentenceList.size();
        }
        model.setNumberOfPair(numberOfPair);
        model.setDataVersion(dataVersion);
        return true;
    }

    private String queryFromServer() throws Exception{
        String url = "http://" + Configurations.Server_address + ":" + Configurations.Server_port + "/api/sync";
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
