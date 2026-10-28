package com.comp713.petclient;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

    private TextView tvResult;
    private final ExecutorService worker = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);
        Button btnLoadPets = findViewById(R.id.btnLoadPets);

        btnLoadPets.setOnClickListener(v -> loadPets());
    }

    private void loadPets() {
        tvResult.setText("Loading...");

        worker.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/pet-system/api/pets/search?species=cat");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode(); // 把获取状态码放在 try 块里

                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    String jsonResult = sb.toString();
                    runOnUiThread(() -> tvResult.setText("Server Response:\n" + jsonResult));
                } else {
                    runOnUiThread(() -> tvResult.setText("Error: HTTP " + responseCode));
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e("NetworkError", e.toString());
                runOnUiThread(() -> tvResult.setText("Network Error: " + e.getMessage()));
            }
        });
    }
}