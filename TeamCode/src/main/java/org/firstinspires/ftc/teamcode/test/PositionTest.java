package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.teamcode.robot.Movement;
import org.firstinspires.ftc.teamcode.robot.Sensors;

@Disabled
@TeleOp
public class PositionTest extends LinearOpMode {

    public Movement robotMovement;
    public Sensors robotSensors;

    @Override
    public void runOpMode() {
        robotMovement = new Movement(
                new VectorF(0.0f, 0.0f, 0.0f), // Set default position to 0,0,0
                hardwareMap); // Pass the ability to interact with hardware
        robotSensors = new Sensors(
                hardwareMap);
        while (opModeInInit()) {
            telemetry.addData("Status", "Ready to Start");
            telemetry.update();
        }

        robotMovement.RobotStart();
        while (opModeIsActive()) {
            robotMovement.setTurnSpeed(gamepad1.left_stick_x); // 5 degrees/second
            robotMovement.movement_vector.put(1, gamepad1.left_stick_y);

            // Update Robot
            robotMovement.UpdateRobot(telemetry);
        }
    }
}