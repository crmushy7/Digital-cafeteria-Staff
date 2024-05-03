package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dtcsstaff.R;

import java.util.List;

public class FoodAdapterStaff  extends RecyclerView.Adapter<FoodAdapterStaff.ViewHolder> {

    private List<FoodSetGetStaff> foods;
    private OnItemClickListener mListener;
    private boolean clickable = true;
    public static Context context;

    public FoodAdapterStaff(Context context,List<FoodSetGetStaff> foods) {
        this.foods = foods;
        this.context=context;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }


    public void updateData(List<FoodSetGetStaff> newFoods) {
        this.foods = newFoods;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_card_staff, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FoodSetGetStaff food = foods.get(position);
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
        private TextView soldCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            food_name=itemView.findViewById(R.id.fc_foodName);
            food_price = itemView.findViewById(R.id.fc_foodPrice);
            food_status = itemView.findViewById(R.id.fc_foodStatus);
            foodPic=itemView.findViewById(R.id.fc_foodImage);
            soldCount=itemView.findViewById(R.id.fc_soldAmount);
        }

        public void bind(FoodSetGetStaff foodSetGetStaff) {
            food_name.setText(foodSetGetStaff.getFoodName());
            food_price.setText(foodSetGetStaff.getFoodPrice());
            food_status.setText(foodSetGetStaff.getFoodStatus());
            if (!foodSetGetStaff.getFoodStatus().equals("Available")){
                food_status.setTextColor(ContextCompat.getColor(context, R.color.red));
            }else{
                food_status.setTextColor(ContextCompat.getColor(context, R.color.green));
            }
            soldCount.setText(foodSetGetStaff.getSoldNumber());


            Glide.with(itemView.getContext())
                    .load(foodSetGetStaff.getItemImage())
                    .into(foodPic);


        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position,FoodSetGetStaff foodSetGetStaffStaff);
    }
}



