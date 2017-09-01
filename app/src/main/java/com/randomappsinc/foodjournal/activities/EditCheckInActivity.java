package com.randomappsinc.foodjournal.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.fragments.CheckInsFragment;
import com.randomappsinc.foodjournal.fragments.DatePickerFragment;
import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.TimeUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditCheckInActivity extends StandardActivity {

    public static final String CHECK_IN_KEY = "checkIn";

    private final DatePickerFragment.Listener mDateListener = new DatePickerFragment.Listener() {
        @Override
        public void onDateChosen(long dateTimeInMillis) {
            checkIn.setTimeAdded(dateTimeInMillis);
            mDateInput.setText(TimeUtils.getDateText(dateTimeInMillis));
        }

        @Override
        public long getCurrentTime() {
            return checkIn.getTimeAdded();
        }
    };

    @BindView(R.id.experience_input) EditText mExperienceInput;
    @BindView(R.id.date_input) TextView mDateInput;

    private CheckIn checkIn;
    private DatePickerFragment mDatePickerFragment;
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

        mDatePickerFragment = new DatePickerFragment();
        mDatePickerFragment.setListener(mDateListener);

        mExperienceInput.setText(checkIn.getMessage());
        mDateInput.setText(TimeUtils.getDateText(checkIn.getTimeAdded()));
    }

    @OnClick(R.id.date_input)
    public void setDate() {
        mDatePickerFragment.show(getFragmentManager(), "datePicker");
    }

    @OnClick(R.id.save)
    public void onCheckInSaved() {
        checkIn.setMessage(mExperienceInput.getText().toString().trim());
        DatabaseManager.get().getCheckInsDBManager().updateCheckIn(checkIn);
        setResult(CheckInsFragment.EDIT_RESULT_CODE);
        finish();
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
