package com.example.publiclibrary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewBook extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<CardItem> cardItemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_book);

        recyclerView = findViewById(R.id.recyclerViewviewbook);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        cardItemList =  new ArrayList<>();
        adapter = new BookAdapter(this, cardItemList);
        recyclerView.setAdapter(adapter);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        refreshAdapters();
    }

    private void refreshAdapters(){
        cardItemList.clear();
        DbHelper dbHelper = new DbHelper(this);
        Cursor cursor = dbHelper.getAllBook();

        if(cursor != null && cursor.moveToFirst()){
            int bookNameIndex = cursor.getColumnIndex(DbHelper.getBookDetails("BOOK_NAME"));
            int bookAuthorIndex = cursor.getColumnIndex(DbHelper.getBookDetails("BOOK_AUTHOR"));
            int bookPublisherIndex = cursor.getColumnIndex(DbHelper.getBookDetails("BOOK_PUBLISHER"));
            int bookQuantityIndex = cursor.getColumnIndex(DbHelper.getBookDetails("BOOK_QUANTITY"));
            int bookAvailableIndex = cursor.getColumnIndex(DbHelper.getBookDetails("BOOK_AVAILABLE"));



            do{
                String bookName = cursor.getString(bookNameIndex);
                String bookAuthor = cursor.getString(bookAuthorIndex);
                String bookPublisher = cursor.getString(bookPublisherIndex);
                String bookQuantity = cursor.getString(bookQuantityIndex);
                boolean bookAvailable = cursor.getInt(bookAvailableIndex) == 1;


                cardItemList.add(new CardItem(bookName, bookAuthor, bookPublisher, bookQuantity, bookAvailable));
            }while(cursor.moveToNext());
            cursor.close();

        }
        dbHelper.close();
        adapter.notifyDataSetChanged();

    }

    private static class CardItem {
        String name;
        String author;
        String publisher;
        String quantity;
        boolean available;

        CardItem(String name, String author, String publisher, String quantity, boolean available) {
            this.name = name;
            this.author = author;
            this.publisher = publisher;
            this.quantity = quantity;
            this.available = available;
        }
    }

    private static class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
        private List<CardItem> cardItems;
        private final Context context;




        BookAdapter(Context context, List<CardItem> cardItems){
            this.context = context;
            this.cardItems = cardItems;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allbook, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CardItem cardItem = cardItems.get(position);
            holder.ViewBOOkName.setText(cardItem.name);
            holder.ViewBOOkAuthor.setText(cardItem.author);
            holder.ViewBookPublisher.setText(cardItem.publisher);
            holder.ViewBOOkQuantity.setText(cardItem.quantity);
            // Set availability
            holder.ViewBOOkAvailable.setText(cardItem.available ? "Available" : "Not Available");

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditBook.class);
                intent.putExtra("bookName", cardItem.name);
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount(){
            return cardItems.size();
        }
        static class ViewHolder extends RecyclerView.ViewHolder{
            final TextView ViewBOOkName;
            final TextView ViewBOOkAuthor;
            final TextView ViewBookPublisher;
            final TextView ViewBOOkQuantity;
            final TextView ViewBOOkAvailable;

            ViewHolder(View itemView){
                super(itemView);
                ViewBOOkName = itemView.findViewById(R.id.txtallbookcardtitle);
                ViewBOOkAuthor = itemView.findViewById(R.id.txtallbookcardauthor);
                ViewBookPublisher = itemView.findViewById(R.id.txtallbookcardpublisher);
                ViewBOOkQuantity = itemView.findViewById(R.id.txtallbookcardquantity);
                ViewBOOkAvailable = itemView.findViewById(R.id.txtcardavailable);
            }
        }
    }
}