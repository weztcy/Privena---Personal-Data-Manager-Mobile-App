package com.yodimpra.personaldata.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yodimpra.personaldata.R;

import io.realm.DynamicRealm;
import io.realm.Realm;

public class EditNoteActivity extends AppCompatActivity {
    private EditText TitleEdit,DescriptionEdit;

    private String title, description;
    private Button editButton;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        realm = Realm.getDefaultInstance();
        TitleEdit = findViewById(R.id.titleinputedit);
        DescriptionEdit = findViewById(R.id.descriptioninputedit);
        editButton = findViewById(R.id.editBtn);

        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");

        TitleEdit.setText(title);
        DescriptionEdit.setText(description);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = TitleEdit.getText().toString();
                String description = DescriptionEdit.getText().toString();

                if (TextUtils.isEmpty(title)) {
                    TitleEdit.setError("Please enter Course Name");
                } else if (TextUtils.isEmpty(description)) {
                    DescriptionEdit.setError("Please enter Course Description");
                } else {
                    final Note modal = realm.where(Note.class).findFirst();
                    updateCourse(modal, title, description);
                }

                Toast.makeText(EditNoteActivity.this, "Note Edited.", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(EditNoteActivity.this, Dashboard.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void updateCourse(Note modal, String title, String description) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                modal.setDescription(description);
                modal.setTitle(title);

                realm.copyToRealmOrUpdate(modal);
            }
        });
    }
}