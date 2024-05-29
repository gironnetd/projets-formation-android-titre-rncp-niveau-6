package com.openclassrooms.go4lunch.view.main.map;

import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.openclassrooms.go4lunch.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to display the result of search of the user
 */
public class SearchListAdapter extends BaseAdapter implements Filterable {

    List<SpannableString> mData;
    List<SpannableString> mStringFilterList;
    ValueFilter valueFilter;

    interface SearchListener {
        void onSearchItemClick(int position);
    }

    SearchListener callBack;

    public void setCallBack(SearchListener mCallBack) {
        this.callBack = mCallBack;
    }

    public SearchListAdapter() {
    }

    public SearchListAdapter(List<SpannableString> cancel_type) {
        mData=cancel_type;
        mStringFilterList = cancel_type;
    }

    public void setmData(List<SpannableString> mData) {
        this.mData = mData;
    }

    public void setmStringFilterList(List<SpannableString> mStringFilterList) {
        this.mStringFilterList = mStringFilterList;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position).toString();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_search_map_view, parent, false);
        }

        TextView searchResultAddress = convertView.findViewById(R.id.tv_search_result_address);
        searchResultAddress.setText(mData.get(position));

        convertView.setOnClickListener(view -> callBack.onSearchItemClick(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<String> filterList = new ArrayList<>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).toString().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(mStringFilterList.get(i).toString());
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mData = (List<SpannableString>) results.values;
            notifyDataSetChanged();
        }

    }
}
