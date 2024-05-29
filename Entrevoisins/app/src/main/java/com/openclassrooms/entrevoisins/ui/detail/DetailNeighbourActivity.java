package com.openclassrooms.entrevoisins.ui.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.openclassrooms.entrevoisins.R;
import com.openclassrooms.entrevoisins.di.DI;
import com.openclassrooms.entrevoisins.events.ToggleFavoriteNeighbourEvent;
import com.openclassrooms.entrevoisins.model.Neighbour;
import com.openclassrooms.entrevoisins.service.NeighbourApiService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.openclassrooms.entrevoisins.ui.util.Constant.NEIGHBOUR_ID;

public class DetailNeighbourActivity extends AppCompatActivity {

    @BindView(R.id.iv_detail_avatar)
    ImageView neighbourImage;
    @BindView(R.id.tv_detail_neighbour_name)
    TextView neighbourNameTextView;
    @BindView(R.id.cv_detail_neighbour_name)
    TextView cardNeighbourNameTextView;
    @BindView(R.id.tv_adress)
    TextView adressTextView;
    @BindView(R.id.tv_phone_number)
    TextView phoneNumberTextView;
    @BindView(R.id.tv_email)
    TextView emailTextView;
    @BindView(R.id.tv_detail_about_me)
    TextView aboutMeTextView;
    @BindView(R.id.fab_back)
    ImageView fabBack;
    @BindView(R.id.fab_favorite)
    FloatingActionButton fabFavorite;

    private Neighbour mNeighbour;
    private NeighbourApiService mApiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_neighbour);
        ButterKnife.bind(this);
        mApiService = DI.getNeighbourApiService();

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailNeighbourActivity.super.onBackPressed();
            }
        });

        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mNeighbour.getFavorite()) {
                    fabFavorite.setImageResource(R.drawable.ic_star_border_white_24dp);
                } else {
                    fabFavorite.setImageResource(R.drawable.ic_star_white_24dp);
                }

                EventBus.getDefault().post(new ToggleFavoriteNeighbourEvent(mNeighbour));
            }
        });
    }

    /**
     * Init the List of neighbours
     */
    private void initNeighbourDetail() {
        mNeighbour = (Neighbour) getIntent().getSerializableExtra(NEIGHBOUR_ID);
        Glide.with(this)
                .load(mNeighbour.getAvatarUrl())
                .into(neighbourImage);
        neighbourNameTextView.setText(mNeighbour.getName());

        if (mNeighbour.getFavorite()) {
            fabFavorite.setImageResource(R.drawable.ic_star_white_24dp);
        } else {
            fabFavorite.setImageResource(R.drawable.ic_star_border_white_24dp);
        }

        cardNeighbourNameTextView.setText(mNeighbour.getName());
        adressTextView.setText(mNeighbour.getAddress());
        phoneNumberTextView.setText(mNeighbour.getPhoneNumber());

        aboutMeTextView.setText(mNeighbour.getAboutMe());
    }

    @Override
    public void onResume() {
        super.onResume();
        initNeighbourDetail();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void toggleFavorite(ToggleFavoriteNeighbourEvent event) {
        mApiService.toggleToFavorite(event.neighbour);
    }
}

