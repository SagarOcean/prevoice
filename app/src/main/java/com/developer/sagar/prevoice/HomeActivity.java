package com.developer.sagar.prevoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.developer.sagar.prevoice.fragment.FriendFragment;
import com.developer.sagar.prevoice.fragment.HomeFragment;
import com.developer.sagar.prevoice.fragment.ProfileFragment;
import com.developer.sagar.prevoice.fragment.ReadFragment;
import com.developer.sagar.prevoice.fragment.SearchFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    private SpaceNavigationView spaceNavigationView;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_search_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.smiles));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_book_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.avatar));

        spaceNavigationView.setCentreButtonSelectable(true);
        spaceNavigationView.setCentreButtonSelected();

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
               getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new HomeFragment()).commit();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Fragment fragment = null;
                switch (itemIndex){

                    case 0:
                        fragment = new SearchFragment();
                        break;
                    case 1:
                        fragment = new FriendFragment();
                        break;
                    case 2:
                        fragment = new ReadFragment();
                        break;
                    case 3:
                        fragment = new ProfileFragment();
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment).commit();


            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
            }
        });
    }
    private void updateUserStatus(String state){

        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("time",saveCurrentTime);
        hashMap.put("date",saveCurrentDate);
        hashMap.put("state",state);

        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(hashMap);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateUserStatus("offline");
    }


    @Override
    protected void onStart() {
        super.onStart();
        updateUserStatus("online");
    }


}
