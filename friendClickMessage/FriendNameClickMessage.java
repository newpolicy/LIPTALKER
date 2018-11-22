package com.liptalker.home.liptalker.friendClickMessage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.liptalker.home.liptalker.R;
import com.liptalker.home.liptalker.model.UserModel;

public class FriendNameClickMessage extends AppCompatActivity {

    private ImageView friendImage;
    private TextView friendName;
    private TextView friendPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_click_message);

        friendImage = (ImageView)findViewById(R.id.friendImage_ImageView_FriendClickMessage);
        friendName = (TextView)findViewById(R.id.friendName_TextView_FriendClickMessage);
        friendPhoneNumber = (TextView)findViewById(R.id.friendPhoneNumber_TextView_FriendClickMessage);

        try {
            Intent intent = getIntent();
            String phoneNumber = intent.getExtras().getString("friendPhoneNumber");
            String name = intent.getExtras().getString("friendName");
            String url = intent.getExtras().getString("url");
            UserModel userModels = (UserModel) intent.getSerializableExtra("userClass");
            friendPhoneNumber.setText(phoneNumber);
            friendName.setText(name);
            Glide.with(this)
                    .load(url).apply(new RequestOptions().circleCrop())
                    .into(friendImage);
        }catch (NullPointerException e){

        }

    }
}
