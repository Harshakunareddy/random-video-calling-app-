package com.harsha.calling.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.calling.databinding.ActivityRewardingAdsBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class RewardingAdsActivity extends AppCompatActivity {


    ActivityRewardingAdsBinding binding;
    private RewardedAd mRewardedAd;
    FirebaseDatabase database;
    String currentUid;
    int coins= 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRewardingAdsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();
        loadAd();
        database.getReference().child("profiles").child(currentUid).child("coins")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        coins = snapshot.getValue(Integer.class);
                        binding.coins.setText("You Have:" + coins);
                        //binding.coins.setText(String.valueOf(coins));
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });
        binding.videoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRewardedAd != null) {
                    Activity activityContext = RewardingAdsActivity.this;
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            loadAd();
                            coins = coins + 5;
                            database.getReference().child("profiles").child(currentUid).child("coins").setValue(coins);
                           // binding.video1Icon.setImageResource(R.drawable.check);
                        }
                    });
                } else {
                }
            }
        });
    }
    //
    void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        //this.mRewardedAd.load(adRequest);
           RewardedAd.load(this, "ca-app-pub-8576897844225425/7146905460", adRequest, new RewardedAdLoadCallback() {
        
            //RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", adRequest, new RewardedAdLoadCallback()   { 8897232724
            
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error.
                 mRewardedAd = null;
            }
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                mRewardedAd = rewardedAd;
            }
        });
    }
}
