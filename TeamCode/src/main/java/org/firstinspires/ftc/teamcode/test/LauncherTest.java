package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot.Launcher;

@Disabled
@TeleOp
public class LauncherTest extends LinearOpMode {
    Launcher launcher;
    @Override
    public void runOpMode() {
        launcher=new Launcher(hardwareMap);
        // Wait until the play button is pressed
        while (opModeInInit()) {
            telemetry.addData("Status","Ready to Start");
            telemetry.update();
        }
        // Send to the robot movement controller Init has ended
        launcher.RobotStart();

        while (opModeIsActive()) {
            launcher.setRPS(gamepad1.right_stick_y*120.0);
            launcher.UpdateRobot();
            telemetry.addData("Motor Speed %",launcher.getPower());
            launcher.debugData(telemetry);
        }
    }
}