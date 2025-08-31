package com.custom.expensemanager.views.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.SharedPreferences;

import com.custom.expensemanager.adapters.TransactionsAdapter;
import com.custom.expensemanager.models.Transaction;
import com.custom.expensemanager.utils.Constants;
import com.custom.expensemanager.utils.Helper;
import com.custom.expensemanager.viewmodels.MainViewModel;
import com.custom.expensemanager.views.fragments.AddTransactionFragment;
import com.custom.expensemanager.R;
import com.custom.expensemanager.databinding.ActivityMainBinding;
import com.custom.expensemanager.views.fragments.StatsFragment;
import com.custom.expensemanager.views.fragments.TransactionsFragment;
import com.custom.expensemanager.AccountsFragment;
import com.custom.expensemanager.views.fragments.MoreFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    Calendar calendar;
    /*
    0 = Daily
    1 = Monthly
    2 = Calendar
    3 = Summary
    4 = Notes
     */


    public MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore theme preference
        SharedPreferences sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        int themeMode = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(themeMode);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);



        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setTitle("Transactions");


        Constants.setCategories();

        calendar = Calendar.getInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new TransactionsFragment());
        transaction.commit();

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if(item.getItemId() == R.id.transactions) {
                    getSupportFragmentManager().popBackStack();
                    getSupportActionBar().setTitle("Transactions");
                } else if(item.getItemId() == R.id.stats){
                    transaction.replace(R.id.content, new StatsFragment());
                    transaction.addToBackStack(null);
                    getSupportActionBar().setTitle("Statistics");
                } else if(item.getItemId() == R.id.accounts){
                    transaction.replace(R.id.content, new AccountsFragment());
                    transaction.addToBackStack(null);
                    getSupportActionBar().setTitle("Accounts");
                } else if(item.getItemId() == R.id.more){
                    transaction.replace(R.id.content, new MoreFragment());
                    transaction.addToBackStack(null);
                    getSupportActionBar().setTitle("More");
                }
                transaction.commit();
                return true;
            }
        });


    }

    public void getTransactions() {
        viewModel.getTransactions(calendar);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
