package org.firstinspires.ftc.teamcode.util;

import static java.lang.Math.PI;


public class Conversion {
    public static float in2m(float value) {return value/39.3701f;}
    public static double in2m(double value) {return value/39.3701;}

    public static float deg2rad(float value) {return value*(float) PI/180.0f;}
    public static double deg2rad(double value) {return value*PI/180.0;}
}
