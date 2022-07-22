package com.iu.open311_klarschiff;


import android.content.Context;
import android.content.res.Resources;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.List;
import java.util.stream.Collectors;

public class Step2EntryAdapter extends ArrayAdapter<String> {

    private final List<Pair<Integer, String>> serviceCategoryNames;
    private final NewIssueViewModel viewModel;
    private final Resources resources;

    public Step2EntryAdapter(Context context, List<Pair<Integer, String>> serviceCategoryNames,
                             NewIssueViewModel viewModel, Resources resources
    ) {
        super(context, -1,
                serviceCategoryNames.stream().map(pair -> pair.second).collect(Collectors.toList())
        );
        this.serviceCategoryNames = serviceCategoryNames;
        this.viewModel = viewModel;
        this.resources = resources;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent
    ) {
        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.service_category_list_item, parent, false);
        CardView cardView = view.findViewById(R.id.cardView);
        TextView textView = cardView.findViewById(R.id.serviceCategoryName);
        textView.setText(serviceCategoryNames.get(position).second);

        if (null != viewModel.getSelectedServiceCategory() &&
                viewModel.getSelectedServiceCategory().equals(getByPosition(position))) {
            cardView.setCardBackgroundColor(resources.getColor(R.color.logo_yellow));
        }

        return cardView;
    }

    public Pair<Integer, String> getByPosition(int position) {
        return serviceCategoryNames.get(position);
    }
}