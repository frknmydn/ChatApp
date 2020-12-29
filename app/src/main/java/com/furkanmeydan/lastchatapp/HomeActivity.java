package com.furkanmeydan.lastchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    LayoutInflater layoutInflater;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    Adapter adapter;
    ArrayList<ListItem> list = new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firebaseAuth= FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        layoutInflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        listView=findViewById(R.id.listView);
        adapter= new Adapter();
        listView.setAdapter(adapter);


        databaseReference.child(Child.users).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    //gelen verinin bir classs tipinde alınmasını sağlıyoruz
                    // ve bizim bu classımız UserInfo
                    // Aşağıda UserInfodan bir nesne üretiyoruz ve gerekli bilkgileri
                    // O userInfo classından çekiyoruz.
                    UserInfo info = snapshot.getValue(UserInfo.class);

                    //Aşağıda da arraylistin içinde list item classının içine
                    // gerekli bilgileri constructor sayesinde gönderiyoruz.
                    //
                   if(!info.getUid().equals(firebaseAuth.getUid())){
                       list.add(new ListItem(info.getUid(),info.getName(),info.getPhotoURL()));
                   }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //tıkladığımız pozisyondaki kuıllnaıcı idyi alıp taşıcaz.

                Intent intent = new Intent(HomeActivity.this,TalkActivity.class);
                intent.putExtra("aliciUid",list.get(i).getUid());
                intent.putExtra("aliciName",list.get(i).getName());
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.nacBottomBtn);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.navBtnInbox){
                    startActivity(new Intent(HomeActivity.this,InboxActivity.class));
                }
                else if(item.getItemId()==R.id.navBtnProfil){
                    Toast.makeText(getBaseContext(),"Tıklandı",Toast.LENGTH_LONG).show();

                }

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top,menu);


        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_sign_out){

            firebaseAuth.signOut();
            startActivity(new Intent(HomeActivity.this,SignInActivity.class));

        }

        return super.onOptionsItemSelected(item);
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
            //View view1 = view;
            if(view==null){
                view=layoutInflater.inflate(R.layout.control_row_item_profil,null);


            }
            TextView textName = view.findViewById(R.id.textName);
            CircleImageView circleImageView = view.findViewById(R.id.circleImageView);

            textName.setText(list.get(i).getName());
            Helper.imageLoad(HomeActivity.this,list.get(i).getProfileUrl(),circleImageView);

            return view;
        }
    }

    class ListItem{
        String uid;
        String name;
        String profileUrl;

        public ListItem(String uid, String name, String profileUrl) {
            this.uid = uid;
            this.name = name;
            this.profileUrl = profileUrl;
        }

        public String getUid() {
            return uid;
        }

        public String getName() {
            return name;
        }

        public String getProfileUrl() {
            return profileUrl;
        }
    }
}