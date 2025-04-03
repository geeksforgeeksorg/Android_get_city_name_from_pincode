package org.geeksforgeeks.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.*
import org.json.JSONException

class MainActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var button: Button
    private lateinit var textView: TextView

    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)
        button = findViewById(R.id.button)
        textView = findViewById(R.id.textView)

        // Initialize request queue for volley
        requestQueue = Volley.newRequestQueue(this)

        button.setOnClickListener {
            // get user input
            val pinCode = editText.text.toString().trim()

            // check whether user input is empty
            if (TextUtils.isEmpty(pinCode)) {
                Toast.makeText(this, "Please enter a valid pin code", Toast.LENGTH_SHORT).show()
            } else {
                getDataFromPinCode(pinCode)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getDataFromPinCode(pinCode: String) {
        // Clear cache before request
        requestQueue.cache.clear()

        // url to fetch data
        val url = "http://www.postalpincode.in/api/pincode/$pinCode"

        // create object request using volley
        val objectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    // check whether pin code is valid
                    if (!response.has("Status") || response.getString("Status") != "Success") {
                        textView.text = "Pin code is not valid."
                        return@Listener
                    }

                    // get data from response
                    val postOfficeArray = response.getJSONArray("PostOffice")
                    if (postOfficeArray.length() == 0) {
                        textView.text = "No details available for this pin code."
                        return@Listener
                    }

                    // extract data from response
                    val obj = postOfficeArray.getJSONObject(0)
                    val district = obj.getString("District")
                    val state = obj.getString("State")
                    val country = obj.getString("Country")

                    // display data in text view
                    textView.text = """
                        Details of pin code:
                        District: $district
                        State: $state
                        Country: $country
                    """.trimIndent()

                } catch (e: JSONException) {
                    // handle json exception
                    e.printStackTrace()
                    textView.text = "Error parsing data."
                }
            }
        ) { error ->
            // handle volley error
            textView.text = "Error fetching data: \n${error.message}"
        }

        // add object request to request queue
        requestQueue.add(objectRequest)
    }
}
