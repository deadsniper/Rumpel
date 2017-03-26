package com.example.harelavikasis.rumpel.QuestionsPicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.harelavikasis.rumpel.Managers.UserManger;
import com.example.harelavikasis.rumpel.Models.Chat;
import com.example.harelavikasis.rumpel.Models.Question;
import com.example.harelavikasis.rumpel.R;
import com.google.android.gms.games.internal.constants.QuestState;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sgiosviews.SGPickerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class QuestionsPickerView extends AppCompatActivity {


    @Bind(R.id.pickerView)
    SGPickerView pickerView;

    private DatabaseReference mDatabase;
    private DatabaseReference globalDatabase;
    private Chat currentChat;

    private List<Question> questions = new ArrayList<Question>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_picker_view);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String chatId = intent.getStringExtra("chatId");

        mDatabase = FirebaseDatabase.getInstance().getReference("chats").child(chatId);
        globalDatabase = FirebaseDatabase.getInstance().getReference("questions");

        fetchQuestions();

//        setPickerView();
    }

    private void fetchQuestions() {

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentChat =  dataSnapshot.getValue(Chat.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        globalDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot questionSnapshot: dataSnapshot.getChildren()) {
                    Question q = questionSnapshot.getValue(Question.class);
                    questions.add(q);
                }

                setPickerView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void setPickerView() {

        ArrayList<String> items = new ArrayList<String>();
        for (Question q : questions)
        {
            items.add(q.getQuestionText());
        }

        pickerView.setItems(items);

        pickerView.setPickerListener(new SGPickerView.SGPickerViewListener() {
            @Override
            public void itemSelected(String item, int index) {

            }
        });
    }

    public void sendRiddleTapped(View v) {
        int index = pickerView.getCurrentSelectedItemIndex();
        Question selectedQuestions = questions.get(index);
        selectedQuestions.setSenderId(UserManger.getInstance().getUserId());
        selectedQuestions.setQuestionOpen(true);
        currentChat.addQuestion(selectedQuestions);
        mDatabase.child("questions").setValue(currentChat.getQuestions());
        mDatabase.child("thereOpenQuestion").setValue(true);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}