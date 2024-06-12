package com.example.carrentalproject;

import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.bumptech.glide.Glide; // Import Glide library
import java.util.List;
import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private List<Car> carList;
    private Context context;
    private String startDate;
    private String endDate;
    String customerID;

    public CarAdapter( List<Car> carList) {
        this.carList = carList;
    }
    public CarAdapter(List<Car> carList, String startDate, String endDate, String customerID) {
        this.carList = carList;
        this.startDate = startDate;
        this.endDate = endDate;
        this.customerID = customerID;
    }
    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carList.get(position);

        holder.carBrandModel.setText(car.getCarBrand() + " " + car.getCarModel());
        holder.carPrice.setText("$" + car.getPrice());
        holder.carColor.setText("Color: " + car.getColor());
        holder.carStatus.setText("Status: " + car.getStatus());

        // Load image using Glide
        String imageUrl = car.getImagePath();
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .into(holder.carImage);

        // Set OnClickListener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to start RentDetailsActivity
                Intent intent = new Intent(holder.itemView.getContext(), RentDetailsActivity.class);

                // Pass car details to intent
                intent.putExtra("carImage", car.getImagePath());
                intent.putExtra("carBrandModel", car.getCarBrand() + " " + car.getCarModel());
                intent.putExtra("carPrice", car.getPrice());
                intent.putExtra("carColor", car.getColor());
                intent.putExtra("carStatus", car.getStatus());
                intent.putExtra("carID", String.valueOf(car.getCarID()));

                // Pass start date and end date to intent
                intent.putExtra("startDate", startDate);
                intent.putExtra("endDate", endDate);

                // Pass customerID to intent
                intent.putExtra("customerID", customerID);

                // Start RentDetailsActivity
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return carList.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {

        ImageView carImage;
        TextView carBrandModel, carPrice, carColor, carStatus;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carImage = itemView.findViewById(R.id.car_image);
            carBrandModel = itemView.findViewById(R.id.car_brand_model);
            carPrice = itemView.findViewById(R.id.car_price);
            carColor = itemView.findViewById(R.id.car_color);
            carStatus = itemView.findViewById(R.id.car_status);
        }
    }
}
