package com.iu.open311_klarschiff;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public abstract class AbstractSearchFragment extends Fragment {
    protected RecyclerView recyclerView;
    protected SearchEntryAdapter entryAdapter;
    protected View rootView;
    protected Client client;

    protected abstract View getViewRoot(@NonNull LayoutInflater inflater, ViewGroup container);

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState
    ) {
        client = Client.getInstance(getContext(),
                getResources().getString(R.string.open311_api_key)
        );
        rootView = getViewRoot(inflater, container);

        initListAdapter();
        handleOnItemClick();

        return rootView;
    }

    private void initListAdapter() {
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        entryAdapter = new SearchEntryAdapter(getResources(), getViewLifecycleOwner());
        recyclerView.setAdapter(entryAdapter);
    }

    protected void handleOnItemClick() {
        recyclerView.addOnItemTouchListener(
                new RecyclerViewTouchListener(getContext(), recyclerView,
                        new RecyclerViewTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                closeKeyboard(view);

                                NavController navController =
                                        Navigation.findNavController(getActivity(),
                                                R.id.nav_host_fragment_content_main
                                        );
                                Bundle bundle = new Bundle();
                                ServiceRequest serviceRequest =
                                        entryAdapter.getByPosition(position);
                                bundle.putInt(DetailFragment.ARGS_SERVICE_REQUEST_ID,
                                        serviceRequest.serviceRequestId
                                );
                                navController.navigate(R.id.nav_details, bundle);
                            }

                            @Override
                            public void onLongClick(View view, int position) {
                                // implement long click if necessary
                            }
                        }
                ));
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void handleSortButton(Button button) {
        button.setOnClickListener(view -> {
            closeKeyboard(view);
            PopupMenu popupMenu = new PopupMenu(getActivity(), view);
            popupMenu.getMenuInflater().inflate(R.menu.search_filter, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.sortCategoryAsc:
                        entryAdapter.sortServiceRequests(SearchOrderEnum.CATEGORY_ASC);
                        break;

                    case R.id.sortCategoryDesc:
                        entryAdapter.sortServiceRequests(SearchOrderEnum.CATEGORY_DESC);
                        break;
                    case R.id.sortStatusAsc:
                        entryAdapter.sortServiceRequests(SearchOrderEnum.STATUS_ASC);
                        break;
                    case R.id.sortStatusDesc:
                        entryAdapter.sortServiceRequests(SearchOrderEnum.STATUS_DESC);
                        break;
                }
                return true;
            });
            popupMenu.setForceShowIcon(true);
            popupMenu.show();
        });
    }

    protected void handleSearch(EditText inputSearch, TextView searchCountField) {
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int searchCount = entryAdapter.filterServiceRequests(editable.toString());
                searchCountField.setText(String.valueOf(searchCount));
            }
        });
    }

    private void closeKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
