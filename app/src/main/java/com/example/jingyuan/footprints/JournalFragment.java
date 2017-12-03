package com.example.jingyuan.footprints;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuLayout;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static com.example.jingyuan.footprints.MainActivity.dipToPixels;

/**
 * Created by jingyuan on 11/30/17.
 */
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
    private static final String ARG_PARAM2 = "param2";
    private static final String JOURNAL_OBJECT = "journalObj";
    private static final int JOURNAL_EDITOR_REQ = 1;
    private static final int NEW_JOURNAL = -1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SwipeMenuListView lv;
    public List<Journal> journals = new ArrayList<>();
    MyJournalViewAdapter madapter;
    FloatingActionButton fab_add;

    private OnFragmentInteractionListener mListener;

    // Create SwipeMenuCreator
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
            deleteItem.setWidth(Math.round(dipToPixels(getContext(), 90)));
            // set a icon
            deleteItem.setIcon(R.drawable.ic_delete_white_36px);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };

    public JournalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JournalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JournalFragment newInstance(String param1, String param2) {
        JournalFragment fragment = new JournalFragment();
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
        Log.v("JournalFragment", "onCreateView");

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_journal, container, false);

        // Initialization
        lv = v.findViewById(R.id.listview_journals);
        journals = new ArrayList<>();
        // test data
        Date currentTime = Calendar.getInstance().getTime();
        ArrayList<String> testTags = new ArrayList<>();
        testTags.add("tag1");
        testTags.add("tag2");
        testTags.add("tag3");
        journals.add(new Journal("Journal1", testTags, currentTime, "12 Davidson Rd., Piscataway, NJ", "In order to reuse the Fragment UI components, you should build each as a completely self-contained, modular component that defines its own layout and behavior. Once you have defined these reusable Fragments, you can associate them with an Activity and connect them with the application logic to realize the overall composite UI."));
        journals.add(new Journal("Journal title", testTags, currentTime, "21 Anthony Rd., Edison, NJ", "Often you will want one Fragment to communicate with another, for example to change the content based on a user event. All Fragment-to-Fragment communication is done through the associated Activity. Two Fragments should never communicate directly."));
        journals.add(new Journal("OMG OMG", testTags, currentTime, "1050 George St., New Brunswick, NJ", "Often you will want one Fragment to communicate with another, for example to change the content based on a user event. All Fragment-to-Fragment communication is done through the associated Activity. Two Fragments should never communicate directly."));

        madapter = new MyJournalViewAdapter(getActivity(), journals);
        lv.setAdapter(madapter);
        fab_add = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton_add);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // set creator
        lv.setMenuCreator(creator);
        lv.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        // Set floating button
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditor(NEW_JOURNAL);
            }
        });

        // Set list item onClick event
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openEditor(i);
            }
        });

        lv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // TODO: set delete action: delete journals.get(i)
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("fragment contact status", "onActivityResult");
        if (requestCode == Activity.RESULT_FIRST_USER && data != null) {

            if (resultCode == JOURNAL_EDITOR_REQ) {
                Journal j = (Journal) data.getSerializableExtra(JOURNAL_OBJECT);

                // TODO: read database to modify journal list

                madapter.notifyDataSetChanged();
            }
        }
    }

    public void openEditor(int journalIndex) {
        Intent intent = new Intent(getActivity(), JournalEditorActivity.class);
        if (journalIndex != -1)
            intent.putExtra(JOURNAL_OBJECT, journals.get(journalIndex));
        startActivityForResult(intent, JOURNAL_EDITOR_REQ);
    }
}
