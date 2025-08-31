package com.custom.expensemanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.custom.expensemanager.R;
import com.custom.expensemanager.databinding.RowAccountBinding;
import com.custom.expensemanager.models.Account;
import com.custom.expensemanager.utils.Constants;
import com.custom.expensemanager.utils.Helper;

import java.util.ArrayList;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.AccountsViewHolder> {

    Context context;
    ArrayList<Account> accountArrayList;

    public interface AccountsClickListener {
        void onAccountSelected(Account account);
    }

    AccountsClickListener accountsClickListener;


    public AccountsAdapter(Context context, ArrayList<Account> accountArrayList, AccountsClickListener accountsClickListener) {
        this.context = context;
        this.accountArrayList = accountArrayList;
        this.accountsClickListener = accountsClickListener;
    }

    @NonNull
    @Override
    public AccountsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AccountsViewHolder(LayoutInflater.from(context).inflate(R.layout.row_account, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccountsViewHolder holder, int position) {
        Account account = accountArrayList.get(position);
        holder.binding.accountName.setText(account.getAccountName());
        holder.binding.accountBalance.setText(Helper.formatCurrency(account.getAccountAmount()));

        // Set account type and color
        String accountType = getAccountType(account.getAccountName());
        holder.binding.accountType.setText(accountType);
        holder.binding.accountType.setBackgroundColor(context.getColor(Constants.getAccountsColor(accountType)));

        // Set appropriate icon based on account type
        int iconRes = getAccountIcon(accountType);
        holder.binding.accountIcon.setImageResource(iconRes);

        // Set balance color based on amount
        if (account.getAccountAmount() >= 0) {
            holder.binding.accountBalance.setTextColor(context.getColor(R.color.greenColor));
        } else {
            holder.binding.accountBalance.setTextColor(context.getColor(R.color.redColor));
        }

        holder.itemView.setOnClickListener(c-> {
            accountsClickListener.onAccountSelected(account);
        });
    }

    @Override
    public int getItemCount() {
        return accountArrayList.size();
    }

    private String getAccountType(String accountName) {
        String name = accountName.toLowerCase();
        if (name.contains("bank") || name.contains("hdfc") || name.contains("sbi")) {
            return "Bank";
        } else if (name.contains("cash") || name.contains("wallet")) {
            return "Cash";
        } else if (name.contains("card") || name.contains("credit")) {
            return "Card";
        } else {
            return "Other";
        }
    }

    private int getAccountIcon(String accountType) {
        switch (accountType) {
            case "Bank":
                return R.drawable.ic_accounts;
            case "Cash":
                return R.drawable.ic_salary;
            case "Card":
                return R.drawable.ic_business;
            default:
                return R.drawable.ic_other;
        }
    }

    public class AccountsViewHolder extends RecyclerView.ViewHolder {

        RowAccountBinding binding;

        public AccountsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowAccountBinding.bind(itemView);
        }
    }
}
