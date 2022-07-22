package com.iu.open311_klarschiff;


import android.content.Context;
import android.content.res.Resources;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.List;
import java.util.stream.Collectors;

public class Step1EntryAdapter extends ArrayAdapter<String> {

    private final List<Pair<Integer, String>> serviceGroupNames;
    private final NewIssueViewModel viewModel;
    private final Resources resources;

    public Step1EntryAdapter(Context context, List<Pair<Integer, String>> serviceGroupNames,
                             NewIssueViewModel viewModel, Resources resources
    ) {
        super(context, -1,
                serviceGroupNames.stream().map(pair -> pair.second).collect(Collectors.toList())
        );
        this.serviceGroupNames = serviceGroupNames;
        this.viewModel = viewModel;
        this.resources = resources;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent
    ) {
        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.service_group_list_item, parent, false);
        CardView cardView = view.findViewById(R.id.cardView);
        TextView textView = cardView.findViewById(R.id.serviceGroupName);
        ImageView imageView = cardView.findViewById(R.id.serviceGroupImage);
        textView.setText(serviceGroupNames.get(position).second);

        if (null != viewModel.getSelectedServiceCategoryGroup() &&
                viewModel.getSelectedServiceCategoryGroup().equals(getByPosition(position))) {
            cardView.setCardBackgroundColor(resources.getColor(R.color.logo_yellow));
        }


        String strippedName = serviceGroupNames.get(position).second.toLowerCase()
                .replaceAll("[äöüß/]", "")
                .replace(" ", "");
        int imageId = getContext().getResources()
                .getIdentifier(strippedName, "drawable",
                        getContext().getPackageName()
                );
        if (0 < imageId) {
            imageView.setImageResource(imageId);
        } else {
            imageView.setImageResource(R.drawable.info_square_fill);
        }

        return cardView;
    }

    public Pair<Integer, String> getByPosition(int position) {
        return serviceGroupNames.get(position);
    }
}