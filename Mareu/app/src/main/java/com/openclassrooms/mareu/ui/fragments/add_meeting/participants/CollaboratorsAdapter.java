package com.openclassrooms.mareu.ui.fragments.add_meeting.participants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.mareu.R;
import com.openclassrooms.mareu.model.Collaborator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * RecyclerView adapter for the list of collaborators that can participate to the meeting
 */
public class CollaboratorsAdapter extends RecyclerView.Adapter<CollaboratorsAdapter.ViewHolder> {

    private Context context;

    /**
     * list of collaborators
     */
    private List<Collaborator> collaborators;

    /**
     * list of participants of the meeting
     */
    private List<Collaborator> participants;

    public List<Collaborator> getParticipants() {
        return participants;
    }

    public CollaboratorsAdapter(List<Collaborator> collaborators) {
        this.collaborators = collaborators;
        this.participants = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context == null) { context = parent.getContext(); }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_participant_meeting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        Collaborator collaborator = collaborators.get(position);
        holder.participantName.setText(collaborator.getFirstName() + " " + collaborator.getLastName().toUpperCase());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkBoxParticipation.getDrawable().getConstantState() == context.getDrawable(R.drawable.ic_check_box_outline_blank_24dp).getConstantState()) {
                    holder.checkBoxParticipation.setImageResource(R.drawable.ic_check_box_24dp);
                    participants.add(collaborators.get(position));
                } else {
                    holder.checkBoxParticipation.setImageResource(R.drawable.ic_check_box_outline_blank_24dp);
                    participants.remove(collaborators.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return collaborators.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.root_view)
        public ConstraintLayout itemView;
        @BindView(R.id.text_view_participant_name)
        public TextView participantName;
        @BindView(R.id.check_box_participation)
        public ImageView checkBoxParticipation;

        public ViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
