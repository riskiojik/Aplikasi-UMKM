package com.example.aplikasiumkm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.aplikasiumkm.adapter.AdapterChat;
import com.example.aplikasiumkm.models.ModelChat;
import com.example.aplikasiumkm.models.ModelToko;
import com.example.aplikasiumkm.penjual.MainPenjualActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView chatRv;
    private TextView NameTv, StatusUserTv;
    private EditText pesanEt;
    private FloatingActionButton fltBtn_chat;
    private String tokoUid;
    private ImageView profileIv;
//    private String hisUid;
//    private String myUid;
    String profileImage;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser; 
    ValueEventListener seenListener;
    DatabaseReference userRefRorSeen;

    ArrayList<ModelChat> chatArrayList;
    AdapterChat adapterChat;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        chatRv = findViewById(R.id.chatRv);
        NameTv = findViewById(R.id.NameTv);
        StatusUserTv = findViewById(R.id.StatusUserTv);
        pesanEt = findViewById(R.id.pesanEt);
        fltBtn_chat = findViewById(R.id.fltBtn_chat);
        profileIv = findViewById(R.id.profileIv);

        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        chatRv.setHasFixedSize(true);
        chatRv.setLayoutManager(linearLayoutManager);

        tokoUid = getIntent().getStringExtra("tokoUid");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        loadInfoUser();


//        fltBtn_chat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String msg = pesanEt.getText().toString().trim();
//                if (!msg.equals("")) {
//                    sendMessage(firebaseUser.getUid(), tokoUid, msg);
//                } else {
//                    Toast.makeText(ChatActivity.this, "Gagal mengirim message kosong!", Toast.LENGTH_SHORT).show();
//                }
//                pesanEt.setText("");
//            }
////              inputData();
//        });

        fltBtn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = pesanEt.getText().toString().trim();


                if (TextUtils.isEmpty(message)){
                    Toast.makeText(ChatActivity.this, "Tidak bisa mengirim pesan kosong!!", Toast.LENGTH_SHORT).show();
                }else {
                    sendMessage(firebaseUser.getUid(),tokoUid,message );
                }
                pesanEt.setText("");
            }

        });

        readMessage(firebaseUser.getUid(), tokoUid, profileImage);
        seenMessage();


    }
    private void seenMessage() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                    ModelChat modelChat = dataSnapshot.getValue(ModelChat.class);
                    if (modelChat.getReceiver().equals(firebaseAuth.getUid()) && modelChat.getSender().equals(tokoUid)){
                        HashMap<String, Object> hashSeenMap = new HashMap<>();
                        hashSeenMap.put("isSeen", true);
                        dataSnapshot.getRef().updateChildren(hashSeenMap);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage(String firebaseUser, String tokoUid, String profileImage) {
        chatArrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatArrayList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ModelChat modelChat = dataSnapshot.getValue(ModelChat.class);
                    if (modelChat.getReceiver().equals(firebaseUser) && modelChat.getSender().equals(tokoUid)
                            ||  modelChat.getReceiver().equals(tokoUid) && modelChat.getSender().equals(firebaseUser)){
                        chatArrayList.add(modelChat);


                    }
                    adapterChat = new AdapterChat(ChatActivity.this, chatArrayList, profileImage);
                    adapterChat.notifyDataSetChanged();

                    chatRv.setAdapter(adapterChat);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String firebaseUser ,String tokoUid, String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        String timestamp = ""+System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", "" + firebaseUser);
        hashMap.put("receiver","" + tokoUid);
        hashMap.put("message","" + message);
        hashMap.put("timestamp", "" +timestamp);
        hashMap.put("isSeen", false);
        databaseReference.child("Chats").push().setValue(hashMap);

        pesanEt.setText("");

    }

//    private void sendMessage(String send, String receive, String message) {
//        //Time
//        String timestamp = ""+ System.currentTimeMillis();
//        //SetUpData
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("send", "" + send);
//        hashMap.put("message", "" + message);
//        hashMap.put("receive", ""+ receive);
//        hashMap.put("timestamp", "" + timestamp);
//
//        //SaveDatabase
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
//        databaseReference.child("Chats").setValue(hashMap)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(ChatActivity.this, "Berhasi mengirim pesan", Toast.LENGTH_SHORT).show();
//
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(ChatActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }


    private void loadInfoUser() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("accountType").equalTo("Pengguna").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String NamaLengkap = "" + dataSnapshot.child("NamaLengkap").getValue();
                    String online = "" + dataSnapshot.child("online").getValue();
                    String profileImage = "" + dataSnapshot.child("profileImage").getValue();

                    NameTv.setText(NamaLengkap);
                    if (online.equals("true")){
                        StatusUserTv.setText("Online");
                    }else {
                        StatusUserTv.setText("Offline");
                    }

                    try {
                        Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_person).into(profileIv);

                    }catch (Exception e){
                        profileIv.setImageResource(R.drawable.ic_baseline_person);

                    }



                }

//                String NamaLengkap =  ""+ snapshot.child("NamaLengkap").getValue();
//                profileImage =  ""+ snapshot.child("profileImage").getValue();
//                String status =  ""+ snapshot.child("online").getValue();
//
//                //setUpData
//

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
//    private void cekUserStatus(){
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if (user != null){
//
//        }else {
//            startActivity(new Intent(this, MainPenjualActivity.class));
//        }
//    }
}