package org.firstinspires.ftc.teamcode;
// FTC SDK telemetry
import org.firstinspires.ftc.robotcore.external.Telemetry;

// FTC Dashboard
import org.firstinspires.ftc.teamcode.util.Configuration;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import static org.firstinspires.ftc.teamcode.util.Misc.Clamp;
import static java.lang.Math.abs;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.teamcode.robot.Movement;
import org.firstinspires.ftc.teamcode.robot.Sensors;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import java.util.Locale;

@TeleOp
public class ManualDrive extends LinearOpMode {
    //static double LAUNCHER_STICK_SENSITIVITY =8.0;
    // must be negative
    public Movement robotMovement;
    public Sensors robotSensors;
    public double launcher_throttle=0.0;
    public boolean rumble=false;
    public GoBildaPinpointDriver odo;
    @Override
    public void runOpMode() {
        robotMovement = new Movement(
                new VectorF(0.0f, 0.0f, 0.0f), // Set default position to 0,0,0
                hardwareMap); // Pass the ability to interact with hardware
        robotSensors = new Sensors(
                hardwareMap);
        FtcDashboard dashboard = FtcDashboard.getInstance();
        Telemetry telemetry = new MultipleTelemetry(
                    this.telemetry,
                    dashboard.getTelemetry()
        );
        odo = hardwareMap.get(GoBildaPinpointDriver.class,"odo");
        
        
        odo.setOffsets(-110.0, 120.0, DistanceUnit.MM);
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        
        odo.resetPosAndIMU();

        telemetry.addData("Status", "Initialized");
        telemetry.addData("X offset", odo.getXOffset(DistanceUnit.MM));
        telemetry.addData("Y offset", odo.getYOffset(DistanceUnit.MM));
        telemetry.addData("Device Version Number:", odo.getDeviceVersion());
        telemetry.addData("Heading Scalar", odo.getYawScalar());
        telemetry.update();

        while (opModeInInit()) {
		}
        robotMovement.RobotStart();
        if(opModeIsActive()){

        }
        while (opModeIsActive()) {
            


            // position and heading
            Pose2D pos = odo.getPosition();
            String data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", pos.getX(DistanceUnit.MM), pos.getY(DistanceUnit.MM), pos.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Position", data);

            
            // turns robot based on stick input
            robotMovement.setTurnSpeed(gamepad1.left_stick_x*Configuration.TURN_MULTIPLIER);	

            robotMovement.movement_vector.put(0, gamepad1.left_stick_x);
            robotMovement.movement_vector.put(1, gamepad1.left_stick_y);

            // Update Movement
            robotMovement.UpdateRobot(telemetry);
            // This code is deprecated
        // END OF MAINLOOP
        }
    }
}
