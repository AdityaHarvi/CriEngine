package com.example.criengine.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.criengine.Adapters.SearchBooksListAdapter;
import com.example.criengine.Database.DatabaseWrapper;
import com.example.criengine.Objects.Book;
import com.example.criengine.Objects.Profile;
import com.example.criengine.R;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

/**
 * User profile activity.
 */
public class UserProfileActivity extends ProfileActivity {

    private DatabaseWrapper dbw;

    private String userId;
    private Profile userProfile;
    private TextView userBooksTextView;
    private ListView userBooksListView;
    private ArrayList<Book> userBooks;
    private SearchBooksListAdapter searchBookAdapter;

    /**
     * The layout used by UserProfileActivity
     * @return The layout used by UserProfileActivity
     */
    public int getFragmentLayout() {
        return R.layout.activity_user_profile;
    }

    /**
     * Called upon the creation of the activity. (Initializes the activity)
     * @param savedInstanceState  If the activity is being re-initialized after previously being
     *                            shut down then this Bundle contains the data it most recently
     *                            supplied. Note: Otherwise it is null. This value may be null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            userId = getIntent().getStringExtra("userId");
        } else {
            Intent intent = new Intent(this, SomethingWentWrong.class);
            startActivity(intent);
            return;
        }

        userBooksTextView = findViewById(R.id.user_books_text);
        userBooksListView = findViewById(R.id.user_books_listview);

        // Setup data and adapter
        userBooks = new ArrayList<>();
        searchBookAdapter = new SearchBooksListAdapter(this, userBooks, false);
        userBooksListView.setAdapter(searchBookAdapter);

        dbw = DatabaseWrapper.getWrapper();
        dbw.getProfile(userId).addOnSuccessListener(new OnSuccessListener<Profile>() {
            @Override
            public void onSuccess(Profile profile) {
                userProfile = profile;
                userTextView.setText(getString(R.string.user_profile_text, profile.getUsername()));
                bioEditText.setText(profile.getBio());
                phoneEditText.setText(profile.getPhone());
                addressEditText.setText(profile.getAddress());
                userBooksTextView.setText(getString(R.string.user_books_text, profile.getUsername()));

                dbw.getOwnedBooks(profile).addOnSuccessListener(new OnSuccessListener<List<Book>>() {
                    @Override
                    public void onSuccess(List<Book> books) {
                        userBooks.addAll(books);
                        searchBookAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
