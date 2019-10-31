package com.softbankrobotics.maplocalizeandmove.Utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.aldebaran.qi.sdk.builder.TransformBuilder;
import com.aldebaran.qi.sdk.object.actuation.Frame;
import com.aldebaran.qi.sdk.object.geometry.Quaternion;
import com.aldebaran.qi.sdk.object.geometry.Transform;
import com.aldebaran.qi.sdk.object.geometry.Vector3;

import java.io.Serializable;

public class vector2Theta implements Parcelable, Serializable {
    private double x, y, theta;

    private vector2Theta(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;
    }

    /**
     * creates a Vector2theta representing the translation between two frames and angle
     * @param frameOrigin the origin of the translation
     * @param frameTarget the end of the translation
     * @return the Vector2theta to go from frameOrigin to frameDestination
     */
    public static vector2Theta betweenFrames(@NonNull Frame frameTarget, @NonNull Frame frameOrigin) {
        // Compute the transform to go from "frameOrigin" to "frameTarget"
        Transform transform = frameTarget.async().computeTransform(frameOrigin).getValue().getTransform();

        // Extract translation from the transform
        Vector3 translation = transform.getTranslation();
        // Extract quaternion from the transform
        Quaternion quaternion = transform.getRotation();

        // Extract the 2 coordinates from the translation and orientation angle from quaternion
        return new vector2Theta(translation.getX(), translation.getY(), NavUtils.getYawFromQuaternion(quaternion));
    }

    /**
     * Returns a transform representing the translation described by this Vector2theta
     * @return the transform
     */
    public Transform createTransform() {
        // this.theta is the radian angle to appy taht was serialized
        return TransformBuilder.create().from2DTransform(this.x, this.y, this.theta);
    }

    /***************** PARCELABLE REQUIREMENTS *******************/

    private vector2Theta(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
        theta = in.readDouble();
    }

    public static final Creator<vector2Theta> CREATOR = new Creator<vector2Theta>() {
        @Override
        public vector2Theta createFromParcel(Parcel in) {
            return new vector2Theta(in);
        }

        @Override
        public vector2Theta[] newArray(int size) {
            return new vector2Theta[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(x);
        parcel.writeDouble(y);
        parcel.writeDouble(theta);
    }
}