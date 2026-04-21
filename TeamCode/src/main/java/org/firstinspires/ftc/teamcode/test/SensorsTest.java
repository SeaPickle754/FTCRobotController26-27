package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot.Sensors;
@Disabled
@TeleOp
public class SensorsTest extends LinearOpMode {
    public Sensors robotSensors;
    @Override
    public void runOpMode() {
        robotSensors = new Sensors(
                hardwareMap);
        robotSensors.startStream();
        while (opModeInInit()) {
            telemetry.addData("Status","Ready to Start");
            telemetry.update();
        }
        while (opModeIsActive()) {
            robotSensors.detectTags();
            robotSensors.telemetryOutput(telemetry);
            telemetry.update();
            sleep(20);
        }
        robotSensors.visionPortal.close();
    }
}
