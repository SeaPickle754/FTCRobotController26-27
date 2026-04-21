package org.firstinspires.ftc.teamcode.util;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.PI;
import static java.lang.Math.tan;

public class TrigMath {
    public static <T extends Number> double subtractAngles(T a1, T a2) {
        double A1=(double)a1;
        double A2=(double)a2;
        // Using individual variables is faster than compacting it into one
        double cos1=cos(A1);
        double sin1=sin(A1);
        double cos2=cos(A2);
        double sin2=sin(A2);
        double X=cos1*cos2+sin1*sin2;
        double Y=sin1*cos2-cos1*sin2;
        return atan2(Y,X);
    }
}
