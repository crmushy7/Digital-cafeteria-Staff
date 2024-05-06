package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.dtcsstaff.R;

import java.util.List;

public class FoodAdapter  extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<FoodSetGet> foods;
    private OnItemClickListener mListener;
    private boolean clickable = true;

    public FoodAdapter(List<FoodSetGet> foods) {
        this.foods = foods;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }


    public void updateData(List<FoodSetGet> newFoods) {
        this.foods = newFoods;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FoodSetGet food = foods.get(position);
        holder.bind(food);

        // Set the background drawable for receiptStatus TextView based on the status
//        if (item.getStatus() != null && item.getStatus().equals("Debt")) {
//            holder.receiptStatus.setBackgroundResource(R.drawable.roundedred);
//        } else {
//            holder.receiptStatus.setBackgroundResource(R.drawable.roundedgreen);
//        }

        // Set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(position, food);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return foods.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView food_name;
        private TextView food_price;
        private TextView food_status;
        private ImageView foodPic;
        private LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            food_name=itemView.findViewById(R.id.fc_foodName);
            food_price = itemView.findViewById(R.id.fc_foodPrice);
            food_status = itemView.findViewById(R.id.fc_foodStatus);
            foodPic=itemView.findViewById(R.id.fc_foodImage);
            layout=itemView.findViewById(R.id.ll_foodcard);
        }

        public void bind(FoodSetGet foodSetGet) {
            String text=foodSetGet.getMenuAvailability()+"";
            food_name.setText(foodSetGet.getFoodName());
            food_price.setText(foodSetGet.getFoodPrice());
            food_status.setText(foodSetGet.getFoodStatus());
            if (text.equals("Available")){
                layout.setVisibility(View.GONE);
            }else {
                layout.setVisibility(View.VISIBLE);
            }


            Glide.with(itemView.getContext())
                    .load(foodSetGet.getItemImage())
                    .into(foodPic);


        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position,FoodSetGet foodSetGet);
    }
}



