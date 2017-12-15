package com.example.jingyuan.footprints;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private DatabaseReference mDatabase;
    private DatabaseReference usersRef;
    private boolean access = false;
    private static String DatabaseName = "New_users";
    private BottomNavigationView navigationView;
    private FloatingActionButton fab;
    private static final String ARG_PARAM1 = "param1";

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        usersRef = mDatabase.child(DatabaseName);
        navigationView = (BottomNavigationView)getActivity().findViewById(R.id.navigation);
        fab = (FloatingActionButton)getActivity().findViewById(R.id.fab_newjournal);

        // get reference of buttons
        final View myView = inflater.inflate(R.layout.fragment_login, container, false);
        Button loginBtn = (Button)myView.findViewById(R.id.loginButton);
        Button createNewBtn = (Button)myView.findViewById(R.id.createButton);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "sign in button", Toast.LENGTH_SHORT).show();
                // get user input
                final String userName = ((EditText)myView.findViewById(R.id.userName)).getText().toString();
                final String passWord = ((EditText)myView.findViewById(R.id.passWord)).getText().toString();

                // load data from database
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String password = "";
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            String name = (String) userSnapshot.child("username").getValue();
                            if(name.equals(userName)){
                                password = (String) userSnapshot.child("password").getValue();
                                break;
                            }
                        }
                        if(password.equals(""))
                            Toast.makeText(getActivity().getApplicationContext(), "No such user", Toast.LENGTH_SHORT).show();
                        else {
                            if (password.equals(passWord))
                                access = true;
                            else
                                Toast.makeText(getActivity().getApplicationContext(), "Password does not match", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if(access){
                    // make navigation bar and fab visible
                    navigationView.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
                    // move to journal fragment
                    String para1 = "journal_para1";
                    String para2 = "journal_para2";
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, JournalFragment.newInstance(userName), "Journal").addToBackStack(null).commit();
                }
            }
        });

        createNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userName = ((EditText)myView.findViewById(R.id.userName)).getText().toString();
                final String passWord = ((EditText)myView.findViewById(R.id.passWord)).getText().toString();
                ArrayList<Journal> journalList = null;

                if(userName.equals(""))
                    Toast.makeText(getContext(), "Please Enter User Name", Toast.LENGTH_SHORT).show();
                if(passWord.equals(""))
                    Toast.makeText(getContext(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                if(!userName.equals("") && !passWord.equals("")) {
                    User newUser = new User(userName, passWord);
                    mDatabase.child(DatabaseName).child(newUser.getUsername()).setValue(newUser);

                    // make navigation bar and fab visible
                    navigationView.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);

                    // move to journal fragment
                    String para1 = "journal_para1";
                    String para2 = "journal_para2";
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, JournalFragment.newInstance(userName), "Journal").addToBackStack(null).commit();
                }
            }
        });

        // Inflate the layout for this fragment
        return myView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
