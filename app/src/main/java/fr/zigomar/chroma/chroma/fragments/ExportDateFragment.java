package fr.zigomar.chroma.chroma.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import fr.zigomar.chroma.chroma.activities.MainActivity;
import fr.zigomar.chroma.chroma.R;
import fr.zigomar.chroma.chroma.asynctasks.ExportDataTask;

public class ExportDateFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private TextView startPicker;
    private TextView endPicker;
    private String minDate;
    private String maxDate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.export_date_selector, (ViewGroup) getView(), false);

        startPicker = v.findViewById(R.id.startPicker);
        endPicker = v.findViewById(R.id.endPicker);

        startPicker.setText(minDate);
        endPicker.setText(maxDate);

        startPicker.setOnClickListener(new dateSelectorClickListener(minDate));
        endPicker.setOnClickListener(new dateSelectorClickListener(maxDate));

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.app_name)
                .setMessage(R.string.SpecifyExportDates)
                .setPositiveButton("OK", this)
                .setView(v)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String begin = startPicker.getText().toString();
        String end = endPicker.getText().toString();

        int int_begin = MainActivity.dataFilenameFromStringToInt(begin);
        int int_end = MainActivity.dataFilenameFromStringToInt(end);

        if (int_begin > int_end) {
            Toast.makeText(getActivity(), R.string.WrongExportDates, Toast.LENGTH_SHORT).show();
        } else {
            final MainActivity callingActivity = (MainActivity) getActivity();

            new ExportDataTask(callingActivity.getApplicationContext()).execute(begin, end);
            getDialog().dismiss();
        }
    }

    public void setMinDate(String minDate) {
        this.minDate = minDate;
    }

    public void setMaxDate(String maxDate) {
        this.maxDate = maxDate;
    }

    private class dateSelectorClickListener implements View.OnClickListener {

        private String init_date;

        private dateSelectorClickListener(String init) {
            this.init_date = init;
        }

        @Override
        public void onClick(final View v) {
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).parse(this.init_date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker;
            datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, day);
                    init_date = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(c.getTime());
                    TextView tv = (TextView) v;
                    tv.setText(init_date);
                }
            }, year, month, day);
            datePicker.setTitle("Select Date");
            datePicker.show();
        }
    }
}
