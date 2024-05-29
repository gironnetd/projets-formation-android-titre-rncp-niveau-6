package com.openclassrooms.go4lunch.view.main.workmate;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.view.detail.DetailActivity;
import com.openclassrooms.go4lunch.view.main.MainActivity;
import com.openclassrooms.go4lunch.view.shared.FirestoreAdapter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.openclassrooms.go4lunch.utilities.Constants.RESTAURANT_DETAIL_ID;

/**
 * Adapter to display the list of the workmates and their choice to eat
 */
public class WorkmateListAdapter extends FirestoreAdapter<WorkmateListAdapter.ViewHolder> {

    private Context context;

    WorkmateListAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_workmate,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User workmate = getSnapshot(position).toObject(User.class);

        if(Objects.requireNonNull(workmate).getPhotoUrl() != null ) {
            Glide.with(holder.workmatePhoto.getContext())
                    .load(Objects.requireNonNull(workmate).getPhotoUrl())
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(holder.workmatePhoto);
        } else {
            Glide.with(holder.workmatePhoto.getContext())
                    .load(R.drawable.ic_group_white_background_24dp)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(holder.workmatePhoto);
        }


        if(workmate.getMiddayRestaurant() != null) {
            holder.workmateJoiningTextView.setTextColor(context.getResources()
                    .getColor(android.R.color.black));
            holder.workmateJoiningTextView.setText(String.format(context.getString(R.string.workmate_is_eating_at),
                    workmate.getDisplayName(),
                    workmate.getMiddayRestaurant().getName()));
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(RESTAURANT_DETAIL_ID, workmate.getMiddayRestaurant().getPlaceId());
                ((MainActivity) context).startActivity(intent);
            });
        } else {
            holder.workmateJoiningTextView.setTextColor(context.getResources()
                    .getColor(android.R.color.darker_gray));
            holder.workmateJoiningTextView.setText(String.format("%s hasn't decided yet", workmate.getDisplayName()));
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_workmate_photo)
        ImageView workmatePhoto;
        @BindView(R.id.tv_workmate_joining)
        TextView workmateJoiningTextView;
        View itemView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }
}
