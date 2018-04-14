package fr.zigomar.chroma.chroma.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

import fr.zigomar.chroma.chroma.R;
import fr.zigomar.chroma.chroma.activities.MainActivity;
import fr.zigomar.chroma.chroma.activities.SettingsActivity;

public class PasswordFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private EditText passwordField;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this.passwordField = new EditText(getActivity());
        //this.passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.input_password, (ViewGroup) getView(), false);
        this.passwordField = v.findViewById(R.id.PasswordField);
        v.findViewById(R.id.PasswordRepeatField).setVisibility(View.GONE);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.app_name)
                .setMessage(R.string.PleaseEnterAppPassword)
                .setPositiveButton("OK", this)
                .setView(v)
                .create();


        //return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String result = this.passwordField.getText().toString();
        Log.i("CHROMA", "Submitted password is :" + result);
        MainActivity callingActivity = (MainActivity) getActivity();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(callingActivity);

        if (Objects.equals(sharedPref.getString(SettingsActivity.KEY_PREF_APP_PASSWORD_PWD, ""), result)) {
            dialog.dismiss();
        } else {
            Toast.makeText(getActivity(), R.string.IncorrectPassword, Toast.LENGTH_SHORT).show();

            PasswordFragment pwdFragment = new PasswordFragment();
            getFragmentManager().beginTransaction().add(pwdFragment, "NewPasswordAttempt").commit();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.i("CHROMA", "PasswordDialog was canceled, opening again.");

        Toast.makeText(getActivity(), R.string.PleaseInputPassword, Toast.LENGTH_SHORT).show();

        PasswordFragment pwdFragment = new PasswordFragment();
        getFragmentManager().beginTransaction().add(pwdFragment, "NewPasswordAttempt").commit();
    }

}
