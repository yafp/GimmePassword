package de.yafp.gimmepassword;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Password Generation Tab - Typ; Default
 */
public class TabDefault extends Fragment{

    private static final String TAG = "Gimme Password";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Init some UI Items
        //
        Log.d(TAG,"...preparing tab1: default");

        // get view
        View view = inflater.inflate(R.layout.tab_default, container, false);

        // define min- and max- value for password length
        EditText n_passwordLength = view.findViewById(R.id.t1_passwordLength);
        n_passwordLength.setFilters(new InputFilter[]{new InputFilterMinMax("1", "64")});

        return view;
    }
}
