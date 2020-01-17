package com.mazatron.mazatronsmartpump;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.mazatron.mazatronsmartpump.R;

public class PumpTimerDialog extends AppCompatDialogFragment {

    private NumberPicker mPumpTimer;
    private int mscheduleHours;
    private PumpTimerDialogListener listener;
    private TextView mPumpHourText;
    private int mScheduleText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_timer, null);

        builder.setView(view).setTitle("Pump Timer in Minutes")
                .setIcon(android.R.drawable.ic_lock_idle_alarm)
                .setPositiveButton("Start Timer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.applytexts(mscheduleHours);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        mPumpTimer = view.findViewById(R.id.pumptimer);
        mPumpTimer.setMinValue(1);
        mPumpTimer.setMaxValue(300);

        mPumpHourText = view.findViewById(R.id.hourtext);
        mPumpTimer.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mscheduleHours = numberPicker.getValue();
                mScheduleText = mscheduleHours/60;
                mPumpHourText.setText(String.valueOf(mScheduleText)+" HRS");
            }
        });
        return builder.create();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (PumpTimerDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must Implement ExampleDialogListener");
        }

    }

    public interface  PumpTimerDialogListener {
        void applytexts(int pumpTime);
    }

}
