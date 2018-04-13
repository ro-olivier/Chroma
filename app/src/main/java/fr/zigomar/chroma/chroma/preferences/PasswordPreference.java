package fr.zigomar.chroma.chroma.preferences;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.preference.DialogPreference;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import fr.zigomar.chroma.chroma.R;

public class PasswordPreference extends DialogPreference {

    private static final String DEFAULT_VALUE = "";
    private String value;

    private EditText passwordField;
    private EditText passwordRepeatField;

    public PasswordPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.input_password);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);

    }

    @Override
    protected void onBindDialogView(View view) {

        this.passwordField = view.findViewById(R.id.PasswordField);
        this.passwordRepeatField = view.findViewById(R.id.PasswordRepeatField);

        view.setBackgroundColor(Color.WHITE);

        super.onBindDialogView(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDialogClosed(boolean result) {

        Log.i("CHROMA", "OnDialogClosed called with result = " + result);

        if (result) {
            if (this.passwordField.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), R.string.EmptyPassword, Toast.LENGTH_SHORT).show();
            } else {
                if (this.passwordRepeatField.getText().toString().equals(this.passwordField.getText().toString())) {
                    this.value = this.passwordField.getText().toString();
                    persistString(this.value);
                } else {
                    Toast.makeText(getContext(), R.string.InvalidPasswordRepeat, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            this.value = this.getPersistedString(DEFAULT_VALUE);
        } else {
            this.value = (String) defaultValue;
            persistString(this.value);
        }
    }
}

