package com.example.publiclibrary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewReservedBook extends AppCompatActivity {

    private ReservedBookAdapter adapter;
    private List<ReservedCardItem> cardItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reserved_book);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewReservedBook);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        cardItemList = new ArrayList<>();
        adapter = new ReservedBookAdapter(this, cardItemList);
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAdapters();
    }

    private void refreshAdapters() {
        cardItemList.clear();
        DbHelper dbHelper = new DbHelper(this);
        Cursor cursor = dbHelper.getAllReservedBooks();

        if (cursor != null && cursor.moveToFirst()) {
            int bookNameIndex = cursor.getColumnIndex(DbHelper.getBookDetails("BOOK_NAME"));
            int bookAuthorIndex = cursor.getColumnIndex(DbHelper.getBookDetails("BOOK_AUTHOR"));
            int bookPublisherIndex = cursor.getColumnIndex(DbHelper.getBookDetails("BOOK_PUBLISHER"));
            int reservedByIndex = cursor.getColumnIndex(DbHelper.getReservedBookDetails("RESERVE_MEMBER_NAME")); // Uncommented this line

            do {
                String bookName = cursor.getString(bookNameIndex);
                String bookAuthor = cursor.getString(bookAuthorIndex);
                String bookPublisher = cursor.getString(bookPublisherIndex);
                String reservedBy = cursor.getString(reservedByIndex); // Uncommented this line

                cardItemList.add(new ReservedCardItem(bookName, bookAuthor, bookPublisher, reservedBy)); // Modified this line
            } while (cursor.moveToNext());
            cursor.close();
        }
        dbHelper.close();
        adapter.notifyDataSetChanged();
    }

    private static class ReservedCardItem {
        String name;
        String author;
        String publisher;
        String reservedBy; // Uncommented this line

        ReservedCardItem(String name, String author, String publisher, String reservedBy) { // Modified this line
            this.name = name;
            this.author = author;
            this.publisher = publisher;
            this.reservedBy = reservedBy;
        }
    }

    private static class ReservedBookAdapter extends RecyclerView.Adapter<ReservedBookAdapter.ViewHolder> {
        private final List<ReservedCardItem> cardItems;
        private final Context context;

        ReservedBookAdapter(Context context, List<ReservedCardItem> cardItems) {
            this.context = context;
            this.cardItems = cardItems;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reserved_book_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ReservedCardItem cardItem = cardItems.get(position);
            holder.ViewReservedBookName.setText(cardItem.name);
            holder.ViewReservedBookAuthor.setText(cardItem.author);
            holder.ViewReservedBookPublisher.setText(cardItem.publisher);
            holder.ViewReservedBy.setText(cardItem.reservedBy);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open BookDetailsActivity and pass book details
                    Intent intent = new Intent(context, BookDetailsActivity.class);
                    intent.putExtra("bookName", cardItem.name);
                    intent.putExtra("bookAuthor", cardItem.author);
                    intent.putExtra("bookPublisher", cardItem.publisher);
                    intent.putExtra("member", cardItem.reservedBy);
                    context.startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return cardItems.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView ViewReservedBookName;
            final TextView ViewReservedBookAuthor;
            final TextView ViewReservedBookPublisher;
            final TextView ViewReservedBy; // Uncommented this line

            ViewHolder(View itemView) {
                super(itemView);
                ViewReservedBookName = itemView.findViewById(R.id.txtReservedBookName);
                ViewReservedBookAuthor = itemView.findViewById(R.id.txtReservedBookAuthor);
                ViewReservedBookPublisher = itemView.findViewById(R.id.txtReservedBookPublisher);
                ViewReservedBy = itemView.findViewById(R.id.txtReservedBy); // Uncommented this line
            }
        }
    }
}
