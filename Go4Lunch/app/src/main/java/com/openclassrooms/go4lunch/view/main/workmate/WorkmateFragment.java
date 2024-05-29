package com.openclassrooms.go4lunch.view.main.workmate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.view.main.BaseFragment;
import com.openclassrooms.go4lunch.view.main.MainActivity;
import com.openclassrooms.go4lunch.viewmodel.main.workmate.WorkmateViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment to display the list of the workmates
 */
public class WorkmateFragment extends BaseFragment {

    @BindView(R.id.workmate_recycler_view)
    RecyclerView workmateRecyclerView;

    private WorkmateViewModel workmateViewModel;

    private WorkmateListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, root);
        initWorkmateViewModel();
        ((MainActivity) requireContext()).searchView.setQueryHint(getResources().getString(R.string.search_workmate_title));
        initAutocompletePredictions();
        return root;
    }

    private void initWorkmateViewModel() {
        workmateViewModel = (WorkmateViewModel) obtainViewModel(WorkmateViewModel.class);

        workmateViewModel.queryAllWorkmates();
        workmateViewModel.queryWorkmates().observe(getViewLifecycleOwner(), query -> {
            mAdapter = new WorkmateListAdapter(query);
            workmateRecyclerView.setAdapter(mAdapter);

            if (isRunningInstrumentedTest()) {
                workmateRecyclerView.setItemAnimator(null);
            }
            mAdapter.startListening();
        });
    }

    private void initAutocompletePredictions() {
        EditText searchViewEditText = ((MainActivity) requireContext()).searchView.findViewById(R.id.search_src_text);
        searchViewEditText.setEnabled(false);

        ((MainActivity) requireContext()).searchResultList.setVisibility(View.GONE);
        ((MainActivity) requireContext()).searchView.setQuery("", true);
        ((MainActivity) requireContext()).searchView.setOnQueryTextListener(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }
}
