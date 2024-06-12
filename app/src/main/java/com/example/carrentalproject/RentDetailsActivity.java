package com.example.carrentalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RentDetailsActivity extends AppCompatActivity {

    private ImageView carImage;
    private TextView carBrandModel, carPrice, carColor, carStatus, startDateValue, endDateValue, totalCostValue;
    private Button btnRentNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_details);

        // Initialize views
        carImage = findViewById(R.id.car_image);
        carBrandModel = findViewById(R.id.car_brand_model);
        carPrice = findViewById(R.id.car_price);
        carColor = findViewById(R.id.car_color);
        carStatus = findViewById(R.id.car_status);
        startDateValue = findViewById(R.id.start_date_value);
        endDateValue = findViewById(R.id.end_date_value);
        totalCostValue = findViewById(R.id.total_cost_value);
        btnRentNow = findViewById(R.id.btn_rent_now);

        // Get data from intent
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("carImage");
        String brandModel = intent.getStringExtra("carBrandModel");
        int price = intent.getIntExtra("carPrice", 0);
        String color = intent.getStringExtra("carColor");
        String carID = intent.getStringExtra("carID");
        String status = intent.getStringExtra("carStatus");
        String startDate = intent.getStringExtra("startDate");
        String endDate = intent.getStringExtra("endDate");
        //herorrrr
        String customerID= "1000000004";

// Set data to views
        Glide.with(this).load(imagePath).into(carImage);
        carBrandModel.setText(brandModel);
        carPrice.setText("Price: $" + price);
        carColor.setText("Color: " + color);
        carStatus.setText("Status: " + status);
        startDateValue.setText(startDate);
        String d = "hey";
        Log.d("start is: " + startDate, d);
        endDateValue.setText(endDate);

// Calculate total cost
        try {
            if (startDate != null && endDate != null) { // Check if both dates are not null
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                long start = sdf.parse(startDate).getTime();
                long end = sdf.parse(endDate).getTime();
                long duration = end - start;

                if (duration > 0) {
                    long days = TimeUnit.MILLISECONDS.toDays(duration);
                    long totalCost = days * price;
                    totalCostValue.setText("$" + totalCost);
                } else {
                    showError("End date must be after start date");
                }
            } else {
                showError("Start date or end date is missing");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            showError("Error parsing dates");
        }

        btnRentNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://192.168.0.149:80/CarRental/add_rental.php";
                RequestQueue queue = Volley.newRequestQueue(RentDetailsActivity.this);

                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("ServerResponse", response);  // Log the raw response

                                // Check and clean the response if needed
                                try {
                                    if (response.contains("<br") || response.contains("</") || response.contains("<!")) {
                                        // The response contains HTML, which is not valid JSON
                                        response = response.replaceAll("<[^>]*>", ""); // Remove HTML tags
                                    }

                                    // Parse the cleaned response
                                    JSONObject jsonResponse = new JSONObject(response);
                                    int success = jsonResponse.getInt("success");
                                    String message = jsonResponse.getString("message");

                                    if (success == 1) {
                                        Toast.makeText(RentDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RentDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(RentDetailsActivity.this, "JSON parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(RentDetailsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("idNumber", customerID);
                        params.put("carID", String.valueOf(carID));  // Assuming carID is already defined
                        params.put("startDate", startDate);
                        params.put("endDate", endDate);
                        String totalPriceString = totalCostValue.getText().toString().substring(1);
                        int totalPrice = Integer.parseInt(totalPriceString);
                        params.put("totalPrice", String.valueOf(totalPrice));

                        Log.d("RentalParams", "idNumber: " + params.get("idNumber"));
                        Log.d("RentalParams", "carID: " + params.get("carID"));
                        Log.d("RentalParams", "startDate: " + params.get("startDate"));
                        Log.d("RentalParams", "endDate: " + params.get("endDate"));
                        Log.d("RentalParams", "totalPrice: " + params.get("totalPrice"));

                        return params;
                    }
                };

                queue.add(request);
            }
        });
    }

        private void showError(String message) {
        totalCostValue.setText(message);
        totalCostValue.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

    }
}
