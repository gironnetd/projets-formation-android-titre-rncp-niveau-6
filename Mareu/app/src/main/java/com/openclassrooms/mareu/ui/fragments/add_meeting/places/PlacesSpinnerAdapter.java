package com.openclassrooms.mareu.ui.fragments.add_meeting.places;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.openclassrooms.mareu.R;
import com.openclassrooms.mareu.model.Place;

import java.util.List;

/**
 * Array adapter to display the list of places for the meeting
 */
public class PlacesSpinnerAdapter extends ArrayAdapter<Place> {

    /**
     * list of the places for the meeting
     */
    private List<Place> places;

    public PlacesSpinnerAdapter(Context context, List<Place> places) {
        super(context, R.layout.item_spinner_place_meeting, places);
        this.places = places;
    }

    @Override
    public int getCount() {
        return places.size() + 1;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (position == 0) {
            return initialSelection(true);
        }
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (position == 0) {
            return initialSelection(false);
        }
        return getCustomView(position, convertView, parent);
    }

    private View initialSelection(boolean dropdown) {
        TextView view = new TextView(getContext());
        view.setText(R.string.spinner_default_text);

        if (dropdown) { // Hidden when the dropdown is opened
            view.setHeight(0);
        }
        return view;
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        // Distinguish "real" spinner items (that can be reused) from initial selection item
        View view = convertView != null && !(convertView instanceof TextView)
                ? convertView :
                LayoutInflater.from(getContext()).inflate(R.layout.item_spinner_place_meeting, parent, false);

        // Adjust for initial selection item
        position = position - 1;
        Place place = getItem(position);

        TextView placeName = view.findViewById(R.id.text_view_place_name);
        placeName.setText(place.getName());
        placeName.setPadding(24,0,0,0);
        return view;
    }
}
