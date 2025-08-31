package com.custom.expensemanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.custom.expensemanager.R;
import com.custom.expensemanager.adapters.AccountsAdapter;
import com.custom.expensemanager.databinding.FragmentAccountsBinding;
import com.custom.expensemanager.models.Account;
import com.custom.expensemanager.utils.Constants;
import com.custom.expensemanager.utils.Helper;

import java.util.ArrayList;

public class AccountsFragment extends Fragment {

    public AccountsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentAccountsBinding binding;
    ArrayList<Account> accounts;
    AccountsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountsBinding.inflate(inflater, container, false);

        // Initialize accounts list if null
        if (accounts == null) {
            accounts = new ArrayList<>();
            // Add sample accounts if none exist
            if (accounts.isEmpty()) {
                accounts.add(new Account(5000.00, "HDFC Bank"));
                accounts.add(new Account(1200.50, "Cash"));
                accounts.add(new Account(-500.25, "Credit Card"));
                accounts.add(new Account(2500.00, "Paytm Wallet"));
            }
        }

        // Calculate total balance
        double totalBalance = 0;
        for (Account account : accounts) {
            totalBalance += account.getAccountAmount();
        }

        binding.totalBalanceAmount.setText(Helper.formatCurrency(totalBalance));

        // Set up RecyclerView
        adapter = new AccountsAdapter(getContext(), accounts, new AccountsAdapter.AccountsClickListener() {
            @Override
            public void onAccountSelected(Account account) {
                // Handle account selection
                showAccountDetails(account);
            }
        });

        binding.accountsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.accountsRecyclerView.setAdapter(adapter);

        // Add new account button
        binding.addAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAccountDialog();
            }
        });

        return binding.getRoot();
    }

    private void showAccountDetails(Account account) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(account.getAccountName());
        builder.setMessage("Balance: " + Helper.formatCurrency(account.getAccountAmount()) +
                          "\n\nAccount Type: " + getAccountType(account.getAccountName()) +
                          "\nColor: " + getAccountColorName(account.getAccountName()));
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showEditAccountDialog(account);
            }
        });
        builder.show();
    }

    private void showAddAccountDialog() {
        String[] accountTypes = {"Bank Account", "Cash", "Credit Card", "Digital Wallet", "Investment"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add New Account");
        builder.setItems(accountTypes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedType = accountTypes[which];
                // TODO: Implement actual account creation
                Toast.makeText(getContext(), selectedType + " creation coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void showEditAccountDialog(final Account account) {
        final String[] options = {"Add Money", "Subtract Money"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit " + account.getAccountName());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showAmountDialog(account, true); // Add money
                } else {
                    showAmountDialog(account, false); // Subtract money
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showAmountDialog(final Account account, final boolean isAdding) {
        final android.widget.EditText input = new android.widget.EditText(getContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Enter amount");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle((isAdding ? "Add" : "Subtract") + " Money to " + account.getAccountName());
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String amountStr = input.getText().toString().trim();
                if (!amountStr.isEmpty()) {
                    try {
                        double amount = Double.parseDouble(amountStr);
                        if (amount > 0) {
                            updateAccountBalance(account, amount, isAdding);
                        } else {
                            Toast.makeText(getContext(), "Please enter a positive amount", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateAccountBalance(Account account, double amount, boolean isAdding) {
        double currentBalance = account.getAccountAmount();
        double newBalance;

        if (isAdding) {
            newBalance = currentBalance + amount;
        } else {
            newBalance = currentBalance - amount;
        }

        account.setAccountAmount(newBalance);

        // Update total balance
        updateTotalBalance();

        // Refresh the RecyclerView
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        String operation = isAdding ? "added to" : "subtracted from";
        Toast.makeText(getContext(),
            "Rs " + String.format("%.2f", amount) + " " + operation + " " + account.getAccountName(),
            Toast.LENGTH_SHORT).show();
    }

    private void updateTotalBalance() {
        if (accounts != null && binding != null) {
            double totalBalance = 0;
            for (Account account : accounts) {
                totalBalance += account.getAccountAmount();
            }
            binding.totalBalanceAmount.setText(Helper.formatCurrency(totalBalance));
        }
    }

    private String getAccountType(String accountName) {
        if (accountName.toLowerCase().contains("bank")) {
            return "Bank Account";
        } else if (accountName.toLowerCase().contains("cash")) {
            return "Cash";
        } else if (accountName.toLowerCase().contains("card")) {
            return "Credit Card";
        } else if (accountName.toLowerCase().contains("wallet")) {
            return "Digital Wallet";
        } else {
            return "Other";
        }
    }

    private String getAccountColorName(String accountName) {
        int colorRes = Constants.getAccountsColor(getAccountType(accountName));
        if (colorRes == R.color.bank_color) {
            return "Blue";
        } else if (colorRes == R.color.cash_color) {
            return "Green";
        } else if (colorRes == R.color.card_color) {
            return "Red";
        } else {
            return "Default";
        }
    }
}
