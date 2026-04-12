package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name="gamepaddrive", group="Linear Opmode")
public class gamepaddrive extends LinearOpMode {

    // Declare OpMode members
    private DcMotor frontLeft = null;
    private DcMotor backLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backRight = null;

    @Override
    public void runOpMode() {
        // Initialize the hardware variables.
        // Make sure your robot configuration in the FTC Driver Station app matches these names exactly.
        frontLeft  = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft   = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight  = hardwareMap.get(DcMotor.class, "backRight");

        // Reverse the left side motors so that forward power corresponds to forward movement.
        // Note: You may need to adjust these depending on your exact motor and gear setup.
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized. Ready to start.");
        telemetry.update();

        // Wait for the driver to press PLAY
        waitForStart();

        // Run until the driver presses STOP
        while (opModeIsActive()) {

            // Gamepad inputs
            // Note: The vertical axis is naturally negative when pointing forward, so we invert it.
            double y = -gamepad1.left_stick_y; 
            
            // The horizontal axis is positive when pushed right.
            double x = gamepad1.left_stick_x; 
            
            // Multiplying strafing by 1.1 helps offset imperfect strafing caused by wheel slip or friction
            x = x * 1.1;  
            
            // The right stick controls rotation
            double rx = gamepad1.right_stick_x;

            // Denominator is the largest motor power (absolute value) or 1.
            // This ensures all powers maintain the same ratio when at least one is greater than 1.0.
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            
            // Omni/Mecanum Kinematics calculate the required power for each wheel
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;

            // Apply calculated power to the motors
            frontLeft.setPower(frontLeftPower);
            backLeft.setPower(backLeftPower);
            frontRight.setPower(frontRightPower);
            backRight.setPower(backRightPower);

            // Output telemetry to the Driver Station
            telemetry.addData("Status", "Running");
            telemetry.addData("Front L/R Power", "%.2f / %.2f", frontLeftPower, frontRightPower);
            telemetry.addData("Back  L/R Power", "%.2f / %.2f", backLeftPower, backRightPower);
            telemetry.update();
        }
    }
}
