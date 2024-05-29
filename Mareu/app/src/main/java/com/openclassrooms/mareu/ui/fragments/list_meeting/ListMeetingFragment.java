package com.openclassrooms.mareu.ui.fragments.list_meeting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.mareu.R;
import com.openclassrooms.mareu.api.ApiService;
import com.openclassrooms.mareu.di.Injection;
import com.openclassrooms.mareu.events.DeleteMeetingEvent;
import com.openclassrooms.mareu.model.Meeting;
import com.openclassrooms.mareu.model.Place;
import com.openclassrooms.mareu.ui.activity.MeetingActivity;
import com.openclassrooms.mareu.ui.fragments.add_meeting.places.PlacesSpinnerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.openclassrooms.mareu.model.Place.PlaceGenerator.generatePlaces;

public class ListMeetingFragment extends Fragment {

    /**
     * RecyclerView meetings
     */
    @BindView(R.id.list_meetings)
    RecyclerView meetingsList;

    /**
     * RecyclerViewAdapter for the meetings RecyclerView
     */
    ListMeetingAdapter meetingListAdapter;

    /**
     * TextView displaying that there are no meetings to register
     */
    @BindView(R.id.tv_no_meetings)
    TextView noMeetingsTextView;

    /**
     * ApiService to perform tasks
     */
    private ApiService mApiService;

    @BindView(R.id.filter_by_place_spinner_places)
    Spinner spinnerPlaces;

    @BindView(R.id.card_view_places_meeting)
    CardView filterByPlaceCardView;

    @BindView(R.id.filter_by_place_image_view_close)
    ImageView closeFilterByPlaceImageView;

    @BindView(R.id.card_view_date_meeting)
    CardView filterByDateCardView;

    @BindView(R.id.filter_by_date_image_view_date_meeting)
    ImageView showMeetingDatePickerImageView;

    @BindView(R.id.filter_by_date_image_view_close)
    ImageView closeFilterByDateImageView;

    @BindView(R.id.filter_by_date_meeting_date_picker)
    DatePicker meetingDatePicker;

    /**
     * list of meetings
     */
    private List<Meeting> meetings;

    /**
     * list of places that can be choosed for the meeting
     */
    public final List<Place> places = generatePlaces();

    public List<Meeting> getMeetings() {
        return meetings;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_meeting, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mApiService = Injection.getMeetingApiService();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadMeetings();
        configureImageViews();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Fired if the user clicks on a delete button
     *
     * @param event
     */
    @Subscribe
    public void onDeleteMeeting(DeleteMeetingEvent event) {
        mApiService.deleteMeeting(event.meeting);
        loadMeetings();
    }

    /**
     * load meetings from ApiService to populate meetingsList
     */
    public void loadMeetings() {
        meetings = mApiService.getMeetings();
        meetingListAdapter = new ListMeetingAdapter(false, meetings);
        meetingsList.setAdapter(meetingListAdapter);

        displayMeetingsList();
    }

    public void loadMeetings(List<Meeting> meetings) {
        this.meetings = meetings;

        meetingListAdapter = new ListMeetingAdapter(true, meetings);
        meetingsList.setAdapter(meetingListAdapter);

        if (this.meetings.isEmpty() && filterByPlaceCardView.getVisibility() == View.VISIBLE) {
            noMeetingsTextView.setText(R.string.no_meetings_for_this_place);
        }

        if (this.meetings.isEmpty() && filterByDateCardView.getVisibility() == View.VISIBLE) {
            noMeetingsTextView.setText(R.string.no_meetings_for_this_date);
        }

        displayMeetingsList();
    }

    /**
     * check if meetingsList have to be displayed
     */
    private void displayMeetingsList() {
        if (!meetings.isEmpty()) {
            noMeetingsTextView.setVisibility(View.GONE);
            meetingsList.setVisibility(View.VISIBLE);
        } else {
            noMeetingsTextView.setVisibility(View.VISIBLE);
            meetingsList.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (!((MeetingActivity) getActivity()).isTwoPanes()) {
            inflater.inflate(R.menu.menu_list_meeting, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_by_date:
                meetingsList.setVisibility(View.INVISIBLE);
                if (mApiService.getMeetings().size() > 1) {
                    if (filterByPlaceCardView.getVisibility() == View.VISIBLE) {
                        hidePlacePlaceSpinner();
                    }
                    displayDateTimePicker();
                }
                break;
            case R.id.filter_by_place:
                meetingsList.setVisibility(View.INVISIBLE);
                if (mApiService.getMeetings().size() > 1) {
                    if (filterByDateCardView.getVisibility() == View.VISIBLE) {
                        hideMeetingsDatePicker();
                    }
                    displayPlaceSpinner();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideMeetingsDatePicker() {
        filterByDateCardView.setVisibility(View.GONE);
    }

    private void displayDateTimePicker() {
        configureMeetingDatePicker();
        filterByDateCardView.setVisibility(View.VISIBLE);

        if (meetingDatePicker.getVisibility() == View.GONE) {
            meetingDatePicker.setVisibility(View.VISIBLE);
        }

        if (noMeetingsTextView.getVisibility() == View.VISIBLE) {
            noMeetingsTextView.setVisibility(View.GONE);
        }
    }

    private void configureMeetingDatePicker() {
        meetingDatePicker.setMinDate(System.currentTimeMillis() - 1000);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        meetingDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                GregorianCalendar date = new GregorianCalendar();
                date.set(year, (datePicker.getMonth() + 1), dayOfMonth);
                loadMeetings(mApiService.filterMeetingsByDate(date));
                meetingDatePicker.setVisibility(View.GONE);
            }
        });
    }

    private void displayPlaceSpinner() {
        configureSpinnerPlaces();
        filterByPlaceCardView.setVisibility(View.VISIBLE);
    }

    private void hidePlacePlaceSpinner() {
        filterByPlaceCardView.setVisibility(View.GONE);
    }

    /**
     * configure the spinner of the places for meeting
     */
    private void configureSpinnerPlaces() {
        PlacesSpinnerAdapter dataAdapter = new PlacesSpinnerAdapter(getActivity(), places);
        spinnerPlaces.setAdapter(dataAdapter);
        spinnerPlaces.setPrompt(getString(R.string.spinner_default_text));

        spinnerPlaces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (meetingsList.getVisibility() == View.VISIBLE) {
                    meetingsList.setVisibility(View.INVISIBLE);
                }

                if (position != 0) {
                    loadMeetings(mApiService.filterMeetingsByPlace(places.get(position - 1).getName()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void configureImageViews() {

        closeFilterByPlaceImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterByPlaceCardView.getVisibility() == View.VISIBLE) {
                    filterByPlaceCardView.setVisibility(View.GONE);
                    loadMeetings();
                }
            }
        });

        showMeetingDatePickerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (meetingDatePicker.getVisibility() == View.VISIBLE) {
                    meetingDatePicker.setVisibility(View.GONE);
                } else {
                    meetingDatePicker.setVisibility(View.VISIBLE);
                    if (noMeetingsTextView.getVisibility() == View.VISIBLE) {
                        noMeetingsTextView.setVisibility(View.GONE);
                    }

                    if (meetingsList.getVisibility() == View.VISIBLE) {
                        meetingsList.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        closeFilterByDateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterByDateCardView.getVisibility() == View.VISIBLE) {
                    filterByDateCardView.setVisibility(View.GONE);
                    loadMeetings();
                }
            }
        });
    }
}
