package com.randomappsinc.foodjournal.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.Fragments.CheckInsFragment;
import com.randomappsinc.foodjournal.Models.CheckIn;
import com.randomappsinc.foodjournal.Persistence.DatabaseManager;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.Utils.UIUtils;

import butterknife.ButterKnife;

public class EditCheckInActivity extends StandardActivity {

    public static final String CHECK_IN_KEY = "checkIn";

    private CheckIn checkIn;
    private MaterialDialog mDeleteConfirmationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_check_in);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkIn = getIntent().getParcelableExtra(CHECK_IN_KEY);
        mDeleteConfirmationDialog = new MaterialDialog.Builder(this)
                .title(R.string.check_in_delete_title)
                .content(R.string.check_in_delete_content)
                .negativeText(android.R.string.no)
                .positiveText(R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DatabaseManager.get().getCheckInsDBManager().deleteCheckIn(checkIn);
                        setResult(CheckInsFragment.DELETED_RESULT_CODE);
                        finish();
                    }
                })
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.content_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.delete, IoniconsIcons.ion_android_delete, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                mDeleteConfirmationDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
