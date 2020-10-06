/*Creating a dialog box for when the button is pushed
 * Author(s): Thomas Saldana, Nicholas Perez*/

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

/*Defining the dialog box blueprint or class so we can have a dialog box that is displayed upon pushing the button.*/
public class GridDialog extends AppCompatDialogFragment implements View.OnClickListener{
    Button gridbutton1, gridbutton2, gridbutton3, gridbutton4, gridbutton5, gridbutton6, gridbutton7,
            gridbutton8, gridbutton9, gridbutton10, gridbutton11, gridbutton12;
    Communicator communicator;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        communicator= (Communicator) context;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

        View view=inflater.inflate(R.layout.grid_layout, null);

        gridbutton1= view.findViewById(R.id.grid_button_one);
        gridbutton1.setOnClickListener(this);

        gridbutton2= view.findViewById(R.id.grid_button_two);
        gridbutton2.setOnClickListener(this);

        gridbutton3= view.findViewById(R.id.grid_button_three);
        gridbutton3.setOnClickListener(this);

        gridbutton4= view.findViewById(R.id.grid_button_four);
        gridbutton4.setOnClickListener(this);

        gridbutton5= view.findViewById(R.id.grid_button_five);
        gridbutton5.setOnClickListener(this);

        gridbutton6= view.findViewById(R.id.grid_button_six);
        gridbutton6.setOnClickListener(this);

        gridbutton7= view.findViewById(R.id.grid_button_seven);
        gridbutton7.setOnClickListener(this);

        gridbutton8= view.findViewById(R.id.grid_button_eight);
        gridbutton8.setOnClickListener(this);

        gridbutton9= view.findViewById(R.id.grid_button_nine);
        gridbutton9.setOnClickListener(this);

        gridbutton10= view.findViewById(R.id.grid_button_ten);
        gridbutton10.setOnClickListener(this);

        gridbutton11= view.findViewById(R.id.grid_button_eleven);
        gridbutton11.setOnClickListener(this);

        gridbutton12= view.findViewById(R.id.grid_button_twelve);
        gridbutton12.setOnClickListener(this);

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
        if(view.getId()==R.id.grid_button_one)
        {
            communicator.onDialogMessage("Bicycle Racks");
            dismiss();
        }
        else if(view.getId()==R.id.grid_button_two)
        {
            communicator.onDialogMessage("Parking Permit Dispensers");
            dismiss();
        }
        else if(view.getId()==R.id.grid_button_three)
        {
            communicator.onDialogMessage("Disability Parking Areas");
            dismiss();
        }
        else if(view.getId()==R.id.grid_button_four)
        {
            communicator.onDialogMessage("Information Centers");
            dismiss();
        }
        else if(view.getId()==R.id.grid_button_five)
        {
            communicator.onDialogMessage("Campus Shuttle");
            dismiss();
        }
        else if(view.getId()==R.id.grid_button_six)
        {
            communicator.onDialogMessage("Emergency Phones");
            dismiss();
        }
        else if(view.getId()==R.id.grid_button_seven)
        {
            communicator.onDialogMessage("Restrooms");
            dismiss();
        }
        else if(view.getId()==R.id.grid_button_eight)
        {
            communicator.onDialogMessage("EV Charging Stations");
            dismiss();
        }
        else if(view.getId()==R.id.grid_button_nine)
        {
            communicator.onDialogMessage("Health Center");
            dismiss();
        }
        else if(view.getId()==R.id.grid_button_ten)
        {
            communicator.onDialogMessage("ATM");
            dismiss();
        }
        else if(view.getId()==R.id.grid_button_eleven)
        {
            communicator.onDialogMessage("Evacuation Sites");
            dismiss();
        }
        else if(view.getId()==R.id.grid_button_twelve)
        {
            communicator.onDialogMessage("Dining");
            dismiss();
        }
    }

    interface Communicator
    {
        void onDialogMessage(String message);
    }
}
