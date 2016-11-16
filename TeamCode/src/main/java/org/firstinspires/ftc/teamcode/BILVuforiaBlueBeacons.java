package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by mikab_000 on 11/12/2016.
 */
@Autonomous(name="BIL: Blue Beacons", group="BIL")
public class BILVuforiaBlueBeacons extends LinearOpMode {

    VuforiaLocalizer vuforia;
    BILVuforiaCommon helper = new BILVuforiaCommon();
    BILRobotHardware robot = new BILRobotHardware();

    @Override public void runOpMode() throws InterruptedException{
        this.vuforia = helper.initVuforia(false, 4);
        VuforiaTrackables targets = helper.loadTargets("FTC_2016-17", "Wheels", "Tools", "Legos", "Gears");

        robot.init(hardwareMap);

        waitForStart();

        targets.activate(); //activate the tracking of the image targets once the opmode starts

        while(opModeIsActive()) { //when the op mode is active

            VuforiaTrackable beaconImage = targets.get(0);
            OpenGLMatrix position = ((VuforiaTrackableDefaultListener) beaconImage.getListener()).getPose(); //get positions

            if(position != null) { //if we see the object we are looking for
                VectorF translation = position.getTranslation();
                double xTrans = (double)translation.get(1); //x and y are switched for horizontal phone
                double yTrans = (double)translation.get(0);
                double zTrans = (double)translation.get(2);

                double degreesToTurn = Math.toDegrees(Math.atan2(zTrans, xTrans)) + 90; //vertical phone

                telemetry.addData(beaconImage.getName() + " - Translation", translation);
                telemetry.addData(beaconImage.getName() + " - Degrees", degreesToTurn);

                if(Math.abs(zTrans) > 500) {
                    double leftSpeed = (40 + (degreesToTurn/4))/100;
                    double rightSpeed = (40 - (degreesToTurn/4))/100;
                    robot.setDriveMotors(leftSpeed, leftSpeed, rightSpeed, rightSpeed);
                } else {
                    robot.setAllDriveMotors(0);
                }

            } else {
                telemetry.addData(beaconImage.getName(), "Not In View"); // if not in view it will print "Not in view"
                robot.setAllDriveMotors(30);
            }
            telemetry.update();
        }
    }
}