package com.meowstudio.calendar_organizer.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.meowstudio.calendar_organizer.R;

public class UserSettingsFragment extends Fragment {

    private CardView ringToneCardView;
    private CardView reminderTimeCardView;
    private CardView reminderFrequencyCardView;
    private CardView appThemeCardView;

    private TextView ringtoneTextView;
    private TextView reminderTimeTextView;
    private TextView reminderFrequencyTextView;
    private TextView appThemeTextView;

    private AlertDialog ringtoneAlertDialog;
    private AlertDialog reminderTimeAlertDialog;
    private AlertDialog reminderFrequencyAlertDialog;
    private AlertDialog appThemeAlertDialog;

    private boolean isChanged;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);

        defineViews(view);
        initViews();
        createAlertDialogs();
        defineListeners();

        return view;
    }

    private void defineViews(View view) {
        ringToneCardView = (CardView) view.findViewById(R.id.UserSettingsFragment_CardView_RingTone);
        reminderTimeCardView = (CardView) view.findViewById(R.id.UserSettingsFragment_CardView_ReminderTime);
        reminderFrequencyCardView = (CardView) view.findViewById(R.id.UserSettingsFragment_CardView_ReminderFrequency);
        appThemeCardView = (CardView) view.findViewById(R.id.UserSettingsFragment_CardView_AppTheme);

        ringtoneTextView = (TextView) view.findViewById(R.id.UserSettingsFragment_TextView_DefaultRingtone);
        reminderTimeTextView = (TextView) view.findViewById(R.id.UserSettingsFragment_TextView_DefaultReminderTime);
        reminderFrequencyTextView = (TextView) view.findViewById(R.id.UserSettingsFragment_TextView_DefaultReminderFrequency);
        appThemeTextView = (TextView) view.findViewById(R.id.UserSettingsFragment_TextView_AppTheme);
    }

    private void initViews() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ringtoneTextView.setText(sharedPreferences.getString("ringtone", getResources().getString(R.string.ringtone_1)));
        reminderTimeTextView.setText(sharedPreferences.getString("reminder", getResources().getString(R.string.at_the_time_of_event)));
        reminderFrequencyTextView.setText(sharedPreferences.getString("frequency", getResources().getString(R.string.one_time)));
        appThemeTextView.setText(sharedPreferences.getString("theme", getResources().getString(R.string.orange)));
    }

    private void createAlertDialogs() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogTheme);
        builder.setCancelable(true);

        final MediaPlayer mediaPlayer1 = MediaPlayer.create(getContext(), R.raw.r1_nice_message_tone);
        final MediaPlayer mediaPlayer2 = MediaPlayer.create(getContext(), R.raw.r2_point_blank);
        final MediaPlayer mediaPlayer3 = MediaPlayer.create(getContext(), R.raw.r3_tiru_tiru);
        final MediaPlayer mediaPlayer4 = MediaPlayer.create(getContext(), R.raw.r4_android_cool);
        final MediaPlayer mediaPlayer5 = MediaPlayer.create(getContext(), R.raw.r5_pi_pi_pi);
        final MediaPlayer mediaPlayer6 = MediaPlayer.create(getContext(), R.raw.r6_cat_ring_vibration);
        final MediaPlayer mediaPlayer7 = MediaPlayer.create(getContext(), R.raw.r7_cat_meow_meow);
        final MediaPlayer mediaPlayer8 = MediaPlayer.create(getContext(), R.raw.r8_kitten);
        final MediaPlayer mediaPlayer9 = MediaPlayer.create(getContext(), R.raw.r9_cat_call);

        final View ringtoneDialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_alert_dialog_ringtone, null, false);

        RadioGroup ringtoneRGroup = (RadioGroup) ringtoneDialogView.findViewById(R.id.AlertDialogLayout_RingtoneRadioGroup);

        ringtoneRGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId) {
                    case R.id.r1:
                        mediaPlayer1.start();
                        ringtoneTextView.setText(R.string.ringtone_1);
                        save("ringtone", getString(R.string.ringtone_1));
                        break;
                    case R.id.r2:
                        mediaPlayer2.start();
                        ringtoneTextView.setText(R.string.ringtone_2);
                        save("ringtone", getString(R.string.ringtone_2));
                        break;
                    case R.id.r3:
                        mediaPlayer3.start();
                        ringtoneTextView.setText(R.string.ringtone_3);
                        save("ringtone", getString(R.string.ringtone_3));
                        break;
                    case R.id.r4:
                        mediaPlayer4.start();
                        ringtoneTextView.setText(R.string.ringtone_4);
                        save("ringtone", getString(R.string.ringtone_4));
                        break;
                    case R.id.r5:
                        mediaPlayer5.start();
                        ringtoneTextView.setText(R.string.ringtone_5);
                        save("ringtone", getString(R.string.ringtone_5));
                        break;
                    case R.id.r6:
                        mediaPlayer6.start();
                        ringtoneTextView.setText(R.string.ringtone_6);
                        save("ringtone", getString(R.string.ringtone_6));
                        break;
                    case R.id.r7:
                        mediaPlayer7.start();
                        ringtoneTextView.setText(R.string.ringtone_7);
                        save("ringtone", getString(R.string.ringtone_7));
                        break;
                    case R.id.r8:
                        mediaPlayer8.start();
                        ringtoneTextView.setText(R.string.ringtone_8);
                        save("ringtone", getString(R.string.ringtone_8));
                        break;
                    case R.id.r9:
                        mediaPlayer9.start();
                        ringtoneTextView.setText(R.string.ringtone_9);
                        save("ringtone", getString(R.string.ringtone_9));
                        break;
                }
            }
        });

        builder.setView(ringtoneDialogView);
        ringtoneAlertDialog = builder
                .setPositiveButton("OK", null)
                .create();

        final View reminderTimeDialogView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_alert_dialog_notification, null, false);
        RadioGroup reminderTimeRadioGroup = (RadioGroup) reminderTimeDialogView.findViewById(R.id.AlertDialogLayout_RadioGroup);
        reminderTimeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                reminderTimeTextView.setText(((RadioButton) reminderTimeDialogView.findViewById(group.getCheckedRadioButtonId())).getText().toString());
                save("reminder", ((RadioButton) reminderTimeDialogView.findViewById(group.getCheckedRadioButtonId())).getText().toString());
            }
        });
        builder.setView(reminderTimeDialogView);
        reminderTimeAlertDialog = builder
                .setPositiveButton("OK", null)
                .create();

        final View reminderFrequencyDialogView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_alert_dialog_repeat, null, false);
        RadioGroup reminderFrequencyRadioGroup = (RadioGroup) reminderFrequencyDialogView.findViewById(R.id.AlertDialogLayout_RadioGroup);
        reminderFrequencyRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                reminderFrequencyTextView.setText(((RadioButton) reminderFrequencyDialogView.findViewById(group.getCheckedRadioButtonId())).getText().toString());
                save("frequency", ((RadioButton) reminderFrequencyDialogView.findViewById(group.getCheckedRadioButtonId())).getText().toString());
            }
        });
        builder.setView(reminderFrequencyDialogView);
        reminderFrequencyAlertDialog = builder
                .setPositiveButton("OK", null)
                .create();

        final View appThemeDialogView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_alert_dialog_apptheme, null, false);
        RadioGroup appThemeRadioGroup = (RadioGroup) appThemeDialogView.findViewById(R.id.AlertDialogLayout_RadioGroup);
        appThemeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!appThemeTextView.getText().toString().equalsIgnoreCase(((RadioButton) appThemeDialogView.findViewById(group.getCheckedRadioButtonId())).getText().toString())) {
                    save("theme", ((RadioButton) appThemeDialogView.findViewById(group.getCheckedRadioButtonId())).getText().toString());
                    saveFlag("isChanged", true);
                    isChanged = true;
                }
                appThemeTextView.setText(((RadioButton) appThemeDialogView.findViewById(group.getCheckedRadioButtonId())).getText().toString());
            }
        });
        builder.setView(appThemeDialogView);
        appThemeAlertDialog = builder
                .setPositiveButton("OK", null)
                .create();
    }

    private void defineListeners() {
        ringToneCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtoneAlertDialog.show();
            }
        });

        reminderTimeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderTimeAlertDialog.show();
            }
        });

        reminderFrequencyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderFrequencyAlertDialog.show();
            }
        });

        appThemeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appThemeAlertDialog.show();
                appThemeAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        changeTheme();
                    }
                });
            }
        });
    }

    private void changeTheme() {
        String s = getString("theme");
        if (isChanged) {
            restartApp();
        }
    }

    private void save(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void saveFlag(String key, boolean flag) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, flag);
        editor.apply();
    }

    private String getString(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPreferences.getString(key, "");
    }

    private void restartApp() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }
}
