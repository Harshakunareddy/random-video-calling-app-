package com.harsha.calling.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.calling.databinding.ActivityMainBinding;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harsha.calling.models.User;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {


    //private AdView mAdView;
    ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    long coins = 0;
    String[] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    private int requestCode = 1;
    User user;
    public String Firebase;
    int counter = 0;

    private InterstitialAd minterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
// banner ads declaration
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        loadAd();
       // mAdView = findViewById(R.id.adView);
        AdRequest adRequest= new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

        //banner ads complete

        ProgressDialog progress=new ProgressDialog(this);
        progress.setCancelable(true);
        progress.setProgress(0);
        progress.setMessage("Loading....");
        progress.setMax(100);
        progress.show();


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        database.getReference().child("profiles")
                .child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        progress.dismiss();
                        user = snapshot.getValue(User.class);
                        coins = user.getCoins();
                        binding.coins.setText("Your Coins:" + coins);
                        Glide.with(MainActivity.this)
                                .load(user.getProfile())
                                .into(binding.profilePicture);
                        //getDbCount();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });

        /*binding.findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPermissionsGranted()) {
                    database.getReference().child("profiles")
                            .child(currentUser.getUid())
                            .child("coins")
                            .setValue(coins);
                    Intent intent = new Intent(MainActivity.this, ConnectingActivity.class);
                    intent.putExtra("profile", user.getProfile());
                    startActivity(intent);
                    /*if (coins >= 1) {
                        coins = coins - 1;
                        database.getReference().child("profiles")
                                .child(currentUser.getUid())
                                .child("coins")
                                .setValue(coins);
                        Intent intent = new Intent(MainActivity.this, ConnectingActivity.class);
                        intent.putExtra("profile", user.getProfile());
                        startActivity(intent);
//startActivity(new Intent(MainActivity.this, ConnectingActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Insufficient Coins", Toast.LENGTH_SHORT).show();
                    }
                } else { askPermissions();
                }
            }
        });*/



        binding.findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                counter += 1;

                if (counter < 5) {
                    if(isPermissionsGranted()) {
                        database.getReference().child("profiles")
                                .child(currentUser.getUid())
                                .child("coins")
                                .setValue(coins);
                        Intent intent = new Intent(MainActivity.this, ConnectingActivity.class);
                        intent.putExtra("profile", user.getProfile());
                        startActivity(intent);
                    /*if (coins >= 1) {
                        coins = coins - 1;
                        database.getReference().child("profiles")
                                .child(currentUser.getUid())
                                .child("coins")
                                .setValue(coins);
                        Intent intent = new Intent(MainActivity.this, ConnectingActivity.class);
                        intent.putExtra("profile", user.getProfile());
                        startActivity(intent);
//startActivity(new Intent(MainActivity.this, ConnectingActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Insufficient Coins", Toast.LENGTH_SHORT).show();
                    }*/
                    } else { askPermissions();
                    }
                }

                else{

                    if(minterstitialAd!=null){
                        minterstitialAd.show(MainActivity.this);
                        minterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                minterstitialAd =null;
                                counter=0;
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                minterstitialAd =null;
                                counter=0;
                                loadAd();

                                if(isPermissionsGranted()) {
                                    database.getReference().child("profiles")
                                            .child(currentUser.getUid())
                                            .child("coins")
                                            .setValue(coins);
                                    Intent intent = new Intent(MainActivity.this, ConnectingActivity.class);
                                    intent.putExtra("profile", user.getProfile());
                                    startActivity(intent);
                    /*if (coins >= 1) {
                        coins = coins - 1;
                        database.getReference().child("profiles")
                                .child(currentUser.getUid())
                                .child("coins")
                                .setValue(coins);
                        Intent intent = new Intent(MainActivity.this, ConnectingActivity.class);
                        intent.putExtra("profile", user.getProfile());
                        startActivity(intent);
//startActivity(new Intent(MainActivity.this, ConnectingActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Insufficient Coins", Toast.LENGTH_SHORT).show();
                    }*/
                                } else { askPermissions();
                                }

                            }
                        });

                    }

                    else{

                        Toast.makeText(MainActivity.this,"ad is loading",Toast.LENGTH_SHORT).show();


                    }

                }
            }

        });




        binding.rewardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RewardingAdsActivity.class));
            }
        });
    }



    void askPermissions(){
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    private boolean isPermissionsGranted() {
        for(String permission : permissions ){
            if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }

    /* public void getDbCount() {
        FirebaseDatabase listRef = new FirebaseDatabase("https://random-calling-app-5d799-default-rtdb.firebaseio.com/presence/");
        final Firebase userRef = listRef.push();
// Add ourselves to presence list when online.
        Firebase presenceRef = new Firebase("https://random-calling-app-5d799-default-rtdb.firebaseio.com/.info/connected");
        ValueEventListener myPresence = new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snapshot) {
// Remove ourselves when we disconnect.
                userRef.onDisconnect().removeValue();
                userRef.setValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

            @Override public void onCancelled(FirebaseError firebaseError) {
                Log.e("DBCount", "The read failed: " + firebaseError.getMessage());
            }
        };
        presenceRef.addValueEventListener(myPresence);
// Number of online users is the number of objects in the presence list.
        ValueEventListener myList = new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snapshot) {
// Remove ourselves when we disconnect.
                Log.i("DBCount", "# of online users = " + String.valueOf(snapshot.getChildrenCount()));
            }
            @Override public void onCancelled(FirebaseError firebaseError) {
                Log.e("DBCount", "The read failed: " + firebaseError.getMessage());
            }
        };android:value="ca-app-pub-3940256099942544~3347511713"/>
        listRef.addValueEventListener(myList);
    }*/


    private void loadAd() {

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-8576897844225425/1958484932", adRequest, new InterstitialAdLoadCallback() {

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                minterstitialAd = interstitialAd;


            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                minterstitialAd = null;
            }
        });

    }

}



