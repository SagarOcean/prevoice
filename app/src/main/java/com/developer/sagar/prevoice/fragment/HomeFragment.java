package com.developer.sagar.prevoice.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.developer.sagar.prevoice.CallingActivity;
import com.developer.sagar.prevoice.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private LottieAnimationView callnow;
    private LottieAnimationView homeGif;
    private LottieAnimationView acceptCall;
    private LottieAnimationView hangup;
    private ImageView recieverImageView;
    private static final String APP_KEY = "840aa5bb-975b-450b-8bd5-049dee5bb652";
    private static final String APP_SECRET = "o81op9Z7qUusUDVyt1d8IA==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";
    private String recieverName, recieverImage;
    private RelativeLayout recieverLayout;
    private String callerId;
    private String recipientId;
    private boolean flag=false;
    private DatabaseReference userRef;
    private ArrayList<String> recievers = new ArrayList<>();
    private ArrayList<String> readyToCall = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        callnow = view.findViewById(R.id.call_now);
        homeGif = view.findViewById(R.id.home_gif);
        hangup = view.findViewById(R.id.hang_up);
        acceptCall = view.findViewById(R.id.accept_call);
        recieverLayout = view.findViewById(R.id.reciever_layout);
        recieverImageView = view.findViewById(R.id.reciever_image);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        callerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission
                (getActivity().getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE},
                    1);
        }

        // Retrieve all the user who are online

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot users : dataSnapshot.getChildren()) {
                    String stateValue = users.child("state").getValue().toString();
                    if (stateValue.equals("online")) {
                        String uid = users.child("uid").getValue().toString();
                        if (!(uid.equals(callerId)))
                            recievers.add(uid);

                    }

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        callnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readyToCall.add(callerId);

            }
        });

        for (String ready:readyToCall){
            if(ready.equals(callerId)){
                flag = true;
                break;
            }
        }
        if(flag){
            for (String ready:readyToCall){
                if(!(ready.equals(callerId)))
                    incomingCall(callerId,ready);
            }
        }

        return view;
    }

    private void incomingCall(String callerIdstring, String reciever) {
        final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUserId.equals(callerIdstring)) {
            recipientId = reciever;
            retrieveRecieverData();
            homeGif.setVisibility(View.GONE);
            callnow.setVisibility(View.GONE);
            recieverLayout.setVisibility(View.VISIBLE);
            acceptCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), CallingActivity.class);
                    intent.putExtra("recieverImage", recieverImage);
                    intent.putExtra("recieverName", recieverName);
                    intent.putExtra("senderUid", currentUserId);
                    startActivity(intent);
                    getActivity().finish();
                }
            });

            hangup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recieverLayout.setVisibility(View.GONE);
                    homeGif.setVisibility(View.VISIBLE);
                    callnow.setVisibility(View.VISIBLE);
                    return;
                }
            });

        } else {
            return;
        }
    }


    private void retrieveRecieverData() {

        userRef.child(recipientId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        recieverImage = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(recieverImage).into(recieverImageView);
                        recieverName = dataSnapshot.child("name").getValue().toString();
                        Toast.makeText(getActivity().getApplicationContext(), recieverName + " is calling", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}
