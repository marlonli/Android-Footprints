package com.example.jingyuan.footprints;


import android.content.Intent;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class JournalEditorActivity extends AppCompatActivity {

    private static final String JOURNAL_OBJECT = "journalObj";
    private static final int JOURNAL_EDITOR_REQ = 1;
    private Journal journal;
    private EditText et_title;
    private EditText et_content;
    private EditText et_date;
    private ImageButton ib_save;
    private ImageButton ib_location;
    private ImageButton ib_tags;
    private ImageButton ib_photos;
    private ImageButton ib_camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_editor);

        // Initialization
        initialization();

        // Get intent
        Intent intent = getIntent();
        journal = (Journal) intent.getSerializableExtra(JOURNAL_OBJECT);
        // TODO: edit journal if j != null
        Log.v("Journal Editor", "journal: " + journal);
        if (journal != null) {
            et_title.setText(journal.getTitle());
            et_content.setText(journal.getContent());
        }

        // Set date
        // Format: Sat Dec 02 19:19:45 EST 2017
        String[] date = Calendar.getInstance().getTime().toString().split(" ");
        et_date = (EditText) findViewById(R.id.editText_date);
        et_date.setText(date[0] + ", " + date[1] + " " + date[2] + ", " + date[5]);
    }

    private void initialization() {
        et_title = (EditText) findViewById(R.id.editText_title);
        et_content = (EditText) findViewById(R.id.editText_content);
        ib_save = (ImageButton) findViewById(R.id.imageButton_save);
        ib_location = (ImageButton) findViewById(R.id.imageButton_location);
        ib_tags = (ImageButton) findViewById(R.id.imageButton_tags);
        ib_photos = (ImageButton) findViewById(R.id.imageButton_photos);
        ib_camera = (ImageButton) findViewById(R.id.imageButton_camera);

        // Set save button and image button
        // TODO: set image button add photo
        ib_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: set return value (save to database)
                Intent intent = new Intent(JournalEditorActivity.this, MainActivity.class);

                intent.putExtra(JOURNAL_OBJECT, journal);
//                intent.putExtra("addPerson", add);
//                intent.putExtra("size", relation.size());

                setResult(JOURNAL_EDITOR_REQ, intent);
                finish();
            }
        });
    }
}
