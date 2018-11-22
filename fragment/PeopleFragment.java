package com.liptalker.home.liptalker.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.liptalker.home.liptalker.R;
import com.liptalker.home.liptalker.friendClickMessage.FriendNameClickMessage;
import com.liptalker.home.liptalker.model.MyModel;
import com.liptalker.home.liptalker.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment {
    public static final int sub = 1001; /*다른 액티비티를 띄우기 위한 요청코드(상수)*/
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Handler mHandler = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.peoplefragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());

        return view;
    }
    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        List<UserModel> userModels;
        List<MyModel> myModels;

        //데이터베이스에서 친구이름 ,친구전화번호, 친구이미지다운로드주소 가져오기
        public PeopleFragmentRecyclerViewAdapter(){
            //인터넷 연결이 되지 않은 상태에서 동작하게 하기 위해 친구의 데이터를 캐시에 저장하기
            try {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            }catch (DatabaseException e){}
            //내 전화번호 가져오기

            final String myPhoneNumber = user.getPhoneNumber();
            userModels = new ArrayList<>();
            myModels = new ArrayList<>();
            //데이터 가져오기를 스레드 형태로 만들어 백그라운드에서 동작하게 하기
            mHandler = new Handler(); Thread t = new Thread(new Runnable(){
                @Override public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseReference databaseReference =
                                FirebaseDatabase.getInstance().getReference().child("users");
                        Log.i("level", databaseReference.getKey());
                        databaseReference.keepSynced(true);
                            databaseReference.addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        userModels.clear();
                                        try {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if (!snapshot.getKey().equals(myPhoneNumber)) {
                                                    userModels.add(snapshot.getValue(UserModel.class));
                                                } else {
                                                    //친구 목록중 내번호와 같은 친구를 나로 인식하여 데이터 저장 및 뷰 설정
                                                    myModels.add(snapshot.getValue(MyModel.class));
                                                    try {
                                                        ImageView myImageView = (ImageView) getView().findViewById(R.id.myProfile_ImageView);
                                                        TextView myNameTextView = (TextView) getView().findViewById(R.id.myName_TextView);

                                                        myNameTextView.setText(myModels.get(0).userName);
                                                        String url = myModels.get(0).profileImageUrl;
                                                        Glide.with(getView())
                                                                .load(url).apply(new RequestOptions().circleCrop())
                                                                .into(myImageView);
                                                    } catch (NullPointerException e) {
                                                    }
                                                }
                                            }
                                            notifyDataSetChanged();
                                        }catch (DatabaseException e){}
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                    }
                });
            }
            });
            t.start();

        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends,
                    parent,false);

            return new CustomViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder,final int position) {
            //이미지 넣어주는 곳
            try {
                //저장한 친구들의 데이터를 갖고 뷰 설정
                final String friendPhoneNumber = userModels.get(position).phoneNumber;
                final String friendName = userModels.get(position).userName;
                ((CustomViewHolder) holder).textView.setText(friendName);
                final String url = userModels.get(position).profileImageUrl;
                Log.i("wow", url);
                Glide.with(holder.itemView.getContext())
                        .load(url).apply(new RequestOptions().circleCrop())
                        .into(((CustomViewHolder) holder).imageView);
                ((CustomViewHolder) holder).textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(getActivity().getApplicationContext(), FriendNameClickMessage.class);
                            intent.putExtra("friendPhoneNumber", friendPhoneNumber);
                            intent.putExtra("friendName", friendName);
                            intent.putExtra("url", url);
                            startActivity(intent);
                        }catch (Exception e){}
                    }
                });
                }catch (NullPointerException e){}
                }
        @Override
        public int getItemCount() {
            return userModels.size();
        }
        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView)view.findViewById(R.id.frienditem_imageview);
                textView = (TextView)view.findViewById(R.id.frienditem_textview);
            }
        }
    }
}
