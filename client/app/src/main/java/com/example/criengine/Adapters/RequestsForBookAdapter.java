package com.example.criengine.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.criengine.Activities.RootActivity;
import com.example.criengine.Activities.SelectGeopage;
import com.example.criengine.Database.DatabaseWrapper;
import com.example.criengine.Objects.Book;
import com.example.criengine.Objects.Notification;
import com.example.criengine.R;
import java.util.ArrayList;

/*
 * RequestsForBookAdapter is custom ArrayAdapter that can be used to show User profiles that have
 * requested a book in a ListView.
 * Outstanding Issues:
 * - Does not retrieve nor push changes to the database.
 */
public class RequestsForBookAdapter extends ArrayAdapter<String> {
    private ArrayList<String> userRequests;
    private Book book;
    private Context context;
    private DatabaseWrapper dbw;

    /**
     * Constructor.
     * @param context The context of the activity.
     * @param userRequests The list of users requesting a book.
     */
    public RequestsForBookAdapter(@NonNull Context context, @NonNull ArrayList<String> userRequests, Book book) {
        super(context, 0, userRequests);
        this.context = context;
        this.userRequests = userRequests;
        this.book = book;
        this.dbw = DatabaseWrapper.getWrapper();
    }

    /**
     * Returns a view with the properly formatted information.
     * @param position The position from the list.
     * @param convertView The old view to reuse (if possible).
     * @param parent The parent view group.
     * @return The view that displays the formatted data at the specified position in the data set.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Set the view.
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.user_accept_or_reject, parent, false);
        }

        // Assign the proper view for each button / text.
        TextView username = view.findViewById(R.id.user_name);
        Button acceptUser = view.findViewById(R.id.user_accept);
        Button rejectUser = view.findViewById(R.id.user_reject);

        // Get the uid of the user.
        final String uid = userRequests.get(position);

        // Set the text for names / buttons.
        username.setText(uid);
        acceptUser.setText("✔");
        rejectUser.setText("✖");

        acceptUser.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentGeopage = new Intent(v.getContext(), SelectGeopage.class);
                    intentGeopage.putExtra("acceptedUser", uid);
                    intentGeopage.putExtra("users", userRequests);
                    intentGeopage.putExtra("book", book);
                    v.getContext().startActivity(intentGeopage);
                }
            }
        );

        rejectUser.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbw.declineRequest(uid, book.getBookID());

                    userRequests.remove(uid);
                    if (userRequests.size() == 0) {
                        book.setStatus("available");
                        dbw.addBook(book);

                        // Return to previous activity automatically.
                        Intent intent = new Intent(context, RootActivity.class);
                        intent.putExtra("Index", RootActivity.PAGE.MY_BOOKS);
                        context.startActivity(intent);
                    }
                    notifyDataSetChanged();
                }
            }
        );

        return view;
    }
}
