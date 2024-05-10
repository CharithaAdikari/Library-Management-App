package com.example.publiclibrary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewMember extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MemberAdapter adapter;
    private List<CardItem> cardItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_member);

        recyclerView = findViewById(R.id.recyclerViewviewmember);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        cardItemList = new ArrayList<>();
        adapter = new MemberAdapter(this, cardItemList);
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

    private void refreshAdapters() {
        cardItemList.clear();
        DbHelper dbHelper = new DbHelper(this);
        Cursor cursor = dbHelper.getAllMembers();

        if (cursor != null && cursor.moveToFirst()) {
            int memberNameIndex = cursor.getColumnIndex(DbHelper.getMemberDetails("MEMBER_NAME"));
            int memberEmailIndex = cursor.getColumnIndex(DbHelper.getMemberDetails("MEMBER_EMAIL"));
            int memberPhoneIndex = cursor.getColumnIndex(DbHelper.getMemberDetails("MEMBER_PHONE"));
            int memberAddressIndex = cursor.getColumnIndex(DbHelper.getMemberDetails("MEMBER_ADDRESS"));

            do {
                String memberName = cursor.getString(memberNameIndex);
                String memberEmail = cursor.getString(memberEmailIndex);
                String memberPhone = cursor.getString(memberPhoneIndex);
                String memberAddress = cursor.getString(memberAddressIndex);

                cardItemList.add(new CardItem(memberName, memberEmail, memberPhone, memberAddress));
            } while (cursor.moveToNext());
            cursor.close();

        } else {
            Toast.makeText(this, "No members found", Toast.LENGTH_SHORT).show();
        }
        dbHelper.close();
        adapter.notifyDataSetChanged();

    }

    private static class CardItem {
        String name;
        String email;
        String phone;
        String address;

        CardItem(String name, String email, String phone, String address) {
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.address = address;
        }
    }

    private static class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
        private final List<CardItem> cardItems;
        private final Context context;

        MemberAdapter(Context context, List<CardItem> cardItems) {
            this.context = context;
            this.cardItems = cardItems;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allmember, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CardItem cardItem = cardItems.get(position);
            holder.txtMemberName.setText(cardItem.name);
            holder.txtMemberEmail.setText(cardItem.email);
            holder.txtMemberPhone.setText(cardItem.phone);
            holder.txtMemberAddress.setText(cardItem.address);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditMember.class);
                intent.putExtra("memberName", cardItem.name);
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return cardItems.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView txtMemberName;
            final TextView txtMemberEmail;
            final TextView txtMemberPhone;
            final TextView txtMemberAddress;

            ViewHolder(View itemView) {
                super(itemView);
                txtMemberName = itemView.findViewById(R.id.txt_member_name);
                txtMemberEmail = itemView.findViewById(R.id.txt_member_email);
                txtMemberPhone = itemView.findViewById(R.id.txt_member_phone);
                txtMemberAddress = itemView.findViewById(R.id.txt_member_address);
            }
        }
    }
}
