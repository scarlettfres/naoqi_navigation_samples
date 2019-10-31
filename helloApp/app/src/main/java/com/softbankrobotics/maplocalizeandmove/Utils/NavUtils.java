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

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
public class NavUtils {
    /**
     * Gets the "yaw" (or "theta") angle from a quaternion (the only angle relevant for navigation)
     */
    static double getYawFromQuaternion(Quaternion q) {
        // yaw (z-axis rotation)
        double x = q.getX();
        double y = q.getY();
        double z = q.getZ();
        double w = q.getW();
        double sinYaw = 2.0 * (w * z + x * y);
        double cosYaw = 1.0 - 2.0 * (y * y + z * z);
        return atan2(sinYaw, cosYaw);
    }
    
    /**
     * Tries to directly go to given pos and angle, in straight line. Returns a future.
     */
    static Future<Void> goStraightToPos(QiContext qiContext, double x, double y, double theta) {
        double timeMax = 5.0f;
        double angleMax = Math.PI;
        double duration = abs(theta)/angleMax * timeMax;
        Log.d("NAVUTILS", "goStraightToPos: " + duration);
        String animationString = String.format("[\"Holonomic\", [\"Line\", [%f, %f]], %f, %f]", x, y, theta, duration);
        Animation animation = AnimationBuilder.with(qiContext).withTexts(animationString).build();
        Animate animate = AnimateBuilder.with(qiContext).withAnimation(animation).build();
        return animate.async().run();}

    static Future<Void> stayAtPoseFor(QiContext qiContext, double time) {
        String animationString = String.format("[\"Holonomic\", [\"Line\", [0.0, 0.0]], 0.0, %f]", time);
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
        Transform deltaTransform = frame.computeTransform(qiContext.getActuation().robotFrame()).getTransform();

        Transform inMapTransf = frame.computeTransform(qiContext.getMapping().mapFrame()).getTransform();

        Quaternion quaternion = deltaTransform.getRotation();
        double theta = getYawFromQuaternion(quaternion);

        Quaternion quaternionInMAp = inMapTransf.getRotation();
        double thetaInMap = getYawFromQuaternion(quaternionInMAp);


        Log.d("NAVUTILS", "frame in map: x = " + inMapTransf.getTranslation().getX() +
                "y =" + inMapTransf.getTranslation().getY() + "theta = " + thetaInMap);
        Log.d("NAVUTILS", "Quat in map: w = " + inMapTransf.getRotation().getW() + "x =" +
                inMapTransf.getRotation().getX() + "y = " +  inMapTransf.getRotation().getY() +
                "z =" +  inMapTransf.getRotation().getZ());
        Log.d("NAVUTILS", "frame in Robot: x = " + deltaTransform.getTranslation().getX()
                + "y =" + deltaTransform.getTranslation().getY() + "theta = " + theta);

        return goStraightToPos(qiContext, 0, 0, theta);

    }
}