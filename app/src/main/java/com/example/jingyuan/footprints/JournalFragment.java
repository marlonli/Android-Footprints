package com.example.jingyuan.footprints;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuLayout;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wyh.slideAdapter.ItemBind;
import com.wyh.slideAdapter.ItemView;
import com.wyh.slideAdapter.SlideAdapter;
import com.wyh.slideAdapter.SlideLayout;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JournalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JournalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JournalFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String JOURNAL_OBJECT = "journalObj";
    private static final int JOURNAL_EDITOR_REQ = 1;
    private static final int NEW_JOURNAL = -1;
    private static final String EDITOR_MODE = "mode";
    private static final int EDIT = 10;

    public String username;
    public String journal_list;
    //    private SwipeMenuListView lv;
    public List<Journal> journals = new ArrayList<>();
    //    MyJournalViewAdapter madapter;
    MyJournalRecyclerViewAdapter mAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton fab;

    private OnFragmentInteractionListener mListener;

    public ItemBind itemBind;

    /*// Create SwipeMenuCreator
    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
//            // create "open" item
//            SwipeMenuItem openItem = new SwipeMenuItem(
//                    getActivity().getApplicationContext());
//            // set item background
//            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
//                    0xCE)));
//            // set item width
//            openItem.setWidth(Math.round(dipToPixels(getContext(), 90)));
//            // set item title
//            openItem.setTitle("Open");
//            // set item title fontsize
//            openItem.setTitleSize(18);
//            // set item title font color
//            openItem.setTitleColor(Color.WHITE);
//            // add to menu
//            menu.addMenuItem(openItem);

            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getActivity().getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            // set item width
            deleteItem.setWidth(Math.round(Utilities.dipToPixels(getContext(), 90)));
            // set a icon
            deleteItem.setIcon(R.drawable.ic_delete_white_36px);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };*/

    public JournalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 username.
     * @return A new instance of fragment JournalFragment.
     */
    public static JournalFragment newInstance(String param1) {
        JournalFragment fragment = new JournalFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("JournalFragment", "onCreateView");

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_journal, container, false);

        // Set action bar menu
//        setHasOptionsMenu(true);

        fab = getActivity().findViewById(R.id.fab_newjournal);

        // Initialization
        journals = new ArrayList<>();
        read_data_from_database(new LoadDataCallback() {
            @Override
            public void loadFinish() {
                SlideAdapter.load(journals)
                        .item(R.layout.journal_list, 0,0,R.layout.swipe_menu,0.25f)
                        .bind(itemBind)
                        .into(mRecyclerView);
                Log.v("JournalFragment status", "loadFinish!!!!!!!!!");
                sortList();
            }
        });
//        addTestData();
//        Collections.sort(journals, new JournalsComparator());

//        lv = v.findViewById(R.id.listview_journals);
//        madapter = new MyJournalViewAdapter(getActivity(), journals);
//        lv.setAdapter(madapter);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView_journals);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                LinearLayout.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }
        });
        // notifyItemInserted(position)  notifyItemRemoved(position)

        // specify an adapter
//        mAdapter = new MyJournalRecyclerViewAdapter(journals);
//        mRecyclerView.setAdapter(mAdapter);

        itemBind = new ItemBind<Journal>() {
            @Override
            public void onBind(ItemView itemView, Journal j, final int position) {
                itemView.setText(R.id.list_title, j.getTitle())
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //onClick item
                                openEditor(position);
                            }
                        });
                itemView.setText(R.id.list_content, j.getContent());
                String loc = Utilities.latLngToLoc(getActivity(), j.getLat(), j.getLng());
//                String loc = latLngToLoc(j.getLat(), j.getLng());
                itemView.setText(R.id.list_location, loc);
                ArrayList<String> tags_tmp =  journals.get(position).getTags();
                if (tags_tmp!=null) {
                    String tags = tags_tmp.toString();
                    itemView.setText(R.id.list_tag, tags.substring(1, tags.length() - 1));
                }
                itemView.setText(R.id.list_mon, j.getDateTimeString().toString().split(" ")[1]);
                itemView.setText(R.id.list_date, j.getDateTimeString().toString().split(" ")[2]);
                itemView.setOnClickListener(R.id.rightMenu_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // onClick delete
//                        Toast.makeText(getActivity(), "Delete item " + position, Toast.LENGTH_SHORT).show();

                        // Delete the corresponding journal from the database.
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference Users = database.getReference("New_users");
                        DatabaseReference journal_listener = Users.child(username).child("journal_list");
                        journal_listener.addValueEventListener(new ValueEventListener() {
                            String real_title = journals.get(position).getTitle();
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // delete from database
                                for (DataSnapshot snap:dataSnapshot.getChildren()){
                                    String key_title = snap.getKey();
                                    if (key_title.equals(real_title)){
                                        Users.child(username).child("journal_list").child(key_title).removeValue();
                                        // delete from local list
                                        journals.remove(position);
                                        break;
                                    }
                                }



                                // update list
//                                updateList();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
//                        .setOnClickListener(R.id.textView, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                //onClick item text
//                            }
//                        });
            }
        };

//        updateList();


//        SwipeController swipeController = new SwipeController();
//        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
//        itemTouchhelper.attachToRecyclerView(mRecyclerView);

        return v;
    }

    private void updateList() {
        mRecyclerView.getAdapter().notifyDataSetChanged();

    }

    private void read_data_from_database(final LoadDataCallback callback){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference Users = database.getReference("New_users");
        DatabaseReference aaa = Users.child(username);
        DatabaseReference bbb = aaa.child("journal_list");
        bbb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                journals.clear();
                for (DataSnapshot snap:dataSnapshot.getChildren()){
                    String key = snap.getKey();
                    String title = (String) snap.child("title").getValue();
                    String content = (String) snap.child("content").getValue();
                    long dateTimeLong = (long) snap.child("dateTimeLong").getValue();
                    String dateTimeString = (String) snap.child("dateTimeString").getValue();
                    String lat = (String) snap.child("lat").getValue();
                    String lng = (String) snap.child("lng").getValue();
                    ArrayList<String> tags = (ArrayList<String>) snap.child("tags").getValue();
                    Journal journal = new Journal(title, tags,dateTimeLong,lat,lng, content);
                    ArrayList<String> photo_string = (ArrayList<String>) snap.child("photoString").getValue();
                    if (photo_string!=null) {
                        journal.setPhoto_string(photo_string);
                        ArrayList<Bitmap> photo_bit = photo_bit_to_string(photo_string);
                        journal.setPhotos(photo_bit);
                    }

                    journals.add(journal);
                }
//                updateList();
                callback.loadFinish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void sortList() {
        Collections.sort(journals, new JournalsComparator());
    }

    private ArrayList<Bitmap> photo_bit_to_string(ArrayList<String> photo_string){
        ArrayList<Bitmap> photo_bit = new ArrayList<Bitmap>();
        for (int i=0;i<photo_string.size();i++){
            String photo_string_tmp = photo_string.get(i);
            byte[] decodeByte = Base64.decode(photo_string_tmp,0);
            Bitmap photo_bit_tmp = BitmapFactory.decodeByteArray(decodeByte,0,decodeByte.length);
            photo_bit.add(photo_bit_tmp);
        }
        return photo_bit;
    }

    private void addTestData() {
        long startDate1 = 0;
        long startDate2 = 0;
        try {
            String dateString1 = "11/09/2017 14:09:03";
            String dateString2 = "11/16/2017 14:09:03";
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date date = sdf.parse(dateString1);
            Date date2 = sdf.parse(dateString2);

            startDate1 = date.getTime();
            startDate2 = date2.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        // test data
        long currentTime = System.currentTimeMillis();
        ArrayList<String> testTags = new ArrayList<>();
        testTags.add("tag1");
        testTags.add("tag2");
        testTags.add("tag3");
        journals.add(new Journal("Journal1", testTags, currentTime, "30", "120", getString(R.string.large_text)));
        journals.add(new Journal("Journal title", testTags, currentTime - 86400000, "30", "-120", "Often you will want one Fragment to communicate with another, for example to change the content based on a user event. All Fragment-to-Fragment communication is done through the associated Activity. Two Fragments should never communicate directly."));
        journals.add(new Journal("OMG OMG", testTags, startDate1, "40", "-74", "Often you will want one Fragment to communicate with another, for example to change the content based on a user event. All Fragment-to-Fragment communication is done through the associated Activity. Two Fragments should never communicate directly."));
        journals.add(new Journal("Journal1", testTags, startDate2, "40", "-110", "In order to reuse the Fragment UI components, you should build each as a completely self-contained, modular component that defines its own layout and behavior. Once you have defined these reusable Fragments, you can associate them with an Activity and connect them with the application logic to realize the overall composite UI."));
        journals.add(new Journal("Journal1", testTags, currentTime, "30", "-70", "In order to reuse the Fragment UI components, you should build each as a completely self-contained, modular component that defines its own layout and behavior. Once you have defined these reusable Fragments, you can associate them with an Activity and connect them with the application logic to realize the overall composite UI."));


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditor(NEW_JOURNAL);
            }
        });


//        mAdapter.setOnItemClickListener(new MyJournalRecyclerViewAdapter.OnItemClickListener(){
//            @Override
//            public void onItemClick(View view , int position){
//                openEditor(position);
//            }
//        });



        // set creator
//        lv.setMenuCreator(creator);
//        lv.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
//
//        // Set list item onClick event
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                openEditor(i);
//            }
//        });
//
//        lv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//                switch (index) {
//                    case 0:
//                        // TODO: set delete action: delete journals.get(i)
//                        break;
//                }
//                // false : close the menu; true : not close the menu
//                return false;
//            }
//        });

    }

    // Add menu to action bar

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_scrolling, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_add:
//                // User chose the "add" item
//                openEditor(NEW_JOURNAL);
//                return true;
//
//            default:
//                // If we got here, the user's action was not recognized.
//                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
//
//        }
//    }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("fragment status", "onActivityResult");
        if (requestCode == Activity.RESULT_FIRST_USER && data != null) {
            if (resultCode == JOURNAL_EDITOR_REQ) {
                Journal j = (Journal) data.getSerializableExtra(JOURNAL_OBJECT);

                // TODO: write to  database to modify journal list
                // notifyItemInserted(position)  notifyItemRemoved(position)
//                mdapter.notifyDataSetChanged();
            }
        }
    }

    public void openEditor(int journalIndex) {
        Intent intent = new Intent(getActivity(), JournalEditorActivity.class);
        intent.putExtra(EDITOR_MODE, EDIT);
        if (journalIndex != NEW_JOURNAL) {
//            intent.putExtra(JOURNAL_OBJECT, journals.get(journalIndex));
            intent.putExtra("journal_name",journals.get(journalIndex).getTitle());
            intent.putExtra("username", username);
        }
        else{
            intent.putExtra("username", username);
        }
        startActivityForResult(intent, JOURNAL_EDITOR_REQ);
    }

    public String latLngToLoc(String lat, String lng) {
        String loc = null;
        if (lat == null || lng == null)
            return loc;

        double myLat = Double.valueOf(lat);
        double myLng = Double.valueOf(lng);
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> addresses = new ArrayList<>();
        // Get and show address
        try {

            addresses.addAll(geocoder.getFromLocation(myLat, myLng, 1));

        } catch (IOException e) {
            e.printStackTrace();
        }


        if (addresses!= null && addresses.size() != 0) {
            Address address = addresses.get(0);
            StringBuilder sb = new StringBuilder();
            if (address.getSubThoroughfare() != null) sb.append(address.getSubThoroughfare()).append(", ");
            if (address.getThoroughfare() != null) sb.append(address.getThoroughfare()).append(", ");
            if (address.getLocality() != null) sb.append(address.getLocality()).append(", ");
            if (address.getAdminArea() != null) sb.append(address.getAdminArea()).append(" ");
            if (address.getPostalCode() != null) sb.append(address.getPostalCode());
            loc = sb.toString();
        }
        return loc;
    }
}
