package com.mazatron.mazatronsmartpump;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.mazatron.mazatronsmartpump.R;

public class DateTimeDialog extends AppCompatDialogFragment {

    private  boolean bNeutral = false;
    private TimePicker mscheduleTIme;
    private DateTimeDialogListener listener;

    private Button mSundayButton;
    private boolean mSundayButtonInd = false;

    private Button mSaturdayButton;
    private boolean mSaturdayButtonInd = false;

    private Button mMondayButton;
    private boolean mMondayButtonInd = false;

    private Button mTuesdayButton;
    private boolean mTuesdayButtonInd = false;

    private Button mWednesdayButton;
    private boolean mWednesdayButtonInd = false;

    private Button mThursdayButton;
    private boolean mThursdayButtonInd = false;

    private Button mFridayButton;
    private boolean mFridayButtonInd = false;

    final String  MY_PREFS_NAME_SCHEDULE = "MyMazatronSchedule";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_schedule, null);

        builder.setView(view).setTitle("Schedule Pump")
                             .setIcon(android.R.drawable.ic_menu_my_calendar)
                             .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {

                                 }
                             })
                             .setPositiveButton("Schedule", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {
                                     Integer  currentHour  = mscheduleTIme.getCurrentHour();
                                     Integer currentMinute = mscheduleTIme.getCurrentMinute();

                                     bNeutral = false;
                                     listener.applyTexts(currentHour,currentMinute,
                                             mSundayButtonInd,
                                             mMondayButtonInd,
                                             mTuesdayButtonInd,
                                             mWednesdayButtonInd,
                                             mThursdayButtonInd,
                                             mFridayButtonInd,
                                             mSaturdayButtonInd,
                                             bNeutral
                                             );

                                 }
                             })
                             .setNeutralButton("Clear Schedule", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if (getActivity().getSharedPreferences(MY_PREFS_NAME_SCHEDULE, Context.MODE_PRIVATE) != null) {
                                        SharedPreferences prefScheduleClear = getActivity().getSharedPreferences(MY_PREFS_NAME_SCHEDULE, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor prefScheduleClearEdit = prefScheduleClear.edit();
                                        prefScheduleClearEdit.clear();
                                        prefScheduleClearEdit.apply();
                                        bNeutral = true;
                                        listener.applyTexts(null,null,
                                                false,
                                                false,
                                                false,
                                                false,
                                                false,
                                                false,
                                                false,
                                                bNeutral
                                        );
                                        Toast.makeText(getContext(),"Schedule Clear", Toast.LENGTH_LONG).show();
                                    }


                                }
                             });

        mscheduleTIme  = view.findViewById(R.id.timeschedule);
        mscheduleTIme.setIs24HourView(false);

        if (getActivity().getSharedPreferences(MY_PREFS_NAME_SCHEDULE, Context.MODE_PRIVATE) != null) {
            SharedPreferences prefSchedule = getActivity().getSharedPreferences(MY_PREFS_NAME_SCHEDULE,Context.MODE_PRIVATE);
            mSundayButtonInd  = prefSchedule.getBoolean("SundayAlarm",false);
            mTuesdayButtonInd = prefSchedule.getBoolean("TuesdayAlarm",false);
            mWednesdayButtonInd = prefSchedule.getBoolean("WednesdayAlarm", false);
            mMondayButtonInd = prefSchedule.getBoolean("MondayAlarm", false);
            mThursdayButtonInd = prefSchedule.getBoolean("ThursdayAlarm", false);
            mFridayButtonInd = prefSchedule.getBoolean("FridayAlarm", false);
            mSaturdayButtonInd = prefSchedule.getBoolean("SaturdayAlarm", false);


            if(mSaturdayButtonInd || mFridayButtonInd || mThursdayButtonInd || mWednesdayButtonInd
               || mTuesdayButtonInd || mSundayButtonInd ||mMondayButtonInd){
                mscheduleTIme.setHour(prefSchedule.getInt("ScheduleHour",0 ));
                mscheduleTIme.setMinute(prefSchedule.getInt("ScheduleMinute",0));
            }
        }

        mMondayButton = view.findViewById(R.id.butOne);
        if(mMondayButtonInd){
            mMondayButton.setBackgroundResource(R.color.startblue);
        }
        mMondayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!mMondayButtonInd) {
                    mMondayButton.setBackgroundResource(R.color.startblue);
                    mMondayButtonInd = true;
                }else{
                    mMondayButton.setBackgroundResource(R.color.graylight);
                    mMondayButtonInd = false;
                }
            }
        });

        mTuesdayButton = view.findViewById(R.id.butTwo);
        if(mTuesdayButtonInd){
            mTuesdayButton.setBackgroundResource(R.color.startblue);
        }
        mTuesdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mTuesdayButtonInd) {
                    mTuesdayButton.setBackgroundResource(R.color.startblue);
                    mTuesdayButtonInd = true;
                }else{
                    mTuesdayButton.setBackgroundResource(R.color.graylight);
                    mTuesdayButtonInd = false;
                }

            }
        });

        mWednesdayButton = view.findViewById(R.id.butThree);
        if(mWednesdayButtonInd){
            mWednesdayButton.setBackgroundResource(R.color.startblue);
        }
        mWednesdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mWednesdayButtonInd) {
                    mWednesdayButton.setBackgroundResource(R.color.startblue);
                    mWednesdayButtonInd = true;
                }else{
                    mWednesdayButton.setBackgroundResource(R.color.graylight);
                    mWednesdayButtonInd = false;
                }

            }
        });

        mThursdayButton = view.findViewById(R.id.butFour);
        if(mThursdayButtonInd){
            mThursdayButton.setBackgroundResource(R.color.startblue);
        }
        mThursdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mThursdayButtonInd) {
                    mThursdayButton.setBackgroundResource(R.color.startblue);
                    mThursdayButtonInd = true;
                }else{
                    mThursdayButton.setBackgroundResource(R.color.graylight);
                    mThursdayButtonInd = false;
                }

            }
        });

        mFridayButton = view.findViewById(R.id.butFive);
        if(mFridayButtonInd){
         mFridayButton.setBackgroundResource(R.color.startblue);
        }
        mFridayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mFridayButtonInd) {
                    mFridayButton.setBackgroundResource(R.color.startblue);
                    mFridayButtonInd = true;
                }else{
                    mFridayButton.setBackgroundResource(R.color.graylight);
                    mFridayButtonInd = false;
                }

            }
        });

        mSaturdayButton = view.findViewById(R.id.butSix);
        if(mSaturdayButtonInd){
            mSaturdayButton.setBackgroundResource(R.color.startblue);
        }
        mSaturdayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!mSaturdayButtonInd) {
                    mSaturdayButton.setBackgroundResource(R.color.startblue);
                    mSaturdayButtonInd = true;
                }else{
                    mSaturdayButton.setBackgroundResource(R.color.graylight);
                    mSaturdayButtonInd = false;
                }

            }
        });

        mSundayButton = view.findViewById(R.id.butSeven);
        if(mSundayButtonInd){
            mSundayButton.setBackgroundResource(R.color.startblue);
        }
        mSundayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!mSundayButtonInd) {
                    mSundayButton.setBackgroundResource(R.color.startblue);
                    mSundayButtonInd = true;
                }else{
                    mSundayButton.setBackgroundResource(R.color.graylight);
                    mSundayButtonInd = false;
                }
            }
        });

         return builder.create();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DateTimeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must Implement ExampleDialogListener");
        }

    }

    public interface DateTimeDialogListener{
        void applyTexts(Integer CurrentHour,Integer CurrentMinute,
                        Boolean sundayInd,
                        Boolean mondayInd,Boolean tuedayInd,
                        Boolean weddayInd,Boolean thudayInd,
                        Boolean fridayInd,Boolean satdayInd,
                        Boolean neutrlChk);
    }

}
