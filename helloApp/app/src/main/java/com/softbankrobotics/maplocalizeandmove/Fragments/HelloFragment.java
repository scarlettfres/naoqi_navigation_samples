package com.softbankrobotics.maplocalizeandmove.Fragments;

import android.content.Context;
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
import com.softbankrobotics.maplocalizeandmove.MainActivity;
import com.softbankrobotics.maplocalizeandmove.R;
import com.softbankrobotics.maplocalizeandmove.Utils.Popup;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class HelloFragment extends android.support.v4.app.Fragment {
    private MainActivity ma;
    private View localView;
    public HumanAwareness humanAwareness;
    private Popup humanPopup;
    private Button close;
    private String humanName = "human";

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
        }else{
            Log.e(TAG, "could not get mainActivity, can't create fragment");
            return null;
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        localView = view;
        humanAwareness = ma.robotHelper.humanAwareness;

        humanPopup = new Popup(R.layout.popup_hello_human, this, ma);
        close = humanPopup.inflator.findViewById(R.id.close_button);
        close.setOnClickListener((v) -> {
            humanPopup.dialog.hide();
            ma.setFragment(new GoToFrameFragment(),true);
        });
        lookAtPeople();
    }

    public void lookAtPeople() {

        if (humanAwareness != null) {
            // Get the humans around the robot.
            Future<List<Human>> humansAroundFuture = humanAwareness.async().getHumansAround();
            humansAroundFuture.andThenConsume(humansAround -> {
                if(humansAround.size() > 0) {
                    ma.say("hello human!");
                    ma.setFragment(new GoToFrameFragment(),true);
                } else {
                    ma.say("no one ...I am going back to home.");
                    ma.setFragment(new GoToFrameFragment(),true);
                }
            });
        }
    }
    public void setPeopleName(String value) {
        humanName = value;
    }

}
