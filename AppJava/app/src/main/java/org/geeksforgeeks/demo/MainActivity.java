package org.geeksforgeeks.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private TextView textView;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        Button button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);

        // Initialize request queue for Volley
        requestQueue = Volley.newRequestQueue(this);

        button.setOnClickListener(v -> {
            // Get user input
            String pinCode = editText.getText().toString().trim();

            // Check whether user input is empty
            if (TextUtils.isEmpty(pinCode)) {
                Toast.makeText(this, "Please enter a valid pin code", Toast.LENGTH_SHORT).show();
            } else {
                getDataFromPinCode(pinCode);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getDataFromPinCode(String pinCode) {
        // Clear cache before request
        requestQueue.getCache().clear();

        // URL to fetch data
        String url = "http://www.postalpincode.in/api/pincode/" + pinCode;

        // Create object request using Volley
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        // Check whether pin code is valid
                        if (!response.has("Status") || !"Success".equals(response.getString("Status"))) {
                            textView.setText("Pin code is not valid.");
                            return;
                        }

                        // Get data from response
                        JSONArray postOfficeArray = response.getJSONArray("PostOffice");
                        if (postOfficeArray.length() == 0) {
                            textView.setText("No details available for this pin code.");
                            return;
                        }

                        // Extract data from response
                        JSONObject obj = postOfficeArray.getJSONObject(0);
                        String district = obj.getString("District");
                        String state = obj.getString("State");
                        String country = obj.getString("Country");

                        // Display data in text view
                        textView.setText("Details of pin code:\n" +
                                "District: " + district + "\n" +
                                "State: " + state + "\n" +
                                "Country: " + country);

                    } catch (JSONException e) {
                        // Handle JSON exception
                        e.printStackTrace();
                        textView.setText("Error parsing data.");
                    }
                },
                error -> {
                    // Handle Volley error
                    textView.setText("Error fetching data: \n" + error.getMessage());
                }
        );

        // Add object request to request queue
        requestQueue.add(objectRequest);
    }
}
