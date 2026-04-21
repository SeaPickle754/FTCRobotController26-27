package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
@Disabled
@TeleOp
public class TestMotor extends LinearOpMode {
    public DcMotor motor;
    @Override
    public void runOpMode() {
        motor=hardwareMap.get(DcMotor.class,"test_motor");
        while (opModeInInit()) {
            telemetry.addData("Status","Ready to Start");
            telemetry.update();
        }
        while (opModeIsActive()) {
            motor.setPower(gamepad1.right_stick_y);
        }
    }
}
