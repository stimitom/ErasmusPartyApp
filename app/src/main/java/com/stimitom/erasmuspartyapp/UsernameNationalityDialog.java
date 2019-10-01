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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class UsernameNationalityDialog extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener {
    private final String TAG = "UserNationalityDialog";
    private EditText editTextUsername;
    private Spinner spinnerNationality;
    private Context context = VenuesListActivity.reloader;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_name_nationality, null);

        editTextUsername = view.findViewById(R.id.edit_username);
        spinnerNationality = view.findViewById(R.id.spinner_nationality);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(VenuesListActivity.reloader, R.array.countries, android.R.layout.simple_spinner_item);
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
                        saveUserToDatabase(username, nationality);
                    }
                });


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    //Handle Spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    /******************/
    /**
     * Database methods concerning User
     */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef =  db.collection("users");

    public void saveUserToDatabase(String inputUsername, String inputNationality) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        User user = new User(userId,inputUsername,inputNationality,userEmail);
        usersRef.document(userId)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "user upload to database succesful");
                        Toast.makeText(context, "Have Fun tonight!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "user upload to database FAILED");
                        Log.e(TAG, e.toString());
                    }
                });
    }

}

/*
 //Retrieve a user's data from the database with the userId of the current user
        public void loadUserFromDatabase(String userId) {
            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Map<String, Object> user = documentSnapshot.getData();

                            } else {
                                //is executed when user is not registered or logged in
                                Log.e(TAG, "No loadable document exists");
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "failure on loading userdata", e);
                        }
                    });
    }
 */