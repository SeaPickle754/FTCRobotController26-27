package org.firstinspires.ftc.teamcode.robot;

public class PIDController {
    private double Kp; // Proportional gain
    private double Ki; // Integral gain
    private double Kd; // Derivative gain

    private double setpoint; // Desired target value
    private double previousError; // Error from the previous iteration
    private double integralSum; // Accumulation of errors over time

    private double minOutput; // Minimum allowed output
    private double maxOutput; // Maximum allowed output

    private long lastUpdateTime; // Timestamp of the last update

    public PIDController(double Kp, double Ki, double Kd, double minOutput, double maxOutput) {
        this.Kp = Kp;
        this.Ki = Ki;
        this.Kd = Kd;
        this.minOutput = minOutput;
        this.maxOutput = maxOutput;
        this.lastUpdateTime = System.nanoTime();
    }

    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
    }

    public double calculate(double currentValue) {
        long currentTime = System.nanoTime();
        double deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0; // Convert nanoseconds to seconds
        lastUpdateTime = currentTime;

        double error = setpoint - currentValue;

        // Proportional term
        double proportionalTerm = Kp * error;

        // Integral term
        integralSum += error * deltaTime;
        // Optional: Implement integral windup prevention (e.g., limit integralSum)
        // if (integralSum > maxIntegral) integralSum = maxIntegral;
        // else if (integralSum < minIntegral) integralSum = minIntegral;
        double integralTerm = Ki * integralSum;

        // Derivative term
        double derivativeTerm = Kd * (error - previousError) / deltaTime;
        previousError = error;

        // Combine terms to get the raw output
        double output = proportionalTerm + integralTerm + derivativeTerm;

        // Clamp the output within defined limits
        if (output > maxOutput) {
            output = maxOutput;
        } else if (output < minOutput) {
            output = minOutput;
        }

        return output;
    }

    public void reset() {
        integralSum = 0;
        previousError = 0;
        lastUpdateTime = System.nanoTime();
    }
}