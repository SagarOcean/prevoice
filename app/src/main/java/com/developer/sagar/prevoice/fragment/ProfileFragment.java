package com.developer.sagar.prevoice.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.developer.sagar.prevoice.HomeActivity;
import com.developer.sagar.prevoice.LoginActivity;
import com.developer.sagar.prevoice.ProfileDetails;
import com.developer.sagar.prevoice.R;
import com.developer.sagar.prevoice.Splash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileFragment extends Fragment {
    ImageView profileImage;
    TextView profileName,profileAge,profileCountry;
    TextView level,friends,talks,minutes;
    DatabaseReference userRef;
    Button logOut;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.profile_fragment,container,false);
        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profileAge = view.findViewById(R.id.profile_age);
        profileCountry = view.findViewById(R.id.profile_country);
        level = view.findViewById(R.id.level);
        friends = view.findViewById(R.id.friends);
        talks = view.findViewById(R.id.talks);
        minutes = view.findViewById(R.id.minute);
        logOut = view.findViewById(R.id.logOut);


        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("level"))){

                    HashMap<String,Object> hashMap = new HashMap<>();
                    String talks = "0",minutes="0",friends="0",level="1";
                    hashMap.put("talks",talks);
                    hashMap.put("minutes",minutes);
                    hashMap.put("friends",friends);
                    hashMap.put("level",level);

                    userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .updateChildren(hashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {

                                    }

                                }
                            });

                }
                else {
                    userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    String getLevel,getTalks,getFriends,getMinutes;

                                    if(dataSnapshot.hasChild("level")){
                                        getLevel = dataSnapshot.child("level").getValue().toString();
                                        getTalks = dataSnapshot.child("talks").getValue().toString();
                                        getFriends = dataSnapshot.child("minutes").getValue().toString();
                                        getMinutes = dataSnapshot.child("friends").getValue().toString();

                                        level.setText(getLevel);
                                        friends.setText(getFriends);
                                        talks.setText(getTalks);
                                        minutes.setText(getMinutes);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();

                                }
                            });



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        getDetails();
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));

            }
        });

        return  view;
    }

    private void getDetails() {
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String getImage,getName,getAge,getCountry;

                        if(dataSnapshot.exists()){
                            getImage = dataSnapshot.child("image").getValue().toString();
                            Picasso.get().load(getImage).into(profileImage);
                            getName = dataSnapshot.child("name").getValue().toString();
                            getAge = dataSnapshot.child("age").getValue().toString();
                            getCountry = dataSnapshot.child("country").getValue().toString();

                            profileAge.setText(getAge);
                            profileCountry.setText(getCountry);
                            profileName.setText(getName);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
