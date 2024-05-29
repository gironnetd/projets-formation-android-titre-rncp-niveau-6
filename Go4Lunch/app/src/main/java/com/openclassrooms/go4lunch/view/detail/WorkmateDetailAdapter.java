package com.openclassrooms.go4lunch.view.detail;

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
import com.openclassrooms.go4lunch.view.shared.FirestoreAdapter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter to display list of workmates which have chosen this restaurant to eat
 */
public class WorkmateDetailAdapter extends FirestoreAdapter<WorkmateDetailAdapter.ViewHolder> {

    WorkmateDetailAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_workmate,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User workmate = getSnapshot(position).toObject(User.class);

        if(Objects.requireNonNull(workmate).getPhotoUrl() != null) {
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

        holder.workmateJoiningTextView.setText(String.format(holder.workmateJoiningTextView.getContext().getString(R.string.workmate_is_joining), workmate.getDisplayName()));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_workmate_photo)
        ImageView workmatePhoto;
        @BindView(R.id.tv_workmate_joining)
        TextView workmateJoiningTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
