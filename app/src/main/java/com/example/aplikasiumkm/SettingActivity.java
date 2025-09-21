package com.example.aplikasiumkm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Set;

public class SettingActivity extends AppCompatActivity {

    private ImageButton back_btn;
    private SwitchCompat Ntf_Swicth;
    private TextView StatusNotificationTv;

    private static final String enableMessage = "Notification are enabled";
    private static final String disableMessage = "Notification are disabled";

    private FirebaseAuth firebaseAuth;

    private boolean isChecked = false;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        back_btn = findViewById(R.id.back_btn);
        Ntf_Swicth = findViewById(R.id.Ntf_Swicth);
        StatusNotificationTv = findViewById(R.id.StatusNotificationTv);

        firebaseAuth = FirebaseAuth.getInstance();

        sp = getSharedPreferences("SETTING_SP", MODE_PRIVATE);
        isChecked = sp.getBoolean("FCM_ENABLE", false);

        Ntf_Swicth.setChecked(isChecked);
        
        if (isChecked){
            StatusNotificationTv.setText(enableMessage);
        }else {
            StatusNotificationTv.setText(disableMessage);
        }

        Ntf_Swicth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){

                    subscribeToTopic();
                }else {

                    unSubscriberToTopic();
                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    private void subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                spEditor = sp.edit();
                spEditor.putBoolean("FCM_ENABLE", true);
                spEditor.apply();

                Toast.makeText(SettingActivity.this, ""+enableMessage , Toast.LENGTH_SHORT).show();
                StatusNotificationTv.setText(enableMessage);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void unSubscriberToTopic(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FCM_TOPIC).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                spEditor = sp.edit();
                spEditor.putBoolean("FCM_ENABLE", false);
                spEditor.apply();
                Toast.makeText(SettingActivity.this, ""+disableMessage , Toast.LENGTH_SHORT).show();
                StatusNotificationTv.setText(disableMessage);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}