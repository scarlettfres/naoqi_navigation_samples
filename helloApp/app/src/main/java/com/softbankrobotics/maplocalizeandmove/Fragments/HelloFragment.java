package com.softbankrobotics.maplocalizeandmove.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.object.human.Human;
import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;
import com.aldebaran.qi.sdk.util.FutureUtils;
import com.softbankrobotics.maplocalizeandmove.MainActivity;
import com.softbankrobotics.maplocalizeandmove.R;
import com.softbankrobotics.maplocalizeandmove.Utils.Popup;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.support.constraint.Constraints.TAG;

public class HelloFragment extends android.support.v4.app.Fragment {
    private MainActivity ma;
    private View localView;
    public HumanAwareness humanAwareness;
    TextView textView;

    String humanName = "human";
    Bitmap pictureBitmap;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        int fragmentId = R.layout.fragment_hello;
        this.ma = (MainActivity) getActivity();
        if(ma != null){
            Integer themeId = ma.getThemeId();
            if(themeId != null){
                final Context contextThemeWrapper = new ContextThemeWrapper(ma, themeId);

                LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

                return localInflater.inflate(fragmentId, container, false);
            }else{
                return inflater.inflate(fragmentId, container, false);
            }
        } else {
            Log.e(TAG, "could not get mainActivity, can't create fragment");
            return null;
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        localView = view;
        humanAwareness = ma.robotHelper.humanAwareness;
        textView = localView.findViewById(R.id.hello);
        localView.findViewById(R.id.back_button).setOnClickListener(
                (v) -> ma.setFragment(new GoToFrameFragment(),false));
        ma.robotHelper.releaseAbilities().andThenConsume(fut -> lookAtPeople());
    }

    public void lookAtPeople() {
        Future<Void> animationFuture = ma.robotHelper.animationTosearchPeople();
        Log.d("HELLOFRAGMENT", "lookAtPeople");
        humanAwareness.addOnHumansAroundChangedListener(result -> {
            Future<List<Human>> humansAroundFuture = humanAwareness.async().getHumansAround();
            humansAroundFuture.andThenConsume(humansAround -> {
                if (humansAround.size() > 0) {
                    animationFuture.requestCancellation();
                    ma.say("hello human!");
                    Log.d("HELLOFRAGMENT", "detected people");
                    ma.runOnUiThread(() -> textView.setText("Hello " + humanName + " ! "));
                    ma.runOnUiThread(() -> setPicture(ma.pictureData));
                } else {
                    ma.say("no one ...I am going back to home.");
                    Log.d("HELLOFRAGMENT", "no one");
                    textView.setText("No one is here ! ");
                }
            });
        });
        animationFuture.andThenConsume(
                fut -> humanAwareness.removeAllOnHumansAroundChangedListeners());
    }

    public void setPeopleName(String value)  {
        humanName = value;
    }

    private void setPicture(ByteBuffer data)
    {
        data.rewind();
        final int pictureBufferSize = data.remaining();
        final byte[] pictureArray = new byte[pictureBufferSize];
        data.get(pictureArray);
        Log.i(TAG, "PICTURE RECEIVED! (" + pictureBufferSize + " Bytes)");
        // display picture
        pictureBitmap = BitmapFactory.decodeByteArray(pictureArray, 0, pictureBufferSize);
        ImageView imageView =  (ImageView)localView.findViewById(R.id.hello_image);
        imageView.setImageBitmap(pictureBitmap);
    }



}
