package com.furkanmeydan.lastchatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TalkActivity extends AppCompatActivity {


    LayoutInflater layoutInflater;
    Adapter adapter;
    ChatInbox chatInbox=null;
    ArrayList<ListItem> list = new ArrayList<>();

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String aliciUid, aliciName, gonderenUid;

    Button btnGonder;
    ListView listView;
    EditText editMesaj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);

        layoutInflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView= findViewById(R.id.listView);
        editMesaj=findViewById(R.id.editMesaj);
        btnGonder = findViewById(R.id.btnGonder);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        gonderenUid=firebaseAuth.getUid();
        aliciUid= getIntent().getExtras().getString("aliciUid");
        aliciName= getIntent().getExtras().getString("aliciName");

        Toast.makeText(this,aliciName + " " + aliciUid , Toast.LENGTH_LONG).show();
        adapter = new Adapter();
        listView.setAdapter(adapter);

        createChat();

        //Mesajı gönderme butonu.
        btnGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child(Child.CHATS).push().setValue(
                        new Chats(chatInbox.getInboxKey(), gonderenUid, editMesaj.getText().toString()),
                        new DatabaseReference.CompletionListener() {
                            //İşlem baraşılı ise.
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                editMesaj.setText("");// her mesaj göndermeden sonra temizlenmesi için.

                                final String mesajKey= ref.getKey(); //Son gönderilen data'nın keyini gönderir.
                                //ARdından son mesaj child'ını güncellemememiz laızm.

                                databaseReference.child(Child.CHAT_LAST).orderByChild("inboxKey")
                                        .equalTo(chatInbox.getInboxKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                            //Yeni mesaj geldiğinde son mesajı chat_last içine göndermemiz lazım.
                                            databaseReference.child(Child.CHAT_LAST).child(snapshot.getKey())
                                                    .child("mesajKey").setValue(mesajKey);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                                //Gelen kutusundaki okunma bilgisini 1 yapıcaz.
                                databaseReference.child(Child.CHAT_INBOX).orderByChild("inboxKey").equalTo(chatInbox.getInboxKey())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                    if(aliciUid.equals(snapshot.child("gonderenUid").getValue().toString())){
                                                        databaseReference.child(Child.CHAT_INBOX).child(snapshot.getKey()).child("okundu").setValue("1");


                                                    }
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });



                            }
                        }
                );
            }
        });

    }

     void createChat() {

        databaseReference.child(Child.CHAT_INBOX).orderByChild("gonderenUid").equalTo(gonderenUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    //Kendine ait olan (gonderend = ben) inboxları getirmesi için
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            if(snapshot.getValue(ChatInbox.class).getAliciUid().equals(aliciUid)){
                                chatInbox = snapshot.getValue(ChatInbox.class);
                                Log.d("TAGG2", "Hayır boş değil");


                            }






                        }




                        chatInboxAndChatLast(); // eğer o konuşma daha varsa o gelen kutusunu alacak yoksa gelen kutusunu yaratacak

                        chats(); // konuşmaları yükleyecek metot

                        chatLast(); // Son bir mesaj geldiğinde o son mesajı listView'a ekleyecek metot



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });



    }

     void chatInboxAndChatLast() {

            if(chatInbox==null) {


                //Önceden konuşma olmamışsa
                //ChatInbox create

                String key = databaseReference.push().getKey();
                databaseReference.child(Child.CHAT_INBOX).push().setValue(
                        new ChatInbox(key, gonderenUid, aliciUid, "0")
                );

                databaseReference.child(Child.CHAT_INBOX).push().setValue(
                        new ChatInbox(key, aliciUid, gonderenUid, "0")
                );

                chatInbox = new ChatInbox(key,gonderenUid,aliciUid,"0");
                //chatLast Create-- Konuşma olmadıysa son mesaj da yoktur o da yapılır.
                databaseReference.child(Child.CHAT_LAST).push().setValue(
                        new ChatLast(key, "")
                );
            }


    }
    void chats() {

        //Bu metodda getinboxkey() null object veriyor neden veriyor hiçbir fikrim yok ama ChatInbox
        //içinde public olarak tanımlandığı için nesneden chatInbox.inboxKey'i kullanabiliyoruz
        //VE ONU NULL VERMİYOR

        // alternatif şekilde çözümünü buldum. ChatInbox classında return getInboxKey();'i
        //return this.getInboxKey(); olarak değiştirdim. Düzeldi neden bir fikrim yok.

        databaseReference.child(Child.CHATS).orderByChild("inboxKey").equalTo(chatInbox.getInboxKey()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snp : snapshot.getChildren()){
                            Chats chats = snp.getValue(Chats.class);
                            list.add(new ListItem(chats.getGonderenUid(),chats.getMesaj()));

                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
     void chatLast(){


        databaseReference.child(Child.CHAT_LAST).orderByChild("inboxKey").equalTo(chatInbox.getInboxKey()).
                addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    databaseReference.child(Child.CHATS).child(snapshot.child("mesajKey").getValue().toString()).
                            addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Chats chats = snapshot.getValue(Chats.class);
                                    list.add(new ListItem(chats.getGonderenUid(),chats.getMesaj()));
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




    }






    class Adapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = view;
            if(view==null){
                view1= layoutInflater.inflate(R.layout.control_row_item_talk,null);
            }
            LinearLayout linearTalk = view1.findViewById(R.id.linearTalk);
            LinearLayout linearRow = view1.findViewById(R.id.linearTalkforGravity);

            if(list.get(i).getGonderenUid().equals(gonderenUid)){
                //Arka plan gönderisi set edicez
                linearTalk.setBackgroundResource(R.drawable.draw_talk);
                linearRow.setGravity(Gravity.RIGHT);
            }
            else{
                linearTalk.setBackgroundResource(R.drawable.o_talk);
                linearRow.setGravity(Gravity.LEFT);
            }


            TextView textMesaj = view1.findViewById(R.id.textMesaj);
            textMesaj.setText(list.get(i).getMesaj());


            return view1;
        }
    }

    class ListItem{
        String gonderenUid;
        String mesaj;

        public ListItem(String gonderenUid, String mesaj) {
            this.gonderenUid = gonderenUid;
            this.mesaj = mesaj;
        }

        public String getGonderenUid() {
            return gonderenUid;
        }

        public String getMesaj() {
            return mesaj;
        }
    }
}