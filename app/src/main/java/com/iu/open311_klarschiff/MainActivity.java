package com.iu.open311_klarschiff;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.iu.open311_klarschiff.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends DefaultActivity {
    public static String INTENT_EXTRA_SHOW_MY_REQUESTS = "showMyRequests";

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initServiceCategories();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_issues,
                R.id.nav_search
        ).setOpenableLayout(drawer).build();
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if (null != getIntent().getExtras()) {
            boolean showMyRequests =
                    getIntent().getExtras().getBoolean(INTENT_EXTRA_SHOW_MY_REQUESTS);
            if (showMyRequests) {
                navController.navigate(R.id.nav_issues);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data.hasExtra(INTENT_EXTRA_SHOW_MY_REQUESTS) && Boolean.TRUE.equals(
                    data.getBooleanExtra(INTENT_EXTRA_SHOW_MY_REQUESTS, false))) {
                NavController navController =
                        Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_issues);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) ||
                super.onSupportNavigateUp();
    }

    private void initServiceCategories() {
        Client apiClient = Client.getInstance(getApplicationContext(),
                getResources().getString(R.string.open311_api_key)
        );
        try {
            apiClient.loadServices().observe(this, result -> {
                if (null == result) {
                    return;
                }

                if (result instanceof Result.Success) {
                    ThreadExecutorSupplier.getInstance().getMajorBackgroundTasks().execute(() -> {
                        Database database = Database.getInstance(getApplicationContext());
                        database.serviceCategoryDao().deleteAll();
                        List<ServiceCategory> serviceCategories =
                                (List<ServiceCategory>) ((Result.Success<?>) result).getData();
                        serviceCategories.forEach(serviceCategory -> database.serviceCategoryDao()
                                .insert(serviceCategory));
                    });
                }
            });
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(),
                    "Could not load service categories: " + e.getMessage()
            );
        }
    }
}