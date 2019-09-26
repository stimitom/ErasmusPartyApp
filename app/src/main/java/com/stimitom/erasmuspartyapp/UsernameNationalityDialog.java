package com.stimitom.erasmuspartyapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class UsernameNationalityDialog extends AppCompatDialogFragment {

    private EditText editTextUsername;
    private EditText editTextNationality;
    private UsernameNationalityListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_name_nationality,null);

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
                        String nationality = editTextNationality.getText().toString();
                        listener.transportInputs(username,nationality);
                    }
                });
        editTextUsername = view.findViewById(R.id.edit_username);
        editTextNationality = view.findViewById(R.id.edit_nationality);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (UsernameNationalityListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement UsernameNationalityListener");
        }
    }

    public interface UsernameNationalityListener{
        void transportInputs(String username, String nationality);
    }
}
