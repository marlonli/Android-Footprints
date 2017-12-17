package com.example.jingyuan.footprints;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
    private static final String NEW_FRIEND = "new_friend";
    private static final String MY_USERNAME = "username";
    private static final String MY_PROFILE = "profile";
    private static final String PERSON_OBJECT = "person";
    private static final String PERSON_POSITION = "position";
    private static final int SEARCH_FOR_FRIENDS_REQ = 1;
    private static final int OPEN_PROFILE_REQ = 2;
    private static final int FRIENDS_FRAGMENT_REQ = 1000;
    private static final String DBName = "New_users";

    private String username;
    public List<User> friends = new ArrayList<>();

    private MyFriendRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
//    private FloatingActionButton fab;

    private OnFragmentInteractionListener mListener;
    private DatabaseReference mDatabase;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 username.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_PARAM1);

            mDatabase = FirebaseDatabase.getInstance().getReference(DBName);
            // add current user as first element in list "friends"
            //final User myself = new User(username);
            //friends.add(0, myself);
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
//        fab = getActivity().findViewById(R.id.fab_newjournal);

        // Initialization
        //friends = new ArrayList<>();
        //addTestData();
        // load friends from database, use callback for synchronization
        loadFriends(username, new LoadDataCallback() {
            @Override
            public void loadFinish() {
                mAdapter.notifyDataSetChanged();
            }
        });
//        Collections.sort(friends, new JournalsComparator());

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

    private void loadFriends(String name, final LoadDataCallback callback) {
        final String myName = name;
//        final ArrayList<User> friendss = new ArrayList<User>();
        //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(DBName);
        //mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String curName = "";
                friends.clear();
                for(DataSnapshot userSnap : dataSnapshot.getChildren()) {
                    curName = (String)userSnap.child("username").getValue();
                    if(curName.equals(myName)) {
                        // load username and profile of current user
                        User curUser = loadCurrentUser(userSnap);
                        // add current user to friends list as the first element
                        friends.add(0, curUser);
                        // load friends' username of current user
                        Iterable<DataSnapshot> myFriends = userSnap.child("friends").getChildren();
                        for(DataSnapshot snapshot : myFriends) {
                            //String friendName = (String)snapshot.getValue();
                            ArrayList<String> myFriend = (ArrayList<String>)snapshot.getValue();
                            User friend = new User(myFriend.get(0));
                            if(myFriend.size() > 1) {
                                byte[] decodedByteArray = Base64.decode(myFriend.get(1), Base64.DEFAULT);
                                friend.setProfileByteArray(decodedByteArray);
                            }
                            friends.add(friend);
                        }
                        break;
                    }
                }

                callback.loadFinish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private User loadCurrentUser(DataSnapshot snapshot) {
        String userName = (String)snapshot.child("username").getValue();
        User user = new User(userName);
        // load current user's profile
        if(snapshot.child("profile").getValue() != null) {
            String encodedProfile = (String) snapshot.child("profile").getValue();
            byte[] decodedByteArray = Base64.decode(encodedProfile, Base64.DEFAULT);
            //Bitmap myProfile = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
            //user.setProfile(myProfile);
            user.setProfileByteArray(decodedByteArray);
        }
        return user;
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

        startActivityForResult(intent, FRIENDS_FRAGMENT_REQ);
    }

    // Add menu to action bar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint("Username");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                intent.putExtra(SearchManager.QUERY, query);
                intent.putExtra("username", username);
                ArrayList<String> friendsName = new ArrayList<>();
                for(User user : friends){
                    friendsName.add(user.getUsername());
                }
                intent.putExtra("friends", friendsName);
                startActivityForResult(intent, FRIENDS_FRAGMENT_REQ);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_add_person:
//                // User chose the "add" item
//                // TODO: search friends
//                searchFriends();
//                return true;
//
//            default:
//                // If we got here, the user's action was not recognized.
//                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
//
//        }
//    }

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
        Log.v("FriendsFragment status", "onActivityResult");
        if (requestCode == FRIENDS_FRAGMENT_REQ && data != null) {
            if (resultCode == SEARCH_FOR_FRIENDS_REQ) {
                // Add a new friend
                User u = (User) data.getSerializableExtra(NEW_FRIEND);
                // TODO: write to  database to modify friends list

//                mdapter.notifyDataSetChanged();
            } else if (resultCode == OPEN_PROFILE_REQ) {
                // Change username or profile
                User u = (User) data.getSerializableExtra(PERSON_OBJECT);
                int position = data.getIntExtra(PERSON_POSITION, 0);
                Log.v("FrendsFragment status", "position: " + position);
                if (position == 0) {
                    // set my profile and user name
                    //User me = friends.get(0);
//                me.setProfile(u.getProfile());
                    Log.v("FrendsFragment status", "username: " + u.getUsername());
                    //me.setUsername(u.getUsername());
                    //friends.set(0, u);
                    mAdapter.notifyDataSetChanged();
                }
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
