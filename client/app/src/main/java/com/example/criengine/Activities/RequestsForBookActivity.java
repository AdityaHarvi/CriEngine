package com.example.criengine.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.criengine.Adapters.RequestsForBookAdapter;
import com.example.criengine.Database.DatabaseWrapper;
import com.example.criengine.Objects.Book;
import com.example.criengine.Objects.Profile;
import com.example.criengine.R;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Requests for Book Activity. Displays all users who have requested your available book & allows
 * for the owner to reject / accept a requester.
 */
public class RequestsForBookActivity extends AppCompatActivity implements Serializable {
    private RequestsForBookAdapter userListAdapter;
    private ListView userListView;
    private TextView header;
    private Book book;
    private ArrayList<Profile> userList;
    private DatabaseWrapper dbw;
    private Context context;

    /**
     * Called upon the creation of the activity. (Initializes the activity)
     * @param savedInstanceState  If the activity is being re-initialized after previously being
     *                            shut down then this Bundle contains the data it most recently
     *                            supplied. Note: Otherwise it is null. This value may be null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_requests_for_book);
        // Grabs the book.
        if (getIntent().getExtras() != null) {
            book = (Book) getIntent().getSerializableExtra("Book");
        } else {
            Intent intent = new Intent(this, SomethingWentWrong.class);
            startActivity(intent);
            return;
        }
        dbw = DatabaseWrapper.getWrapper();

        dbw.getBookBorrowers(book.getBookID()).addOnSuccessListener(new OnSuccessListener<List<Profile>>() {
            @Override
            public void onSuccess(List<Profile> profiles) {
                userList = (ArrayList<Profile>) profiles;

                // Set the adapter.
                userListAdapter = new RequestsForBookAdapter(context, userList, book);
                userListView.setAdapter(userListAdapter);
            }
        });


        userListView = findViewById(R.id.requestsListView);
        header = findViewById(R.id.requests_for_book_header);
        header.setText("Requests for \"" + book.getTitle() + "\"");
    }
}