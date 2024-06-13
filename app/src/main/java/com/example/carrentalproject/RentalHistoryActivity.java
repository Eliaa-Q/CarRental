package com.example.carrentalproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class RentalHistoryActivity extends AppCompatActivity {

    private RecyclerView rentalRecyclerView;
    private RentalAdapter rentalAdapter;
    private List<Rental> rentalList;
    private int customerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_history);

        // Initialize views
        rentalRecyclerView = findViewById(R.id.RentalRecyclerView);
        rentalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rentalList = new ArrayList<>();
        rentalAdapter = new RentalAdapter(rentalList, this);
        rentalRecyclerView.setAdapter(rentalAdapter);

        // Retrieve customerID from intent
        customerID = Integer.parseInt(getIntent().getStringExtra("customerID"));
        //customerID = 1000000004;
        if (customerID != -1) {
            fetchRentalHistory(customerID);
        } else {
            Toast.makeText(this, "Invalid customer ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchRentalHistory(int customerID) {
        String url = "http://192.168.0.149:80/CarRental/fetch_rentals.php?customerID=" + customerID;

        // Create a request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create a request to fetch JSON data from the URL
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            String responseString = response.toString(); // Convert JSON array to String
                            Log.d("RentalHistory", "Server Response: " + responseString);

                            rentalList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);

                                int rentalID = obj.getInt("rentalID");
                                int idNumber = obj.getInt("idNumber");
                                int carID = obj.getInt("carID");
                                String startDate = obj.getString("startDate");
                                String endDate = obj.getString("endDate");
                                String totalPrice = obj.getString("totalPrice");
                                String status = obj.getString("status");

                                // Create a RentalRequest object and add it to the list
                                Rental rental = new Rental(rentalID, idNumber, carID, startDate, endDate, Double.parseDouble(totalPrice), status);
                                rentalList.add(rental);
                            }
                            rentalAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e("RentalHistory", "Error parsing JSON response", e);
                            Toast.makeText(RentalHistoryActivity.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() { // Error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("RentalHistory", "Error fetching rental history", error);
                        Toast.makeText(RentalHistoryActivity.this, "Error fetching rental history", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue
        queue.add(request);
    }
}