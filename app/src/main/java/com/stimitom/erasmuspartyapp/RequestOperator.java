package com.stimitom.erasmuspartyapp;
import com.stimitom.erasmuspartyapp.ModelPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestOperator extends Thread {

    interface RequestOperatorListener {
        void success(ModelPost publication);
        void failed(int responseCode);
    }

    private RequestOperatorListener listener;
    private int responseCode;

    public void setListener(RequestOperatorListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        super.run();
        try {
            ModelPost publication = request();
            if (publication != null) success(publication);
            else failed(responseCode);
        } catch (IOException e) {
            failed(-1);
        } catch (JSONException e) {
            failed(-2);
        }
    }

    private ModelPost request() throws IOException, JSONException {
        URL obj = new URL("http://jsonplaceholder.typicode.com/posts/1");

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        con.setRequestProperty("Content-Type", "application/json");

        responseCode = con.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        InputStreamReader streamReader;

        if (responseCode == 200) {
            streamReader = new InputStreamReader(con.getInputStream());
        } else {
            streamReader = new InputStreamReader((con.getErrorStream()));
        }

        BufferedReader in = new BufferedReader(streamReader);
        String inputLine;
        StringBuffer response = new StringBuffer();


        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println(response.toString());

        if (responseCode == 200) return parsingJsonObject(response.toString());
        else return null;
    }

    public ModelPost parsingJsonObject(String response) throws JSONException {

        JSONObject object = new JSONObject(response);
        ModelPost post = new ModelPost();

        post.setId(object.optInt("id", 0));
        post.setUserId(object.optInt("userId", 0));
        post.setTitle(object.getString("title"));
        post.setBodyText(object.getString("body"));

        return post;
    }

    private void failed(int code) {
        if (listener != null) listener.failed(code);
    }

    private void success(ModelPost publication) {
        if (listener != null) listener.success(publication);
    }

}

