package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name = "AprilTag Servo Tracker", group = "Concept")
public class AprilTag extends LinearOpMode {

    // Vision variables
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    // Hardware variables
    private Servo topServo;
    private double servoPosition = 0.5; // Start centered (0.0 to 1.0)
    
    // Tracking Constants
    // The ID of the AprilTag we want to track. Change this depending on your need!
    private final int TARGET_TAG_ID = 1; 
    
    // Proportional Control constant for servo movement. 
    // You may need to tune this to prevent oscillation or make it move faster/slower.
    private final double kP = 0.005; 

    @Override
    public void runOpMode() {
        initAprilTag();

        // Initialize the servo (ensure it is named "topServo" in your driver station configuration)
        topServo = hardwareMap.get(Servo.class, "topServo");
        topServo.setPosition(servoPosition);

        telemetry.addData("Status", "Initialized. Waiting for start...");
        telemetry.update();

        // Wait for the driver to press START
        waitForStart();

        // Run until the driver presses STOP
        while (opModeIsActive()) {
            
            // Retrieve a list of all currently detected AprilTags
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            telemetry.addData("# Total AprilTags Detected", currentDetections.size());

            boolean targetFound = false;
            AprilTagDetection targetDetection = null;

            // Iterate through every detected tag to print ID and search for our target
            for (AprilTagDetection detection : currentDetections) {
                if (detection.metadata != null) {
                    // 1. Print out the AprilTag ID on the terminal
                    telemetry.addLine(String.format("Spotted ID %d (%s)", detection.id, detection.metadata.name));
                    
                    // Check if this is the tag we want to track
                    if (detection.id == TARGET_TAG_ID) {
                        targetFound = true;
                        targetDetection = detection;
                    }
                } else {
                    telemetry.addLine(String.format("Spotted Unknown Tag ID %d", detection.id));
                }
            }

            // 2. Turn to specific AprilTags using ONLY the top servo
            if (targetFound && targetDetection != null) {
                telemetry.addLine("\n--- TRACKING MODE ---");
                telemetry.addData("Tracking Target ID", TARGET_TAG_ID);
                
                // Bearing is the camera's angle to the tag (left/right). 
                double bearingError = targetDetection.ftcPose.bearing;
                telemetry.addData("Bearing Error (deg)", "%.2f", bearingError);

                // Update servo position. 
                // If the tag is to the Right (positive bearing), we adjust the position.
                // Depending on how your servo is mounted, you might need to change the minus to a plus.
                servoPosition = servoPosition - (bearingError * kP);

                // Constrain the servo position between 0.0 and 1.0 to prevent crashing the servo
                servoPosition = Math.max(0.0, Math.min(1.0, servoPosition));
                
                // Apply the new position to the servo
                topServo.setPosition(servoPosition);

            } else {
                telemetry.addLine("\n--- IDLE ---");
                telemetry.addData("Target Tag Not Visible", "Looking for ID %d...", TARGET_TAG_ID);
            }

            telemetry.addData("Current Servo Position", "%.3f", servoPosition);
            telemetry.update();
            
            // A short sleep helps save basic processing cycles
            sleep(20);
        }

        // Close the vision portal when we're completely done
        visionPortal.close();
    }

    private void initAprilTag() {
        // Create the AprilTag processor
        aprilTag = new AprilTagProcessor.Builder().build();

        // Create the vision portal
        // Make sure your camera configuration in the app is named "Webcam 1"
        VisionPortal.Builder builder = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag);
        
        visionPortal = builder.build();
    }
}
