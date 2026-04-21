package org.firstinspires.ftc.teamcode.robot;

import static org.firstinspires.ftc.teamcode.util.Misc.Clamp;
import static org.firstinspires.ftc.teamcode.util.TrigMath.subtractAngles;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import org.firstinspires.ftc.teamcode.util.Configuration;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class Movement {

    public VectorF position;
    public double turn_rate;
    public VectorF movement_vector;
    public IMU imu;
    public DcMotorEx left_motor;
    public DcMotorEx right_motor;
    //public PIDController movementPID;
    public double steering_power=0.0;
    public double drive_power=0.0;

    public ElapsedTime last_cycle;
    public double refresh_rate=60.0; // [Hz]
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
        left_motor=(DcMotorEx)hardwareMap.get(DcMotor.class,"left_motor");
        right_motor=(DcMotorEx)hardwareMap.get(DcMotor.class,"right_motor");

        // MOTOR DIRECTION SETTINGS
        left_motor.setDirection(DcMotorSimple.Direction.FORWARD);
        right_motor.setDirection(DcMotorSimple.Direction.REVERSE);
        refresh_rate=60.0;
        // Timer to detect refresh rate
        last_cycle=new ElapsedTime();

        //movementPID = PIDController(0.5, 0, 0, );
    }
    // Set default_heading to 0.0 if default_heading isn't given
    public Movement(VectorF default_position, HardwareMap hardwareMap) {this(default_position,0.0,hardwareMap);}
    public void RobotStart() {
        imu.resetYaw();
    } // Reset Yaw to 0
    public void UpdateRobot(Telemetry telemetry) {
        // TODO: Add position tracker
        // Limits Drive power to MAX_ACCELERATION
        // does not limit speed
		drive_power=drive_power+Clamp(movement_vector.get(1)-drive_power,Configuration.MAX_ACCELERATION/refresh_rate*-1.0,Configuration.MAX_ACCELERATION/refresh_rate);
        steering_power=turn_rate;

        double total_strength=abs(drive_power)+abs(steering_power);
        double steering_strength=steering_power*abs(steering_power)/total_strength;
        double drive_strength=drive_power*abs(drive_power)/total_strength;
        double left_power_motor=drive_strength+steering_strength;
        double right_power_motor=drive_strength-steering_strength;
        left_motor.setPower(left_power_motor);
        right_motor.setPower(right_power_motor);

        left_motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right_motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        telemetry.addData("Forward Speed",movement_vector.get(1));
        telemetry.addData("LeftMotor",left_power_motor);
        telemetry.addData("RightMotor",right_power_motor);

        refresh_rate=Clamp(1.0/last_cycle.seconds(),50.0,100.0);
        telemetry.addData("Refresh Rate",round(refresh_rate));
        last_cycle.reset();
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
    public void setBrakeOnOff(Boolean state) {
        int currentPosition = left_motor.getCurrentPosition();
        if (state) {
            left_motor.setPower(0);
            right_motor.setPower(0);
        }
    }
    public void setTurnSpeed(double target_turn_rate) {
        turn_rate=target_turn_rate;
    }
    public void holdHeading(double target_heading) {
        double INTERVAL=0.001;
        double current_heading=getHeading();
        double turn_diff=subtractAngles(current_heading,target_heading);
        setTurnSpeed(turn_diff*INTERVAL);
    }

    public void jog_position(boolean gamepadLeft, boolean gamepadRight, boolean gamepadForward, boolean gamepadBack){
        if(gamepadForward) {
            left_motor.setPower(0.6);
            right_motor.setPower(0.6);
        }
        if(gamepadLeft){
            right_motor.setPower(0.6);
            left_motor.setPower(-0.6);
        }
        if(gamepadRight) {
            left_motor.setPower(0.6);
            right_motor.setPower(-0.6);
        }
        if(gamepadBack) {
            left_motor.setPower(-0.6);
            right_motor.setPower(-0.6);
        }
    }
    /**
     * @param target_position Target global 2D position [m]
     */
    public void lookToward(VectorF target_position) {
        VectorF targetVector=target_position.subtracted(position);
        double angle2target=atan2(targetVector.get(0),targetVector.get(1));
        holdHeading(angle2target);
    }
    // Drive in target_vector direction (Target Vector is relative to the robots location)
    public void driveTo(VectorF target_vector,double speed,boolean turn_towards) {
        double heading=getHeading();
        double angle2target = atan2(target_vector.get(0), target_vector.get(1));
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
