package com.developer.sagar.prevoice.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.sagar.prevoice.R;
import com.developer.sagar.prevoice.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SearchFragment extends Fragment {
    private RecyclerView findFriendList;
    private EditText search;
    private String str="";
    private DatabaseReference usersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.search_fragment,container,false);
        search = view.findViewById(R.id.edtsearch);
        findFriendList = view.findViewById(R.id.find_people_list);
        findFriendList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(search.getText().toString().equals(""))
                    Toast.makeText(getActivity().getApplicationContext(),"Please write name to search",Toast.LENGTH_SHORT).show();
                else {
                    str = charSequence.toString();
                    onStart();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        return  view;
    }
    public static class FindFriendViewHolder extends RecyclerView.ViewHolder{
        TextView userName,userAge,userCountry;
        Button addFriend;
        ImageView profileImage;
        RelativeLayout cardView;

        public FindFriendViewHolder(@NonNull View item) {
            super(item);
            userName = item.findViewById(R.id.text_contact);
            userAge = item.findViewById(R.id.contact_age);
            userCountry = item.findViewById(R.id.contact_country);
            addFriend = item.findViewById(R.id.add_friend);
            cardView = item.findViewById(R.id.cardView1);
            profileImage = item.findViewById(R.id.contact_image);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> options = null;

        if(str.equals("")) {

            options = new FirebaseRecyclerOptions.Builder<User>()
                    .setQuery(usersRef, User.class).build();
        }else {
            options= new FirebaseRecyclerOptions.Builder<User>()
                    .setQuery(usersRef.orderByChild("name")
                                    .startAt(str)
                                    .endAt(str + "\uf0ff")
                            ,User.class)
                    .build();
        }
        FirebaseRecyclerAdapter<User,FindFriendViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<User,FindFriendViewHolder>(options){

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_design,parent,false);
                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                        return viewHolder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int i, @NonNull final User user) {

                        holder.userName.setText(user.getName());
                        holder.userCountry.setText(user.getCountry());
                        holder.userAge.setText(user.getAge());
                        Picasso.get().load(user.getImage()).into(holder.profileImage);

                        holder.addFriend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String visit_user_id = getRef(i).getKey();
                                Intent intent = new Intent(getActivity().getApplicationContext(),FriendFragment.class);
                                intent.putExtra("visit_user_id",visit_user_id);
                                intent.putExtra("profile_image",user.getImage());
                                intent.putExtra("profile_name",user.getName());
                                startActivity(intent);

                            }
                        });
                    }
                };
        findFriendList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
}
