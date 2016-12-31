package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import java.util.List;
import java.util.Vector;

/**
 * Created by mikab_000 on 11/12/2016.
 */
@Autonomous(name="BIL: Red Beacons", group="BIL")
public class BILVuforiaRedBeacons extends LinearOpMode {

    VuforiaLocalizer vuforia;
    BILVuforiaCommon helper = new BILVuforiaCommon();
    BILRobotHardware robot = new BILRobotHardware();
    ElapsedTime time = new ElapsedTime();

    @Override public void runOpMode() throws InterruptedException{
        this.vuforia = helper.initVuforia(false, 4);
        VuforiaTrackables targets = helper.loadTargets("FTC_2016-17", "Wheels", "Tools", "Legos", "Gears");

        robot.init(hardwareMap);

        robot.lightSensor.enableLed(true);

        Thread.sleep(50);

        double darkFloorValue = robot.lightSensor.getLightDetected();
        double sideSpeed = 0.25;

        while(!isStarted()) {
            darkFloorValue = (darkFloorValue + robot.lightSensor.getLightDetected())/2;

            //continuously calibrate gyro to keep the heading as accurate as possible
            if(!robot.gyroSensor.isCalibrating()){
                robot.gyroSensor.calibrate();
            }

            idle();
        }

        //wait for the gyro to finish calibrating if it is
        while(robot.gyroSensor.isCalibrating()){
            idle();
        }

        waitForStart();


        robot.setAllDriveMotors(0.5);
        robot.driveUntilLineOrDistance(0.5, 6, darkFloorValue);
        //turn 45 degrees the first 40 degrees at 0.5 speed, and to not overshoot the last 5 degrees would be 0.1 speed
        robot.turnDegrees(0.5, -40);
        robot.turnDegrees(0.1, -5);

        targets.activate(); //activate the tracking of the image targets once the opmode starts

        List<VuforiaTrackable> redTrackablesList = helper.returnRedTargets(targets);

        boolean imageSeen = false;
        VuforiaTrackable toolsTarget = redTrackablesList.get(0);
        VuforiaTrackable gearsTarget = redTrackablesList.get(1);

        if(robot.lightSensor.getLightDetected() < darkFloorValue + robot.lineColorThreshold) {
            robot.setDriveMotors(sideSpeed, -sideSpeed, -sideSpeed, sideSpeed);
            time.reset();
            while(robot.lightSensor.getLightDetected() < darkFloorValue + robot.lineColorThreshold && time.milliseconds() < 500) {
                idle();
            }
            if(time.milliseconds() < 500){
                robot.setAllDriveMotors(0);
            } else {
                robot.setDriveMotors(-sideSpeed, sideSpeed, sideSpeed, -sideSpeed);
                time.reset();
                while(robot.lightSensor.getLightDetected() < darkFloorValue + robot.lineColorThreshold && time.milliseconds() < 1000) {
                    idle();
                }
            }
            robot.setAllDriveMotors(0);
        }

        while(!imageSeen){
            VectorF translation = helper.getTargetTranslation(gearsTarget);
            if(translation != null && translation.get(2) > 20) {
                helper.driveToTarget(gearsTarget, robot);
            } else {
                imageSeen = true;
            }
            idle();
        }

        if(robot.colorSensor.red() >= helper.redBeaconColor){ //left side red
            robot.pusher.setPosition(robot.pusherLeft);
        } else if(robot.colorSensor.blue() >= helper.blueBeaconColor) { //right side is red
            robot.pusher.setPosition(robot.pusherRight);
        }
        Thread.sleep(500);
        robot.pusher.setPosition(robot.pusherMiddle);

        robot.driveByTime(-1, 250);
        //turn 45 degrees the first 40 degrees at 0.5 speed, and to not overshoot the last 5 degrees would be 0.1 speed
        robot.turnDegrees(0.5, 85);
        robot.turnDegrees(0.1, 5);

        robot.setAllDriveMotors(0.5);
        robot.driveUntilLineOrDistance(0.5, 8, darkFloorValue);
        robot.driveDistance(0.5, 0.5); //to top it off

        robot.setAllDriveMotors(0);
        //turn 45 degrees the first 40 degrees at 0.5 speed, and to not overshoot the last 5 degrees would be 0.1 speed
        robot.turnDegrees(0.5, -85);
        robot.turnDegrees(0.1, -5);

        imageSeen = false;
        //push the button
        if(robot.lightSensor.getLightDetected() < darkFloorValue + robot.lineColorThreshold) {
            robot.setDriveMotors(sideSpeed, -sideSpeed, -sideSpeed, sideSpeed);
            time.reset();
            while(robot.lightSensor.getLightDetected() < darkFloorValue + robot.lineColorThreshold && time.milliseconds() < 500) {
                idle();
            }
            if(time.milliseconds() < 500){
                robot.setAllDriveMotors(0);
            } else {
                robot.setDriveMotors(-sideSpeed, sideSpeed, sideSpeed, -sideSpeed);
                time.reset();
                while(robot.lightSensor.getLightDetected() < darkFloorValue + robot.lineColorThreshold && time.milliseconds() < 1000) {
                    idle();
                }
            }
            robot.setAllDriveMotors(0);
        }

        while(!imageSeen){
            VectorF translation = helper.getTargetTranslation(toolsTarget);
            if(translation != null && translation.get(2) > 20) {
                helper.driveToTarget(toolsTarget, robot);
            } else {
                imageSeen = true;
            }
            idle();
        }

        if(robot.colorSensor.red() >= helper.redBeaconColor){ //left side red
            robot.pusher.setPosition(robot.pusherLeft);
        } else if(robot.colorSensor.blue() >= helper.blueBeaconColor) { //right side is red
            robot.pusher.setPosition(robot.pusherRight);
        }
        Thread.sleep(500);
        robot.pusher.setPosition(robot.pusherMiddle);

        robot.driveDistance(-1, 250);
    }
}
