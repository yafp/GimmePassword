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
import android.widget.Spinner;

import java.util.Locale;


public class TabXKCD extends Fragment{

    private static final String TAG = "Gimme Password";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Init some UI Items
        //
        Log.d(TAG,"...preparing tab2: XKCD");

        // get view
        View view = inflater.inflate(R.layout.tab_xkcd, container, false);

        // define min- and max- value for password length
        EditText n_passwordLength = view.findViewById(R.id.t2_passwordLength);
        n_passwordLength.setFilters(new InputFilter[]{new InputFilterMinMax("1", "10")});

        // detect device language
        String deviceLanguage;
        deviceLanguage =  Locale.getDefault().getISO3Language(); // get 3 letter ISO code
        Log.v(TAG,"Device language is: "+deviceLanguage+" (ISO-3).");

        // pre-select a language in language-spinner if possible
        Spinner t2_s_language_selection = view.findViewById(R.id.t2_languageSelection);
        switch (deviceLanguage) {
            case "fin":
                Log.v(TAG, "...Pre-selecting finnish");
                t2_s_language_selection.setSelection(1);
                break;
            case "deu":
                Log.v(TAG, "...Pre-selecting german");
                t2_s_language_selection.setSelection(2);
                break;
            case "ita":
                Log.v(TAG, "...Pre-selecting italian");
                t2_s_language_selection.setSelection(3);
                break;
            case "jap":
                Log.v(TAG, "...Pre-selecting japanese");
                t2_s_language_selection.setSelection(4);
                break;
            case "esp":
                Log.v(TAG, "...Pre-selecting spanish");
                t2_s_language_selection.setSelection(5);
                break;
            default:
                Log.v(TAG, "...Pre-selecting english (default)");
                t2_s_language_selection.setSelection(0);
                break;
        }
        return view;
    }
}
