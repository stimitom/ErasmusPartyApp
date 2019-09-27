package com.stimitom.erasmuspartyapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class UsernameNationalityDialog extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener {
    private final String TAG = "UserNationalityDialog";
    private EditText editTextUsername;
    private Spinner spinnerNationality;
//    private UsernameNationalityListener listener;
    private Context context = VenuesActivity.reloader;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_name_nationality,null);

        editTextUsername = view.findViewById(R.id.edit_username);
        spinnerNationality = view.findViewById(R.id.spinner_nationality);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(VenuesActivity.reloader,R.array.countries,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNationality.setAdapter(adapter);
        spinnerNationality.setOnItemSelectedListener(this);

        builder.setView(view)
                .setTitle("Introduce Yourself: ")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("That's me!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = editTextUsername.getText().toString();
                        String nationality = spinnerNationality.getSelectedItem().toString();
                        saveUserToDatabase(username,nationality);
//                        listener.transportInputs(username,nationality);
                    }
                });


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

//        try {
//            listener = (UsernameNationalityListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() + "must implement UsernameNationalityListener");
//        }
    }

    //Handle Spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }



//    public interface UsernameNationalityListener{
//        void transportInputs(String username, String nationality);
//    }

    /******************/
    /** Handles upload of User to Database */

    private static final String KEY_USERNAME = "username";
    private static final String KEY_NATIONALITY = "nationality";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERID = "userid";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void saveUserToDatabase(String inputUsername, String inputNationality){
        Map<String,Object> user = new HashMap<>();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        user.put(KEY_USERID, userId);
        user.put(KEY_USERNAME,inputUsername);
        user.put(KEY_NATIONALITY, inputNationality);
        user.put(KEY_EMAIL,userEmail);


        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i(TAG, "user upload to database succesful");
                        Toast.makeText(context,"Have Fun tonight!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "user upload to database FAILES");
                        Log.e(TAG, e.toString());
                    }
                });
    }
}
