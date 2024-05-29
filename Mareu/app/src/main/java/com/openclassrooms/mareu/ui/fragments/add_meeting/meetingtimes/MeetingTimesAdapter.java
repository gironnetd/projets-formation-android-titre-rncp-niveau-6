package com.openclassrooms.mareu.ui.fragments.add_meeting.meetingtimes;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.mareu.R;
import com.openclassrooms.mareu.model.MeetingTime;
import com.openclassrooms.mareu.model.Place;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * RecyclerView adapter for the list of meeting times
 */
public class MeetingTimesAdapter extends RecyclerView.Adapter<MeetingTimesAdapter.ViewHolder> {

    private Context context;

    /**
     * place of the meeting
     */
    private Place selectedPlace;

    /**
     * meeting time selected for the meeting
     */
    private MeetingTime selectedMeetingTime;

    /**
     * list of the view holders of the list
     */
    private List<ViewHolder> viewHolders;

    /**
     * last position selected of meeting time in the list for the meeting
     */
    private int lastSelectedPosition;

    public int getLastSelectedPosition() {
        return lastSelectedPosition;
    }

    public void setSelectedPlace(Place selectedPlace) {
        this.selectedPlace = selectedPlace;
        viewHolders.clear();
        notifyDataSetChanged();
    }

    public MeetingTime getSelectedMeetingTime() {
        return selectedMeetingTime;
    }

    public void setDateOfSelectedMeetingTime(Calendar calendar) {
        selectedMeetingTime.getStartTime().set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE));
    }

    public MeetingTimesAdapter(Place selectedPlace) {
        this.selectedPlace = selectedPlace;
        this.viewHolders = new ArrayList<>();
        lastSelectedPosition = -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context == null) { context = parent.getContext(); }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meeting_time, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        viewHolders.add(holder);
        final MeetingTime meetingTime = selectedPlace.getMeetingTimes().get(position);

        String meetingTimeText = context.getString(R.string.meeting_time_string_format,
                DateFormat.getTimeInstance(DateFormat.SHORT, Locale.FRANCE).format(meetingTime.getStartTime().getTime()),
                DateFormat.getTimeInstance(DateFormat.SHORT, Locale.FRANCE).format(meetingTime.getEndTime().getTime()));

        Spanned styledText = HtmlCompat.fromHtml(meetingTimeText, HtmlCompat.FROM_HTML_MODE_LEGACY);
        holder.meetingTime.setText(styledText);

        if(meetingTime.getReserved()) {
            holder.isReserved.setTextColor(context.getResources().getColor(R.color.reserved_meeting_time));
            holder.isReserved.setText(R.string.reserved_meeting_time_text);
            holder.meetingTime.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.view.setClickable(false);
        } else {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedMeetingTime = meetingTime;
                    holder.isReserved.setTextColor(context.getResources().getColor(R.color.reserved_meeting_time));
                    holder.isReserved.setText(R.string.reserved_meeting_time_text);
                    holder.meetingTime.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                    holder.view.setClickable(false);

                    if(lastSelectedPosition != -1) {
                        viewHolders.get(lastSelectedPosition).isReserved.setTextColor(context.getResources().getColor(R.color.free_meeting_time));
                        viewHolders.get(lastSelectedPosition).isReserved.setText(R.string.free_meeting_time_text);
                        viewHolders.get(lastSelectedPosition).meetingTime.setTextColor(context.getResources().getColor(android.R.color.black));
                        viewHolders.get(lastSelectedPosition).view.setClickable(true);
                    }
                    lastSelectedPosition = position;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return selectedPlace.getMeetingTimes().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view_meeting_time)
        public TextView meetingTime;
        @BindView(R.id.text_view_is_reserved)
        public TextView isReserved;
        @BindView(R.id.root_view)
        public ConstraintLayout view;

        public ViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
