package com.example.carrentalproject;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CustomersPage extends AppCompatActivity implements Response.Listener<JSONArray> {

    private Button startdatebtn, enddatebtn, filterButton, historyButton;
    private TextView startdatetxt, enddatetxt;
    private Spinner brandSpinner;
    public Calendar startDateCalendar, endDateCalendar;
    private DatePickerDialog.OnDateSetListener startDateListener, endDateListener;
    private List<Car> carList;
    private CarAdapter carAdapter;
    private RecyclerView recyclerView;
    private String selectedBrand = ""; // Initialize selectedBrand
    private String customerID; // Variable to store customerID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_page);
        customerID = getIntent().getStringExtra("customerID");
        System.out.println("heyyyy"+customerID);
        Intent intent = new Intent(CustomersPage.this, RentalHistoryActivity.class);

        intent.putExtra("customerID", customerID);
        Intent intent2 = new Intent(CustomersPage.this, RentDetailsActivity.class);

        intent2.putExtra("customerID", customerID);

        // Initialize views
        startdatetxt = findViewById(R.id.startdatetxt);
        enddatetxt = findViewById(R.id.enddatetxt);
        startdatebtn = findViewById(R.id.startdatebtn);
        enddatebtn = findViewById(R.id.enddatebtn);
        filterButton = findViewById(R.id.filterButton);
        brandSpinner = findViewById(R.id.brandSpinner);
        historyButton = findViewById(R.id.history_btn);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        carList = new ArrayList<>();
        carAdapter = new CarAdapter(carList);
        recyclerView.setAdapter(carAdapter);

        // Initialize Calendar instances
        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();

        // Restore state if available
        if (savedInstanceState != null) {
            startdatetxt.setText(savedInstanceState.getString("startDate"));
            enddatetxt.setText(savedInstanceState.getString("endDate"));
            selectedBrand = savedInstanceState.getString("selectedBrand");
            // Set the spinner to the saved selection
            int spinnerPosition = ((ArrayAdapter) brandSpinner.getAdapter()).getPosition(selectedBrand);
            brandSpinner.setSelection(spinnerPosition);
        }

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(intent);

            }
        });

        // Initialize listeners for start date and end date
        startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDateCalendar.set(YEAR, year);
                startDateCalendar.set(MONTH, month);
                startDateCalendar.set(DAY_OF_MONTH, dayOfMonth);
                updateStartDate();
            }
        };

        endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDateCalendar.set(YEAR, year);
                endDateCalendar.set(MONTH, month);
                endDateCalendar.set(DAY_OF_MONTH, dayOfMonth);
                updateEndDate();
            }
        };

        // Set OnClickListener for start date button
        startdatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CustomersPage.this, startDateListener,
                        startDateCalendar.get(YEAR),
                        startDateCalendar.get(MONTH),
                        startDateCalendar.get(DAY_OF_MONTH));
                // Set the minimum date to today
                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                datePickerDialog.show();
            }
        });

        // Set OnClickListener for end date button
        enddatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CustomersPage.this, endDateListener,
                        // Set OnClickListener for end date button (continued)
                        endDateCalendar.get(YEAR),
                        endDateCalendar.get(MONTH),
                        endDateCalendar.get(DAY_OF_MONTH));
                // Set the minimum date to today
                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                datePickerDialog.show();
            }
        });

        // Populate brand spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.car_brands, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(adapter);

        // Set OnItemSelectedListener for brand spinner
        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBrand = (String) parent.getItemAtPosition(position);
                if (selectedBrand.equalsIgnoreCase("All")) {
                    selectedBrand = ""; // Empty string for no brand filter
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedBrand = ""; // Empty string for no brand filter
            }
        });

        // Set OnClickListener for filter button
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCarData();
            }
        });

        // Fetch initial car data
        fetchCarData();
    }

    // Method to update start date text view
    private void updateStartDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        startdatetxt.setText(sdf.format(startDateCalendar.getTime()));
    }

    // Method to update end date text view
    private void updateEndDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        enddatetxt.setText(sdf.format(endDateCalendar.getTime()));
    }

    private void fetchCarData() {
        String url = "http://192.168.0.149:80/CarRental/fetch_cars.php";
        String baseUrl = "http://192.168.0.149:80/CarRental/images/"; // Base URL for images

        // Add query parameters for filtering
        String startDate = startdatetxt.getText().toString();
        String endDate = enddatetxt.getText().toString();
        String brandFilter = selectedBrand.isEmpty() ? "" : selectedBrand;

        url += "?startDate=" + startDate + "&endDate=" + endDate + "&brand=" + brandFilter;

        // Create a request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create a request to fetch JSON data from the URL
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            try {
                                // Parse the JSON response and populate carList
                                carList = new ArrayList<>(); // Clear existing data
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject object = response.getJSONObject(i);
                                    int carID = object.getInt("carID"); // Fetch carID

                                    String carBrand = object.getString("carBrand");
                                    String carModel = object.getString("carModel");
                                    int price = object.getInt("price");
                                    String color = object.getString("color");
                                    String status = object.getString("status");
                                    String image = object.getString("image");

                                    // Create a new Car object and add it to carList
                                    Car car = new Car(carID, carBrand, carModel, price, color, status, baseUrl + image);
                                    carList.add(car);
                                }

                                // Update the adapter with new data
                                carAdapter = new CarAdapter(carList, startdatetxt.getText().toString(), enddatetxt.getText().toString(), customerID);
                                recyclerView.setAdapter(carAdapter);
                                carAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("CarFetcher", "Error parsing JSON response", e);
                                // Handle parsing error (e.g., display a toast message)
                            }
                        } else {
                            Log.w("CarFetcher", "Empty JSON response from server");
                            // Handle empty response (e.g., display a message indicating no cars found)
                            carList.clear(); // Clear the existing data
                            carAdapter.notifyDataSetChanged(); // Notify the adapter of the change
                            Toast.makeText(CustomersPage.this, "No cars found", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("CarFetcher", "Error fetching car data", error);
                        // Handle network or server error (e.g., display a toast message)
                        Toast.makeText(CustomersPage.this, "Error fetching car data", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue
        queue.add(request);
    }


    @Override
    public void onResponse(JSONArray jsonArray) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current state
        outState.putString("startDate", startdatetxt.getText().toString());
        outState.putString("endDate", enddatetxt.getText().toString());
        outState.putString("selectedBrand", selectedBrand);
    }
}
