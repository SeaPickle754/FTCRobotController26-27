package org.firstinspires.ftc.teamcode.robot;

import static org.firstinspires.ftc.teamcode.util.Configuration.Configuration;
import static org.firstinspires.ftc.teamcode.util.Misc.Clamp;
import static org.firstinspires.ftc.teamcode.util.TrigMath.subtractAngles;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Movement {
    // SETTINGS
    public VectorF position;
    public double turn_rate;
    public VectorF movement_vector;
    public IMU imu;
    public DcMotorEx[] motors=new DcMotorEx[4];
    public double[] motors_velocity={
            0.0, 0.0,
            0.0, 0.0
    };
    public Movement(VectorF default_position, double default_heading, HardwareMap hardwareMap) {
        // Set the default position and default heading
        position=default_position;
        turn_rate=0;
        movement_vector=new VectorF(0.0f,0.0f);

        // CONTROL HUB ORIENTATION SETTINGS
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection,usbDirection);

        // Set the IMU (Gyro) Variable
        imu=hardwareMap.get(IMU.class,"imu");
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        // Set DC Motor variables
        motors= new DcMotorEx[]{
                (DcMotorEx) hardwareMap.get(DcMotor.class, "motor_3"),(DcMotorEx) hardwareMap.get(DcMotor.class, "motor_1"),
                (DcMotorEx) hardwareMap.get(DcMotor.class, "motor_4"),(DcMotorEx) hardwareMap.get(DcMotor.class, "motor_2"),
        };
        motors[0].setDirection(DcMotorSimple.Direction.REVERSE);motors[1].setDirection(DcMotorSimple.Direction.FORWARD);
        motors[2].setDirection(DcMotorSimple.Direction.REVERSE);motors[3].setDirection(DcMotorSimple.Direction.REVERSE);
    }
    // Set default_heading to 0.0 if default_heading isn't given
    public Movement(VectorF default_position, HardwareMap hardwareMap) {this(default_position,0.0,hardwareMap);}
    public void RobotStart() {
        imu.resetYaw();
    } // Reset Yaw to 0
    public void UpdateRobot(Telemetry telemetry) {
        // TODO: Add position tracker
        Arrays.fill(motors_velocity,0.0);
        double total_magnitude=abs(movement_vector.get(0))+abs(movement_vector.get(1))+abs(turn_rate);
        // Horizontal Drive
        double FB_mag=abs(movement_vector.get(1))/total_magnitude;
        for (int i=0;i<motors_velocity.length; i++) {
            motors_velocity[i]+=movement_vector.get(1)*FB_mag;
        }
        //  L/R
        double LR_mag=abs(movement_vector.get(0))/total_magnitude;
        for (int i=0;i<motors_velocity.length; i++) {
            motors_velocity[i]+=
                    movement_vector.get(0)*LR_mag
                            *((i>=1 & i<=2) ? 1.0 : -1.0); // Invert Diagonal Motors
        }
        // Turning
        double TR_mag=abs(turn_rate)/total_magnitude;
        for (int i=0;i<motors_velocity.length; i++) {
            motors_velocity[i]+=
                    turn_rate*TR_mag
                            *(i%2==0 ? -1.0 : 1.0); // Invert Side Motors
        }

        //Set motor Speed
        for (int i=0;i<motors.length; i++) {
            telemetry.addData("Motor_"+i,motors_velocity[i]+","+motors[i].getVelocity()+","+motors[i].getCurrent(CurrentUnit.MILLIAMPS));
            motors[i].setPower(motors_velocity[i]);
        }
        telemetry.update();
    }
    // Get heading from the IMU (Control Hub)
    public double getHeading() {
        YawPitchRollAngles YPRAimu = imu.getRobotYawPitchRollAngles();
        return YPRAimu.getYaw(AngleUnit.RADIANS);
    }
    /**
     * @param target_turn_rate Turing speed of the robot [rad/s]
     */
    public void setTurnSpeed(double target_turn_rate) {
        turn_rate=target_turn_rate;
    }
    public void holdHeading(double target_heading) {
        double INTERVAL=0.001;
        double current_heading=getHeading();
        double turn_diff=subtractAngles(current_heading,target_heading);
        setTurnSpeed(turn_diff*INTERVAL);
    }
    /**
     * @param target_position Target global 2D position [m]
     */
    public void lookToward(VectorF target_position) {
        VectorF targetVector=target_position.subtracted(position);
        double angle2target=atan2(targetVector.get(1),targetVector.get(0));
        holdHeading(angle2target);
    }
    // Drive in target_vector direction (Target Vector is relative to the robots location)
    public void driveTo(VectorF target_vector,double speed,boolean turn_towards) {
        double heading=getHeading();
        double angle2target = atan2(target_vector.get(1), target_vector.get(0));
        if (turn_towards) {
            holdHeading(angle2target);
        }
        movement_vector=new VectorF((float)(cos(angle2target-heading)*speed),(float)(sin(angle2target-heading)*speed));
    }
    public void driveTo(double orientation,double distance,double speed,boolean turn_towards) {
        driveTo(new VectorF(
                (float)(cos(orientation)*distance),
                (float)(sin(orientation)*distance)),
                speed,
                turn_towards);
    }
}
