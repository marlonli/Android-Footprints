package com.example.jingyuan.footprints;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wyh.slideAdapter.ItemBind;
import com.wyh.slideAdapter.ItemView;
import com.wyh.slideAdapter.SlideAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int SEARCH_FOR_FRIENDS_REQ = 1;
    private static final String NEW_FRIEND = "new_friend";
    private static final String MY_USERNAME = "username";
    private static final String MY_PROFILE = "profile";
    private static final String PERSON_OBJECT = "person";
    private static final String PERSON_POSITION = "position";
    private static final int OPEN_PROFILE_REQ = 2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public List<User> friends = new ArrayList<>();

    private MyFriendRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
//    private FloatingActionButton fab;

    private OnFragmentInteractionListener mListener;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v("PeopleFragment", "onCreateView");

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friends, container, false);

        // Set action bar menu
        setHasOptionsMenu(true);

//        fab = getActivity().findViewById(R.id.fab_newjournal);

        // Initialization
        friends = new ArrayList<>();
        addTestData();
//        Collections.sort(friends, new JournalsComparator());

        // RecyclerView settings
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView_friends);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                LinearLayout.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new MyFriendRecyclerViewAdapter(friends);
        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
//                if (dy > 0)
//                    fab.hide();
//                else if (dy < 0)
//                    fab.show();
//            }
//        });

        return v;
    }

    private void addTestData() {
        friends.add(new User("Lalalala", "123456"));
        friends.add(new User("Lalalala1", "123456"));
        friends.add(new User("Lalalala2", "123456"));
        friends.add(new User("Lalalala3", "123456"));
        friends.add(new User("Lalalala4", "123456"));
        friends.add(new User("Lalalala5", "123456"));
        friends.add(new User("Lalalala6", "123456"));
        friends.add(new User("Lalalala7", "123456"));
        friends.add(new User("Lalalala8", "123456"));
        friends.add(new User("Lalalala9", "123456"));
        friends.add(new User("Lalalala0", "123456"));
        friends.add(new User("Lalalala11", "123456"));
        friends.add(new User("Lalalala12", "123456"));
        friends.add(new User("Lalalala13", "123456"));
        friends.add(new User("Lalalala14", "123456"));

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        // Click item to open profile
        mAdapter.setOnItemClickListener(new MyFriendRecyclerViewAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
                openProfile(position);

            }
        });
    }

    private void openProfile(int position) {
        Intent intent = new Intent(getActivity(), MyProfileActivity.class);
        intent.putExtra(PERSON_OBJECT, friends.get(position));
        intent.putExtra(PERSON_POSITION, position);

        startActivityForResult(intent, OPEN_PROFILE_REQ);
    }

    // Add menu to action bar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_friends, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_person:
                // User chose the "add" item
                // TODO: search friends
                searchFriends();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void searchFriends() {
        Intent intent = new Intent(getActivity(), SearchFriendsActivity.class);
//        intent.putExtra(JOURNAL_OBJECT, journals.get(journalIndex));
        startActivityForResult(intent, SEARCH_FOR_FRIENDS_REQ);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("fragment status", "onActivityResult");
        if (requestCode == Activity.RESULT_FIRST_USER && data != null) {
            if (resultCode == SEARCH_FOR_FRIENDS_REQ) {
                // Add a new friend
                User u = (User) data.getSerializableExtra(NEW_FRIEND);
                // TODO: write to  database to modify friends list

//                mdapter.notifyDataSetChanged();
            } else if (resultCode == OPEN_PROFILE_REQ) {
                // Change username or profile
                User u = (User) data.getSerializableExtra(PERSON_OBJECT);
                // TODO: modify my profile if changed
                // set my profile
//                User me = friends.get(0);
//                me.setProfile(u.getProfile());
//                me.setUsername(u.getUsername());
//                mRecyclerView.notify
            }
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
