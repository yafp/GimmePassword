package de.yafp.gimmepassword;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import static java.lang.Math.log;
import static java.lang.Math.pow;

public class GimmePassword extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private static final String TAG = "Gimme Password";

    // Tab 3: Katakana
    private char[] chars;
    private int i_passwordLength;

    // #############################################################################################
    // ON CREATE
    // #############################################################################################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // needed for checking password hash online
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // customize action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.app_icon_white);
        getSupportActionBar().setTitle(" " + getResources().getString(R.string.app_name));

        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        /*
        The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections.
        We use a {@link FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory.
        If this becomes too memory intensive, it may be best to switch to a
        {@link android.support.v4.app.FragmentStatePagerAdapter}.
        */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        // The {@link ViewPager} that will host the section contents.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


        // V: 1.2.0
        //
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
                Log.v(TAG, "----------++++++++++++++--------");
                int position = tab.getPosition();
                Log.v(TAG, Integer.toString(position));
                Log.v(TAG, "----------++++++++++++++--------");

                Window w = getWindow();
                w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                switch (position) {
                    case 0:
                        Log.v(TAG,"Selected Tab-1");
                        //w.setStatusBarColor(getResources().getColor(R.color.colorTab1));
                        break;


                    case 1:
                        Log.v(TAG,"Selected Tab-2");
                        // Colorize StatusBar
                        //w.setStatusBarColor(getResources().getColor(R.color.colorTab2));

                        // Colorize ActionBar
                        //Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorTab2)));


                        //Setting up Action bar color using # color code.
                        //
                        //results in null object reference
                        //
                        //ActionBar actionbar = getActionBar();
                        //ActionBar actionbar = getSupportActionBar();
                        //actionbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorTab2)));
                        break;

                    case 2:
                        Log.v(TAG,"Selected Tab-3");
                        //w.setStatusBarColor(getResources().getColor(R.color.colorTab3));
                        break;


                    case 3:
                        Log.v(TAG,"Selected Tab-4");
                        //w.setStatusBarColor(getResources().getColor(R.color.colorTab4));
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d(TAG, "F: onTabUnselected");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(TAG, "F: onTabReselected");
            }
        });

        // Log Firebase Event
        logFireBaseEvent("gp_app_Launch");
    }


    /**
     * generates a firebase log event from a given input string
     *
     * @param message the message text to be logged
     */
    private void logFireBaseEvent(String message) {
        Bundle params = new Bundle();
        params.putString(message, "1");
        mFirebaseAnalytics.logEvent(message, params);
    }


    /**
     * convert to hex
     *
     * @param data
     * @return returns the converted HEX
     */
    private static String convertToHex(byte[] data) {
        Log.d(TAG, "F: convertToHex");

        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }


    /**
     *
     *
     * @param text
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private static String generateSHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        Log.d(TAG, "F: generateSHA1");

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] textBytes = text.getBytes("iso-8859-1");
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }


    /**
     * Displays a given string as a toast message to the user
     *
     * @param message the message to be displayed
     */
    private void displayToastMessage(String message) {
        Log.v(TAG, "F: displayToastMessage");

        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);       // for center vertical
        toast.show();

        // Log Firebase Event
        logFireBaseEvent("gp_display_toast");
    }


    /**
     * show password entropy and ask for pwned query
     *
     * @param password the actual password string which gets forwarded to checkPWNED
     * @param entropy_text the generated entropy text
     * @param entropy_value the generated entropy value
     */
    private void askUser(final String password, String entropy_text, String entropy_value) {
        Log.d(TAG, "F: askUser");

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Log.v(TAG, "...user want to check pwned password for generated password ");
                        try {
                            checkPWNEDPasswords(password);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Log.v(TAG, "...user doesn't want to check pwned password for generated password");
                        break;

                    default:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pw_generation_result_dialog_title);
        builder.setIcon(R.mipmap.app_icon_default);
        builder.setMessage(getResources().getString(R.string.entropy_start_text) + " " + entropy_text + " " + getResources().getString(R.string.entropy_end_text) + " " + entropy_value + " ).\n\n" + getResources().getString(R.string.ask_to_query_pwned)).setPositiveButton(getResources().getString(R.string.pw_generation_result_dialog_yes), dialogClickListener).setNegativeButton(getResources().getString(R.string.pw_generation_result_dialog_no), dialogClickListener).show();

    }


    /**
     * shows an pwned alert dialog
     */
    private void showPwnedAlert() {
        Log.d(TAG, "F: showPwnedAlert");

        AlertDialog alertDialog = new AlertDialog.Builder(GimmePassword.this).create();
        alertDialog.setIcon(R.drawable.dialog_error);
        alertDialog.setTitle(getResources().getString(R.string.pwned_warning_title));
        alertDialog.setMessage(getResources().getString(R.string.pwned_warning_message));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

        // Log Firebase Event
        logFireBaseEvent("gp_pwned_alert");
    }


    /**
     * shows an pwned ok dialog
     */
    private void showPwnedOK() {
        Log.d(TAG, "F: showPwnedOK");

        AlertDialog alertDialog = new AlertDialog.Builder(GimmePassword.this).create();
        alertDialog.setIcon(R.drawable.dialog_ok);
        alertDialog.setTitle(getResources().getString(R.string.pwned_ok_title));
        alertDialog.setMessage(getResources().getString(R.string.pwned_ok_message));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

        // Log Firebase Event
        logFireBaseEvent("gp_pwned_ok");
    }


    /**
     * shows a no-network alert dialog
     */
    private void showNetworkIssuesDialog() {
        Log.d(TAG, "F: showNetworkIssuesDialog");

        AlertDialog alertDialog = new AlertDialog.Builder(GimmePassword.this).create();
        alertDialog.setIcon(R.drawable.dialog_error);
        alertDialog.setTitle(getResources().getString(R.string.pwned_network_issues_title));
        alertDialog.setMessage(getResources().getString(R.string.pwned_network_issues_message));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

        // Log Firebase Event
        logFireBaseEvent("showNetworkIssuesDialog");
    }


    /**
     * adds menu items to the main menu
     *
     * @param menu the menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "F: onCreateOptionsMenu");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * runs when the user selects an entry of the upper right sided menu
     *
     * @param item the selected menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "F: onOptionsItemSelected");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Menu: Issues
        if (id == R.id.action_issues) {
            Uri uri = Uri.parse("https://github.com/yafp/GimmePassword/issues"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

            // Log Firebase Event
            logFireBaseEvent("gp_url_issues");

            return true;
        }

        // Menu: About
        if (id == R.id.action_about) {
            try {
                showAbout();
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Menu: XKCD
        if (id == R.id.action_visit_xkcd) {
            openUrlXKCD();
        }

        // Menu: pwnedpasswords
        if (id == R.id.action_visit_pwned) {
            openUrlPwned();
        }

        // Menu: Recommend Gimme Password
        if (id == R.id.action_recommend_app) {

            // https://developer.android.com/training/sharing/send.html
            //
            /*
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, recommend_message);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            */

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            //share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET); // deprecated since API 21
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);


            // Add data to the intent, the receiving app will decide
            // what to do with it.
            share.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.recommend_text));
            share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=de.yafp.gimmepassword");

            startActivity(Intent.createChooser(share, "Share link"));

            // Log Firebase Event
            logFireBaseEvent("gp_recommend_app");
        }


        return super.onOptionsItemSelected(item);
    }


    /**
     * opens the XKCD #936 url
     */
    private void openUrlXKCD() {
        Log.d(TAG, "F: openUrlXKCD");

        // missing 'http://' will cause crashed
        Uri uri = Uri.parse("https://xkcd.com/936/");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

        // Log Firebase Event
        logFireBaseEvent("gp_url_xkcd");
    }


    /**
     * opens the pwned url
     */
    private void openUrlPwned() {
        Log.d(TAG, "F: openUrlPwned");

        // missing 'http://' will cause crashed
        Uri uri = Uri.parse("https://haveibeenpwned.com/");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

        // Log Firebase Event
        logFireBaseEvent("gp_url_pwned");
    }


    /**
     * opens the about dialog
     *
     * @throws NameNotFoundException
     */
    private void showAbout() throws NameNotFoundException {
        Log.d(TAG, "F: showAbout");

        // Inflate the about message contents
        @SuppressLint("InflateParams") View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        // Get package informations
        PackageManager manager = this.getPackageManager();
        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.app_icon_default);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.setMessage("\nPackage:\t\t" + info.packageName + "\nVersion:\t\t\t" + info.versionName + "\nBuild:\t\t\t\t" + info.versionCode);
        builder.create();
        builder.show();

        // Log Firebase Event
        logFireBaseEvent("gp_show_about");
    }


    /**
     * calculates the password entropy of a given password with a defined length & amount of characters in its charset.
     * In addition a string describing the password quality is generated as well
     *
     * regarding password quality:
     *  simple example: https://www.bee-man.us/computer/password_strength.html
     *
     *  < 28 bits        = Very Weak; might keep out family members
     *  28 - 35 bits     = Weak; should keep out most people, often good for desktop login passwords
     *  36 - 59 bits     = Reasonable; fairly secure passwords for network and company passwords
     *  60 - 127 bits    = Strong; can be good for guarding financial information
     *  128+ bits        = Very Strong; often overkill
     *
     * @param length the password lenth
     * @param characters the size of the character set used for password generation
     * @return
     */
    private String[] calculateEntropy(int length, int characters) {
        Log.d(TAG, "F: calculateEntropy");

        // calculate entropy
        double step1 = pow(characters, length);
        double step2 = log(step1) / log(2);

        // round to 1 decimal
        double total = Math.round(step2 * 10d) / 10d;

        // calculated entropy as string
        String password_entropy = Double.toString(total);


        String password_quality;
        if (total > 128) {
            password_quality = getResources().getString(R.string.entropy_very_strong);
        } else if (total > 60) {
            password_quality = getResources().getString(R.string.entropy_quiet_strong);
        } else if (total > 35) {
            password_quality = getResources().getString(R.string.entropy_reasonable);
        } else if (total > 28) {
            password_quality = getResources().getString(R.string.entropy_weak);
        } else {
            password_quality = getResources().getString(R.string.entropy_very_weak);
        }

        // Log entropy results
        Log.i(TAG, "...Size of charset: " + characters);
        Log.i(TAG, "...Password length: " + length);
        Log.i(TAG, "...Password quality is: " + password_quality);
        Log.i(TAG, "...Password entropy is: " + password_entropy);

        // Log Firebase Event
        logFireBaseEvent("gp_calculated_entropy");

        return new String[]{password_quality, password_entropy};
    }


    /**
     * starts the pwned password processing routine.
     *
     *  Some Links:
     *  - https://www.troyhunt.com/i-wanna-go-fast-why-searching-through-500m-pwned-passwords-is-so-quick/
     *  - https://stackoverflow.com/questions/5980658/how-to-sha1-hash-a-string-in-android
     *  - https://haveibeenpwned.com/API/v2#PwnedPasswords
     *
     *  Method 1:
     *  GET https://api.pwnedpasswords.com/pwnedpassword/{password or hash}
     *
     *  When a password is found in the Pwned Passwords repository, the API will respond with HTTP 200 and include a count in the response body indicating how many times that password appears in the data set.
     *  When no match is found, the response code is HTTP 404.
     *
     *
     *  Method 2:
     *  GET https://api.pwnedpasswords.com/range/{first 5 hash chars}
     *
     *  When a password hash with the same first 5 characters is found in the Pwned Passwords repository,
     *  the API will respond with an HTTP 200 and include the suffix of every hash beginning with the specified prefix,
     *  followed by a count of how many times it appears in the data set.
     *  The API consumer can then search the results of the response for the presence of their
     *  source hash and if not found, the password does not exist in the data set.
     *
     * @param password the generated password string
     * @throws IOException
     */
    private void checkPWNEDPasswords(String password) throws IOException {
        Log.d(TAG, "F: checkPWNEDPasswords");

        // Log Firebase Event
        logFireBaseEvent("gp_pwned_check_started");

        // generate sha-1 of password
        String hashstring = null;
        try {
            hashstring = generateSHA1(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "SHA-1: " + hashstring);

        // generate substring (5 first chars of hash)
        String hashstring_5 = null;
        if (hashstring != null) {
            hashstring_5 = hashstring.substring(0, 5);
        }
        Log.i(TAG, "...SHA-1 (first 5 chars): " + hashstring_5);
        Log.i(TAG, "...should check: https://api.pwnedpasswords.com/range/" + hashstring_5);

        // Start online check
        boolean success;
        // Step 1: Check the 5 digit hash string
        success = performGet("https://api.pwnedpasswords.com/range/" + hashstring_5);
        if (success) {
            Log.w(TAG, "...bummer, the 5-digit-hashstring is known at pwnedpasswords.com.");
            Log.i(TAG, "...Should continue by checking the entire hash");

            // Step 2: found 5 digit hashstring in DB, now check the entire hash
            success = performGet("https://api.pwnedpasswords.com/pwnedpassword/" + hashstring);
            if (success) {
                Log.w(TAG, "...major bummer, the complete password hash is known at pwnedpasswords.com");

                // show warning dialog
                showPwnedAlert();

            } else {
                Log.i(TAG, "...the complete password hash is not known at pwnedpasswords.com.");

                // show ok dialog
                showPwnedOK();
            }
        }
    }


    /**
     * helper for pwned password check routine
     *
     * @param target_url
     * @return
     */
    private boolean performGet(String target_url) {
        Log.d(TAG, "F: performGet");

        try {
            URL url = new URL(target_url);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            if (builder.length() == 0) {
                Log.i(TAG, "...no answer");
            } else {
                return true;
            }
        } catch(UnknownHostException e) {
            e.printStackTrace();
            Log.w(TAG, "Unable to contact target_url. Most likely there is no network available.");
            showNetworkIssuesDialog();
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, "Unknown issue while trying to connect to a remote URL.");
        }
        return false;
    }


    /**
     * geneated a random default password
     *
     * @param v the view
     */
    @SuppressWarnings("unused")
    @SuppressLint("SetTextI18n")
    public void onGenerateDefault(View v) {
        Log.d(TAG, "F: onGenerateDefault");

        TextView t1_generatedPassword;

        // Reset content of password field
        t1_generatedPassword = findViewById(R.id.t1_generatedPassword);
        t1_generatedPassword.setText(null);

        // define charsets
        String charPool_uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; //26
        String charPool_lowercaseLetters = "abcdefghijklmnopqrstuvwxyz"; //26
        String charPool_numbers = "0123456789"; // 10
        String charPool_specialChars = "?!.;:,-_+*/\\|<>{[()]}#&%$§@€^`´~"; // 32

        String allowedChars = "";
        Random random;

        // checkboxes
        CheckBox t1_cb_uppercaseLetters;
        CheckBox t1_cb_lowercaseLetters;
        CheckBox t1_cb_numbers;
        CheckBox t1_cb_specialChars;

        // checkboxes
        t1_cb_uppercaseLetters = findViewById(R.id.t1_cb_uppercaseLetters);
        t1_cb_lowercaseLetters = findViewById(R.id.t1_cb_lowercaseLetters);
        t1_cb_numbers = findViewById(R.id.t1_cb_numbers);
        t1_cb_specialChars = findViewById(R.id.t1_cb_specialChars);

        // get checkbox state: uppercase
        if (t1_cb_uppercaseLetters.isChecked()) {
            Log.i(TAG, "...adding uppercase to character pool");
            allowedChars = allowedChars + charPool_uppercaseLetters;
        }

        // get checkbox state: lowercase
        if (t1_cb_lowercaseLetters.isChecked()) {
            Log.i(TAG, "...adding lowercase to character pool");
            allowedChars = allowedChars + charPool_lowercaseLetters;
        }

        // get checkbox state: numbers
        if (t1_cb_numbers.isChecked()) {
            Log.i(TAG, "...adding numbers to character pool");
            allowedChars = allowedChars + charPool_numbers;
        }

        // get checkbox state: special chars
        if (t1_cb_specialChars.isChecked()) {
            Log.i(TAG, "...adding special chars to character pool");
            allowedChars = allowedChars + charPool_specialChars;
        }

        // Check if at least 1 pool is selected or not
        //if (allowedChars.equals("")) {
        if(("").equals(allowedChars)) {
            // get error string
            String cur_error = getResources().getString(R.string.t1_error_empty_char_pool);

            // Show error in log
            Log.e(TAG, cur_error);

            // show error as toast
            displayToastMessage(cur_error);
        } else {
            Log.i(TAG, "...character pool is configured to: " + allowedChars);

            // get password length
            EditText n_passwordLength = findViewById(R.id.t1_passwordLength);
            String s_passwordLength = n_passwordLength.getText().toString().trim();

            if ((s_passwordLength.equals("0")) || (s_passwordLength.isEmpty()) || (s_passwordLength.equals(""))) {
                Log.w(TAG, "...invalid password length detected, changing to default");
                i_passwordLength = 10;
                n_passwordLength.setText(Integer.toString(i_passwordLength), TextView.BufferType.EDITABLE);
            } else {
                i_passwordLength = Integer.parseInt(s_passwordLength);
            }
            Log.i(TAG, "...password length is set to " + Integer.toString(i_passwordLength));

            // password generation
            char[] allowedCharsArray = allowedChars.toCharArray();
            chars = new char[i_passwordLength];
            random = new Random();
            for (int i = 0; i < i_passwordLength; i++) {
                chars[i] = allowedCharsArray[random.nextInt(allowedChars.length())];
            }

            // display the new password
            t1_generatedPassword.setText(chars, 0, i_passwordLength);

            // calculate entropy
            String entropy_results[];
            entropy_results = calculateEntropy(i_passwordLength, allowedChars.length());

            // show entropy results
            String entropy_text;
            entropy_text = entropy_results[0];
            String entropy_value;
            entropy_value = entropy_results[1];

            // Resulting password as string
            String generatedPassword = t1_generatedPassword.getText().toString();

            // Log Firebase Event
            logFireBaseEvent("gp_generate_default");

            // show result
            askUser(generatedPassword, entropy_text, entropy_value);
        }
    }


    /**
     * generates a XKCD 936 like password, using a language based wordlist.
     * wordlist sources:
     * - https://github.com/redacted/XKCD-password-generator/tree/master/xkcdpass/static
     *
     * @param v the view
     * @throws IOException
     */
    @SuppressWarnings("unused")
    @SuppressLint("SetTextI18n")
    public void onGenerateXKCD(View v) throws IOException {
        Log.d(TAG, "F: onGenerateXKCD");

        TextView t2_generatedPassword;

        // Reset content of password field
        t2_generatedPassword = findViewById(R.id.t2_generatedPassword);
        t2_generatedPassword.setText(null);

        Random randomGenerator = new Random();

        // get amount of words
        EditText n_passwordLength = findViewById(R.id.t2_passwordLength);
        String s_passwordLength = n_passwordLength.getText().toString().trim();

        if ((s_passwordLength.equals("0")) || (s_passwordLength.isEmpty()) || (s_passwordLength.equals(""))) {
            Log.w(TAG, "...invalid amount of words, changing to default");
            i_passwordLength = 4;
            n_passwordLength.setText(Integer.toString(i_passwordLength), TextView.BufferType.EDITABLE);
        } else {
            i_passwordLength = Integer.parseInt(s_passwordLength);
        }
        Log.i(TAG, "...amount of words is set to " + Integer.toString(i_passwordLength));

        // get selected language
        Spinner t2_s_language_selection = findViewById(R.id.t2_languageSelection);
        String selected_language = t2_s_language_selection.getSelectedItem().toString();
        Log.i(TAG, "...selected language: " + selected_language);

        String name_of_language_wordlist;

        // Can't use resource strings in switch-statement because of
        // Error: Constant expression required
        // Thats why we are using an uly if/else
        if (selected_language.equals(getResources().getString(R.string.t2_lang_es))) {
            name_of_language_wordlist = "words_es.txt";
        } else if (selected_language.equals(getResources().getString(R.string.t2_lang_jp))) {
            name_of_language_wordlist = "words_jp.txt";
        } else if (selected_language.equals(getResources().getString(R.string.t2_lang_it))) {
            name_of_language_wordlist = "words_it.txt";
        } else if (selected_language.equals(getResources().getString(R.string.t2_lang_de))) {
            name_of_language_wordlist = "words_de.txt";
        } else if (selected_language.equals(getResources().getString(R.string.t2_lang_fi))) {
            name_of_language_wordlist = "words_fi.txt";
        } else {
            name_of_language_wordlist = "words_en.txt";
        }

        Log.i(TAG, "...Selected wordlist: " + name_of_language_wordlist);

        // read selected file line by line
        List<String> myWords;
        myWords = new ArrayList<>();

        InputStream in;
        BufferedReader reader;
        String line;

        in = this.getAssets().open(name_of_language_wordlist);
        reader = new BufferedReader(new InputStreamReader(in));
        line = reader.readLine();
        while (line != null) {
            // add current line to List
            myWords.add(line);
            line = reader.readLine();
        }
        in.close();

        Log.i(TAG, "...available words in this language-list: " + Integer.toString(myWords.size()));

        // get seperator
        Spinner t2_s_separator_selection = findViewById(R.id.t2_seperatorSelection);
        String selected_separator = t2_s_separator_selection.getSelectedItem().toString();
        Log.i(TAG, "...selected separator: " + selected_separator);

        // generate xkcd password from wordlist
        StringBuilder generatedPassword = new StringBuilder();
        for (int i = 0; i < i_passwordLength; i++) {
            // generate a random int
            int randomInt = randomGenerator.nextInt(myWords.size());

            // pick random word based on random int
            String wordToDisplay = myWords.get(randomInt);

            // Make Uppercase first char
            wordToDisplay = wordToDisplay.substring(0, 1).toUpperCase(Locale.getDefault()) + wordToDisplay.substring(1);

            // append to current password
            generatedPassword.append(wordToDisplay);

            // add a splitting char between words if needed
            if (i + 1 < i_passwordLength) {
                //generatedPassword.append("-");
                generatedPassword.append(selected_separator);
            }
        }

        // display the new password
        chars = generatedPassword.toString().toCharArray();
        t2_generatedPassword.setText(chars, 0, generatedPassword.length());

        // https://www.explainxkcd.com/wiki/index.php/936:_Password_Strength
        String entropy_results[];
        entropy_results = calculateEntropy(i_passwordLength, myWords.size());

        // show entropy results
        String entropy_text;
        entropy_text = entropy_results[0];
        String entropy_value;
        entropy_value = entropy_results[1];

        generatedPassword = new StringBuilder(t2_generatedPassword.getText().toString());

        // Log Firebase Event
        logFireBaseEvent("gp_generate_xkcd");

        // run result dialog for user
        askUser(generatedPassword.toString(), entropy_text, entropy_value);
    }


    /**
     * generates a katakana like influenced password
     *
     * @param v the view
     */
    @SuppressWarnings("unused")
    @SuppressLint("SetTextI18n")
    public void onGenerateKatakana(View v) {
        Log.d(TAG, "onGenerateKatakana");

        TextView t3_generatedPassword;

        // Reset content of password field
        t3_generatedPassword = findViewById(R.id.t3_generatedPassword);
        t3_generatedPassword.setText(null);

        // init some stuff
        StringBuilder generatedPassword = new StringBuilder();
        Random random;
        int index_c;
        int index_v;

        // Define charsets
        final String[] consonants = {"k", "s", "t", "n", "h", "m", "y", "r", "w", "f", "g", "z", "d", "b", "p", "K", "S", "T", "N", "H", "M", "Y", "R", "W", "F", "G", "Z", "D", "B", "P"}; // katakana + bonus
        final String[] vowels = {"a", "i", "u", "e", "o", "A", "U", "E", "O"}; // skipping uppercase i

        // get password length
        EditText n_passwordLength = findViewById(R.id.t3_passwordLength);

        String s_passwordLength = n_passwordLength.getText().toString().trim();

        if ((s_passwordLength.equals("0")) || (s_passwordLength.isEmpty()) || (s_passwordLength.equals(""))) {
            Log.w(TAG, "...invalid password length detected, changing to default");
            i_passwordLength = 10;
            n_passwordLength.setText(Integer.toString(i_passwordLength), TextView.BufferType.EDITABLE);
        } else {
            i_passwordLength = Integer.parseInt(s_passwordLength);
        }
        Log.i(TAG, "...password length is set to " + Integer.toString(i_passwordLength));

        // generate password
        Log.i(TAG, "...generating password");
        for (int i = 0; i < i_passwordLength; i++) {
            // Odd: pick random consonants array string
            random = new Random();
            index_c = random.nextInt(consonants.length);

            // Even: pick random vowels array string
            random = new Random();
            index_v = random.nextInt(vowels.length);

            generatedPassword.append(consonants[index_c]).append(vowels[index_v]);
        }
        generatedPassword = new StringBuilder(generatedPassword.substring(0, i_passwordLength)); // Substring to match password length

        // display the new password
        chars = generatedPassword.toString().toCharArray();
        t3_generatedPassword.setText(chars, 0, i_passwordLength);

        // entropy
        int allowedChars = consonants.length + vowels.length; // get charset size

        String entropy_results[]; // prepare array for entropy values
        entropy_results = calculateEntropy(i_passwordLength, allowedChars); // get entropy values

        // show entropy results
        String entropy_text;
        entropy_text = entropy_results[0];

        String entropy_value;
        entropy_value = entropy_results[1];

        generatedPassword = new StringBuilder(t3_generatedPassword.getText().toString());

        // Log Firebase Event
        logFireBaseEvent("gp_generate_kana");

        // run result dialog for user
        askUser(generatedPassword.toString(), entropy_text, entropy_value);
    }


    /**
     * starts the pwned password check of a user given password
     *
     * @param v the view
     */
    @SuppressWarnings("unused")
    public void onClickQueryPwnedDB(View v) {
        Log.d(TAG, "F: onClickQueryPwnedDB");

        final String userPassword;

        // get text from textedit
        EditText t4_userPassword;
        t4_userPassword = findViewById(R.id.t4_userPassword);
        userPassword = t4_userPassword.getText().toString();

        if (userPassword.length() == 0) {
            // get error string
            String cur_error = getResources().getString(R.string.t4_error_password);

            // Show error in log
            Log.e(TAG, cur_error);

            // show error as toast
            displayToastMessage(cur_error);
        } else {
            try {
                checkPWNEDPasswords(userPassword);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d(TAG, "F: onCreateView");
            return null;
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            Log.d(TAG, "F: SectionsPagerAdapter");
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "F: getItem");
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    Log.v(TAG,"Tab 1");
                    return new TabDefault();

                case 1:
                    Log.v(TAG,"Tab 2");
                    return new TabXKCD();

                case 2:
                    Log.v(TAG,"Tab 3");
                    return new TabKana();

                case 3:
                    Log.v(TAG,"Tab 4");
                    return new TabPwned();

                default:
                    return null;
            }
        }


        @Override
        public int getCount() {
            Log.d(TAG, "F: getCount");
            return 4;
        }

    }
}