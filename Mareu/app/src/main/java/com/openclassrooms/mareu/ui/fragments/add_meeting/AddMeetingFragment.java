package com.openclassrooms.mareu.ui.fragments.add_meeting;

import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.openclassrooms.mareu.R;
import com.openclassrooms.mareu.api.ApiService;
import com.openclassrooms.mareu.di.Injection;
import com.openclassrooms.mareu.model.Meeting;
import com.openclassrooms.mareu.model.Place;
import com.openclassrooms.mareu.ui.activity.MeetingActivity;
import com.openclassrooms.mareu.ui.fragments.add_meeting.meetingtimes.MeetingTimesAdapter;
import com.openclassrooms.mareu.ui.fragments.add_meeting.participants.CollaboratorsAdapter;
import com.openclassrooms.mareu.ui.fragments.add_meeting.places.PlacesSpinnerAdapter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.openclassrooms.mareu.model.Collaborator.CollaboratorGenerator.generateCollaborators;
import static com.openclassrooms.mareu.model.Place.PlaceGenerator.generatePlaces;

public class AddMeetingFragment extends Fragment {

    @BindView(R.id.image_view_subject_meeting)
    ImageView subjectImageView;
    @BindView(R.id.text_input_layout_subject_meeting)
    TextInputLayout subjectTextInputLayout;
    @BindView(R.id.text_input_edit_text_meeting_subject)
    TextInputEditText subjectEditText;
    @BindView(R.id.image_view_place_meeting)
    ImageView placesImageView;
    @BindView(R.id.spinner_places)
    Spinner spinnerPlaces;
    @BindView(R.id.label_meeting_date)
    TextView labelDateMeeting;
    @BindView(R.id.image_view_date_meeting)
    ImageView dateMeetingImageView;
    @BindView(R.id.meeting_date_picker)
    DatePicker meetingDatePicker;
    @BindView(R.id.label_meeting_time)
    TextView labelMeetingTimes;
    @BindView(R.id.image_view_meeting_times)
    ImageView meetingTimesImageView;
    @BindView(R.id.meeting_times_list)
    RecyclerView meetingTimesList;
    @BindView(R.id.image_view_meeting_participants)
    ImageView participantsImageView;
    @BindView(R.id.collaborators_list)
    RecyclerView collaboratorsList;

    /**
     * adapter for the list of meeting times
     */
    private MeetingTimesAdapter meetingTimesAdapter;

    /**
     * adapter for the list of participants of meeting
     */
    private CollaboratorsAdapter collaboratorsAdapter;

    /**
     * place selected for the meeting
     */
    private Place selectedPlace;

    /**
     * ApiService to perform tasks
     */
    private ApiService mApiService;

    /**
     * list of places that can be choosed for the meeting
     */
    private Map<Date, List<Place>> placesByDate;

    private Date selectedDate;

    private int spinnerPlacesPosition;

    private MeetingActivity meetingActivity;

    private OnAddedMeetingListener mCallback;

    public interface OnAddedMeetingListener {
        void onMeetingIsAdded();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MeetingActivity){
            meetingActivity =(MeetingActivity) context;
            try {
                mCallback =(OnAddedMeetingListener) meetingActivity;
            } catch (ClassCastException e) {
                throw new ClassCastException(meetingActivity.toString()
                        + " must implement OnHeadlineSelectedListener");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_meeting, container, false);
        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mApiService = Injection.getMeetingApiService();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MeetingActivity) Objects.requireNonNull(getActivity())).fabAddMeeting.setVisibility(View.GONE);
        if (!((MeetingActivity) getActivity()).isTwoPanes()) {
            Objects.requireNonNull(((MeetingActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(((MeetingActivity) getActivity()).getSupportActionBar()).setTitle(R.string.add_meeting_title);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (!((MeetingActivity) Objects.requireNonNull(getActivity())).isTwoPanes()) {
            inflater.inflate(R.menu.menu_add_meeting, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_meeting) {
            Snackbar snackbar = Snackbar.make(((MeetingActivity) Objects.requireNonNull(getActivity())).coordinatorLayout, getString(R.string.meeting_added), Snackbar.LENGTH_SHORT);

            if (Objects.requireNonNull(subjectEditText.getText()).length() == 0) {
                snackbar.setText(getString(R.string.no_subject_meeting_registered));
                snackbar.show();
                return super.onOptionsItemSelected(item);
            }

            if (selectedPlace == null) {
                snackbar.setText(getString(R.string.no_place_meeting_selected));
                snackbar.show();
                return super.onOptionsItemSelected(item);
            }

            if (meetingTimesAdapter == null || meetingTimesAdapter.getSelectedMeetingTime() == null) {
                snackbar.setText(getString(R.string.no_meeting_time_selected));
                snackbar.show();
                return super.onOptionsItemSelected(item);
            }

            if (collaboratorsAdapter.getParticipants().isEmpty()) {
                snackbar.setText(getString(R.string.no_participants_selected));
                snackbar.show();
                return super.onOptionsItemSelected(item);
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, meetingDatePicker.getYear());
            calendar.set(Calendar.MONTH, (meetingDatePicker.getMonth() + 1));
            calendar.set(Calendar.DAY_OF_MONTH, meetingDatePicker.getDayOfMonth());

            meetingTimesAdapter.setDateOfSelectedMeetingTime(calendar);

            Objects.requireNonNull(placesByDate.get(selectedDate)).get(spinnerPlacesPosition).getMeetingTimes()
                    .get(meetingTimesAdapter.getLastSelectedPosition()).setReserved(true);

            mApiService.createMeeting(new Meeting(mApiService.getMeetings().size(),
                    subjectEditText.getText().toString(),
                    meetingTimesAdapter.getSelectedMeetingTime(),
                    collaboratorsAdapter.getParticipants(), Objects.requireNonNull(placesByDate.get(selectedDate)).get(spinnerPlacesPosition)));
            snackbar.show();

            clearFields();

            if (meetingActivity.isTwoPanes()) {
                mCallback.onMeetingIsAdded();
            } else {
                getActivity().onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearFields() {
        Objects.requireNonNull(subjectEditText.getText()).clear();
        selectedPlace = null;
        meetingTimesAdapter = null;
        configureMeetingDatePicker();
        configureSpinnerPlaces();
        labelMeetingTimes.setVisibility(View.GONE);
        meetingTimesImageView.setVisibility(View.GONE);
        meetingTimesList.setVisibility(View.GONE);
        configureCollaboratorsList();
    }



    @Override
    public void onStart() {
        super.onStart();
        configureImageViews();
        configureMeetingDatePicker();
        configureSpinnerPlaces();
        configureCollaboratorsList();
    }

    /**
     * configure the icons of fragment
     */
    private void configureImageViews() {
        subjectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subjectTextInputLayout.getVisibility() == View.GONE) {
                    subjectTextInputLayout.setVisibility(View.VISIBLE);
                    subjectEditText.requestFocus();
                } else {
                    subjectTextInputLayout.setVisibility(View.GONE);
                }
            }
        });

        placesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerPlaces.getVisibility() == View.GONE) {
                    spinnerPlaces.setVisibility(View.VISIBLE);
                } else {
                    spinnerPlaces.setVisibility(View.GONE);
                }
            }
        });

        dateMeetingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (meetingDatePicker.getVisibility() == View.GONE) {
                    meetingDatePicker.setVisibility(View.VISIBLE);
                } else {
                    meetingDatePicker.setVisibility(View.GONE);
                }
            }
        });

        meetingTimesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (meetingTimesList.getVisibility() == View.GONE) {
                    meetingTimesList.setVisibility(View.VISIBLE);
                } else {
                    meetingTimesList.setVisibility(View.GONE);
                }
            }
        });

        participantsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(collaboratorsList.getVisibility() == View.GONE) {
                    collaboratorsList.setVisibility(View.VISIBLE);
                } else {
                    collaboratorsList.setVisibility(View.GONE);
                }
            }
        });
    }

    private void configureMeetingDatePicker() {
        meetingDatePicker.setMinDate(System.currentTimeMillis() - 1000);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if(placesByDate == null) {
            placesByDate = new HashMap<Date, List<Place>>();
            placesByDate.put(calendar.getTime(), generatePlaces());
        }

        selectedDate = new GregorianCalendar(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0).getTime();

        meetingDatePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        labelMeetingTimes.setVisibility(View.VISIBLE);
                        meetingTimesImageView.setVisibility(View.VISIBLE);
                        meetingTimesList.setVisibility(View.VISIBLE);
                        selectedDate = new GregorianCalendar(year, month, dayOfMonth, 0, 0).getTime();

                        if(placesByDate.get(selectedDate) == null) {
                            placesByDate.put(selectedDate, generatePlaces());
                        }

                        meetingTimesAdapter = new MeetingTimesAdapter(Objects.requireNonNull(placesByDate.get(selectedDate)).get(spinnerPlacesPosition));
                        meetingTimesList.setAdapter(meetingTimesAdapter);
                    }
                });
    }

    /**
     * configure the spinner of the places for meeting
     */
    private void configureSpinnerPlaces() {
        if (placesByDate.get(selectedDate) == null) {
            placesByDate.put(selectedDate, generatePlaces());
        }

        PlacesSpinnerAdapter dataAdapter = new PlacesSpinnerAdapter(getActivity(), placesByDate.get(selectedDate));
        spinnerPlaces.setAdapter(dataAdapter);
        spinnerPlaces.setPrompt(getString(R.string.spinner_default_text));

        spinnerPlaces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {

                    if(placesByDate.get(selectedDate) == null) {
                        placesByDate.put(selectedDate, generatePlaces());
                    }

                    selectedPlace = Objects.requireNonNull(placesByDate.get(selectedDate)).get(position - 1);
                    spinnerPlacesPosition = position - 1;
                    labelDateMeeting.setVisibility(View.VISIBLE);
                    dateMeetingImageView.setVisibility(View.VISIBLE);
                    meetingDatePicker.setVisibility(View.VISIBLE);

                    labelMeetingTimes.setVisibility(View.VISIBLE);
                    meetingTimesImageView.setVisibility(View.VISIBLE);
                    meetingTimesList.setVisibility(View.VISIBLE);

                    meetingTimesAdapter = new MeetingTimesAdapter(Objects.requireNonNull(placesByDate.get(selectedDate)).get(spinnerPlacesPosition));
                    meetingTimesList.setAdapter(meetingTimesAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * configure the list of the collaborators that can participate to the meeting
     */
    private void configureCollaboratorsList() {
        collaboratorsAdapter = new CollaboratorsAdapter(generateCollaborators());
        collaboratorsList.setAdapter(collaboratorsAdapter);
    }
}
