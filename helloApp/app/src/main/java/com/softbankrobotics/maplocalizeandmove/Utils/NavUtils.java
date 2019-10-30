package com.softbankrobotics.maplocalizeandmove.Utils;

import android.support.v4.math.MathUtils;
import android.util.Log;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.actuation.Frame;
import com.aldebaran.qi.sdk.object.geometry.Quaternion;
import com.aldebaran.qi.sdk.object.geometry.Transform;
import com.aldebaran.qi.sdk.object.geometry.Vector3;
import com.aldebaran.qi.sdk.util.FutureUtils;

import static java.lang.Math.atan2;
public class NavUtils {
    /**
     * Gets the "yaw" (or "theta") angle from a quaternion (the only angle relevant for navigation)
     */
    static double getYawFromQuaternion(Quaternion quaternion) {

         return Math.atan2(2.0f*(quaternion.getX() * quaternion.getY() - quaternion.getZ()*quaternion.getW()) ,
                1.0f - 2.0f*(Math.pow(quaternion.getY(), 2) + Math.pow(quaternion.getZ(), 2)));
    }
    /**
     * Tries to directly go to given pos and angle, in straight line. Returns a future.
     */
    static Future<Void> goStraightToPos(QiContext qiContext, double x, double y, double theta) {

        double timeMax = 3.0f;
        double angleMax = Math.PI;
        double duration = theta/angleMax * timeMax;


        String animationString = String.format("[\"Holonomic\", [\"Line\", [%f, %f]], %f, %f]", x, y, theta, duration);
        Animation animation = AnimationBuilder.with(qiContext).withTexts(animationString).build();
        Animate animate = AnimateBuilder.with(qiContext).withAnimation(animation).build();
        return animate.async().run();}

    static Future<Void> goStraightToFrame(QiContext qiContext, Frame frame) {
        Transform deltaTransform = frame.computeTransform(qiContext.getActuation().robotFrame()).getTransform();
        double theta = getYawFromQuaternion(deltaTransform.getRotation());
        Vector3 translation = deltaTransform.getTranslation();
        return goStraightToPos(qiContext, translation.getX(), translation.getY(), theta);}
    /**
     * Turn to align with a given frame. Returns a future.
     */


    // align with the angle of the frame.
    public static Future<Void> alignWithTarget(QiContext qiContext, Frame frame) {
        Log.d("NAVUTILS", "alignWithTarget: ");
        Transform deltaTransform = frame.computeTransform(qiContext.getActuation().robotFrame()).getTransform();
        double dx = deltaTransform.getTranslation().getX();
        double dy = deltaTransform.getTranslation().getY();
        if (dx < 0.1f && dy < 0.3f) {
            double angle = atan2(dy, dx);
            Log.d("NAVUTILS", "alignWithTarget: " + angle);
            return goStraightToPos(qiContext, 0, 0, angle);
        }
        return Future.of(null);
    }


    // look at target frame.
    public static Future<Void> alignWithFrame(QiContext qiContext, Frame frame) {
        Log.d("NAVUTILS", "alignWithFrame: ");
        Transform deltaTransform = frame.computeTransform(qiContext.getActuation().robotFrame()).getTransform();
        Quaternion quaternion = deltaTransform.getRotation();
        double theta = getYawFromQuaternion(quaternion);
        Log.d("NAVUTILS", "alignWithFRAME: " + theta);
        return goStraightToPos(qiContext, 0, 0, theta);

    }
}