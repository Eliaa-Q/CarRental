package com.example.carrentalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class RentalAdapter extends RecyclerView.Adapter<RentalAdapter.RentalViewHolder> {
    private List<RentalRequest> rentalList;
    private Context context;

    public RentalAdapter(List<RentalRequest> rentalList, Context context) {
        this.rentalList = rentalList;
        this.context = context;
    }

    @NonNull
    @Override
    public RentalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rental_list_item, parent, false);
        return new RentalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RentalViewHolder holder, int position) {
        RentalRequest rental = rentalList.get(position);

        holder.rentalID.setText(String.valueOf(rental.getRentalID()));
        holder.idNumber.setText(String.valueOf(rental.getIdNumber()));
        holder.carID.setText(String.valueOf(rental.getCarID()));
        holder.startDate.setText(rental.getStartDate());
        holder.endDate.setText(rental.getEndDate());
        holder.totalPrice.setText(String.valueOf(rental.getTotalPrice()));
        holder.status.setText(rental.getStatus());

        // Show delete button only for Pending or Confirmed rentals
        if (rental.getStatus().equals("Pending") || rental.getStatus().equals("Confirmed")) {
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }

        // Handle delete button click
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call API to delete rental
                deleteRental(rental.getRentalID());
            }
        });
    }

    @Override
    public int getItemCount() {
        return rentalList.size();
    }

    private void deleteRental(int rentalID) {
        String url = "http://192.168.0.149:80/CarRental/delete_rental.php";

        // Create a request queue
        RequestQueue queue = Volley.newRequestQueue(context);

        // Create a string request to delete the rental
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle response from server
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt("success");
                            String message = jsonObject.getString("message");
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            if (success == 1) {
                                // Rental deleted successfully, you can update the UI or refresh the list
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(context, "Failed to delete rental", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("rentalID", String.valueOf(rentalID));
                return params;
            }
        };

        // Add the request to the queue
        queue.add(request);
    }


    public static class RentalViewHolder extends RecyclerView.ViewHolder {
        TextView rentalID, idNumber, carID, startDate, endDate, totalPrice, status;
        Button btnDelete;

        public RentalViewHolder(@NonNull View itemView) {
            super(itemView);
            rentalID = itemView.findViewById(R.id.rentalID);
            idNumber = itemView.findViewById(R.id.idNumber);
            carID = itemView.findViewById(R.id.carID);
            startDate = itemView.findViewById(R.id.startDate);
            endDate = itemView.findViewById(R.id.endDate);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            status = itemView.findViewById(R.id.status);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
