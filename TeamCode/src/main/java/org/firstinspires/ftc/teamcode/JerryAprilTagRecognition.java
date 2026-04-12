package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name = "Jerry AprilTag Recognition", group = "Concept")
public class JerryAprilTagRecognition extends LinearOpMode {

    // The variable to store our instance of the AprilTag processor.
    private AprilTagProcessor aprilTag;

    // The variable to store our instance of the vision portal.
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() {
        initAprilTag();

        telemetry.addData("Status", "Initialized. Waiting for start...");
        telemetry.update();

        // Wait for the driver to press START
        waitForStart();

        // Run until the driver presses STOP
        while (opModeIsActive()) {
            
            // Retrieve a list of all currently detected AprilTags
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            telemetry.addData("# AprilTags Detected", currentDetections.size());

            // Iterate through every detected tag and print its info
            for (AprilTagDetection detection : currentDetections) {
                if (detection.metadata != null) {
                    telemetry.addLine(String.format("\n==== (ID %d) %s ====", detection.id, detection.metadata.name));
                    telemetry.addLine(String.format("XYZ (inch): %6.1f, %6.1f, %6.1f", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                    telemetry.addLine(String.format("PRY (deg):  %6.1f, %6.1f, %6.1f", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                    telemetry.addLine(String.format("RBE (inch, deg): %6.1f, %6.1f, %6.1f", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
                } else {
                    // This handles tags that are not in the current game database (unknown tags)
                    telemetry.addLine(String.format("\n==== (ID %d) Unknown Tag ====", detection.id));
                    telemetry.addLine(String.format("Center (pixels): %6.0f, %6.0f", detection.center.x, detection.center.y));
                }
            }

            telemetry.update();
            
            // A short sleep helps save basic processing cycles
            sleep(20);
        }

        // Close the vision portal when we're completely done to save system resources
        visionPortal.close();
    }

    /**
     * Initialize the AprilTag processor and Vision Portal.
     */
    private void initAprilTag() {
        // Create the AprilTag processor using the builder.
        aprilTag = new AprilTagProcessor.Builder()
                // Settings can be customized here (commented out by default to use standard config)
                //.setDrawAxes(true)
                //.setDrawCubeProjection(true)
                //.setDrawTagOutline(true)
                .build();

        // Create the vision portal by using a builder.
        // Make sure your camera configuration in the app is named "Webcam 1"
        VisionPortal.Builder builder = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag);
        
        visionPortal = builder.build();
    }
}
