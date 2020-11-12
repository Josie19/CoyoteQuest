package com.example.roomquest2019;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RepairGrid extends AppCompatDialogFragment implements View.OnClickListener{
    Button repairbutton1, repairbutton2, repairbutton3;
    GridDialog.Communicator communicator;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        communicator= (GridDialog.Communicator) context;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

        View view=inflater.inflate(R.layout.repair_layout, null);

        repairbutton1= view.findViewById(R.id.repair_button_one);
        repairbutton1.setOnClickListener(this);

        repairbutton2= view.findViewById(R.id.repair_button_two);
        repairbutton2.setOnClickListener(this);

        repairbutton3= view.findViewById(R.id.repair_button_three);
        repairbutton3.setOnClickListener(this);



        /*Sets the dialog box to disappear when you click outside of it.*/
        setCancelable(true);
        //return super.onCreateView(inflater, container, savedInstanceState);
/*  inflate(int resource, ViewGroup root)

    Inflate a new view hierarchy from the specified xml resource.
    This method will link our appearance to our dialog.

    1st parameters: name of the XML file that contains the description of our dialog's appearance.
    2nd parameter:  not needed, hence null, because the dialog fragment is completely separate from the main activity,
                    so you don't need the view group parent passed over here.*/
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.repair_button_one)
        {
            communicator.onDialogMessage("Fire Hydrants");
            dismiss();
        }
        else if(view.getId()==R.id.repair_button_two)
        {
            communicator.onDialogMessage("Pipes/Valves");
            dismiss();
        }
        else if(view.getId()==R.id.repair_button_three)
        {
            communicator.onDialogMessage("Electrical Lines");
            dismiss();
        }

    }

    interface Communicator
    {
        void onDialogMessage(String message);
    }
}
/*
* repairbutton4= view.findViewById(R.id.repair_button_four);
        repairbutton4.setOnClickListener(this);

        repairbutton5= view.findViewById(R.id.repair_button_five);
        repairbutton5.setOnClickListener(this);

        repairbutton6= view.findViewById(R.id.repair_button_six);
        repairbutton6.setOnClickListener(this);

        repairbutton7= view.findViewById(R.id.repair_button_seven);
        repairbutton7.setOnClickListener(this);

        repairbutton8= view.findViewById(R.id.repair_button_eight);
        repairbutton8.setOnClickListener(this);

        repairbutton9= view.findViewById(R.id.repair_button_nine);
        repairbutton9.setOnClickListener(this);

        repairbutton10= view.findViewById(R.id.repair_button_ten);
        repairbutton10.setOnClickListener(this);

        repairbutton11= view.findViewById(R.id.repair_button_eleven);
        repairbutton11.setOnClickListener(this);

        repairbutton12= view.findViewById(R.id.repair_button_twelve);
        repairbutton12.setOnClickListener(this);
        * else if(view.getId()==R.id.repair_button_four)
        {
            communicator.onDialogMessage("Information Centers");
            dismiss();
        }
        else if(view.getId()==R.id.repair_button_five)
        {
            communicator.onDialogMessage("Campus Shuttle");
            dismiss();
        }
        else if(view.getId()==R.id.repair_button_six)
        {
            communicator.onDialogMessage("Emergency Phones");
            dismiss();
        }
        else if(view.getId()==R.id.repair_button_seven)
        {
            communicator.onDialogMessage("Restrooms");
            dismiss();
        }
        else if(view.getId()==R.id.repair_button_eight)
        {
            communicator.onDialogMessage("Electrical Lines");
            dismiss();
        }
        else if(view.getId()==R.id.repair_button_nine)
        {
            communicator.onDialogMessage("Health Center");
            dismiss();
        }
        else if(view.getId()==R.id.repair_button_ten)
        {
            communicator.onDialogMessage("ATM");
            dismiss();
        }
        else if(view.getId()==R.id.repair_button_eleven)
        {
            communicator.onDialogMessage("Evacuation Sites");
            dismiss();
        }
        else if(view.getId()==R.id.repair_button_twelve)
        {
            communicator.onDialogMessage("Dining");
            dismiss();
        }
        * , repairbutton4, repairbutton5, repairbutton6, repairbutton7,
            repairbutton8, repairbutton9, repairbutton10, repairbutton11, repairbutton12*/