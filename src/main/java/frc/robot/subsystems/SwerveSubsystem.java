// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.studica.frc.AHRS;
import com.studica.frc.AHRS.NavXComType;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;

//Making the SwerveModules with all the different paramters

public class SwerveSubsystem extends SubsystemBase {
    public final SwerveModule frontLeft = new SwerveModule(
            DriveConstants.kFrontLeftDriveMotorPort,
            DriveConstants.kFrontLeftTurningMotorPort,
            DriveConstants.kFrontLeftDriveMotorReversed,
            DriveConstants.kFrontLeftTurningMotorReversed,
            DriveConstants.kFrontLeftTurnAbsoluteEncoderPort);

    public final SwerveModule frontRight = new SwerveModule(
            DriveConstants.kFrontRightDriveMotorPort,
            DriveConstants.kFrontRightTurningMotorPort,
            DriveConstants.kFrontRightDriveMotorReversed,
            DriveConstants.kFrontRightTurningMotorReversed,
            DriveConstants.kFrontRightTurnAbsoluteEncoderPort);

    public final SwerveModule backLeft = new SwerveModule(
            DriveConstants.kBackLeftDriveMotorPort,
            DriveConstants.kBackLeftTurningMotorPort,
            DriveConstants.kBackLeftDriveMotorReversed,
            DriveConstants.kBackLeftTurningMotorReversed,
            DriveConstants.kBackLeftTurnAbsoluteEncoderPort);

    public final SwerveModule backRight = new SwerveModule(
            DriveConstants.kBackRightDriveMotorPort,
            DriveConstants.kBackRightTurningMotorPort,
            DriveConstants.kBackRightDriveMotorReversed,
            DriveConstants.kBackRightTurningMotorReversed,
            DriveConstants.kBackRightTurnAbsoluteEncoderPort);

    //Old way of constructing //public final AHRS gyro = new AHRS(SPI.Port.kMXP); //Defineing the Gyro
    public final AHRS gyro = new AHRS(NavXComType.kMXP_SPI);

    //THIS IS JUST FOR TRAJECTORIES
    private final SwerveDriveOdometry odometer = new SwerveDriveOdometry(DriveConstants.kDriveKinematics,
    new Rotation2d(0),  new SwerveModulePosition[] {
      frontLeft.getPosition(),
      frontRight.getPosition(),
      backLeft.getPosition(),
      backRight.getPosition()
    });

    
    public final Field2d m_field = new Field2d(); //For Glass

    public SwerveSubsystem() {   
      new Thread(() -> {
        try {
            Thread.sleep(1000);
            zeroHeading();
        } catch (Exception e) {
        }
    }).start();

}


  public void zeroHeading() {
      gyro.reset(); //Making all values 0 (Pitch, Yaw, Roll)
  }
  
  public double getHeading() 
  {
    return Math.IEEEremainder(-gyro.getAngle(), 360); //Get the position the swerve is facing
  }
    
  public Rotation2d getRotation2d()
  {
    return Rotation2d.fromDegrees(getHeading()); //For FRC functions, just converts where your facing to a "Rotation2d" type
  }
  
  //FOR TRAJECTORIES
  public Pose2d getPose() {
    return odometer.getPoseMeters();
  } 

  //FOR TRAJECTORIES
  public void resetOdometry(Pose2d pose) {
    odometer.resetPosition(getRotation2d(), new SwerveModulePosition[] {
      frontLeft.getPosition(),
      frontRight.getPosition(),
      backLeft.getPosition(),
      backRight.getPosition()
     } ,pose);
  }

  public void stopModules() 
  {
    frontLeft.stop();
    frontRight.stop();
    backLeft.stop();
    backRight.stop();
  }

  public void setModuleStates(SwerveModuleState[] desiredStates) 
  {
    //Not sure tbh, just FRC stuff -- read WPILIB Documentation
    frontLeft.setDesiredState(desiredStates[0]);
    frontRight.setDesiredState(desiredStates[1]); 
    backLeft.setDesiredState(desiredStates[2]);
    backRight.setDesiredState(desiredStates[3]);
  }


  @Override

  public void periodic() {

    //ALL OF THIS HAPPENS 5 TIMES A SECOND
    //Just stuff for debugging and for Driver!

      odometer.update(getRotation2d(), new SwerveModulePosition[] {
      frontLeft.getPosition(),
      frontRight.getPosition(),
      backLeft.getPosition(),
      backRight.getPosition()});

    SmartDashboard.putString("Robot Location", getPose().toString());
    SmartDashboard.putBoolean("AutoBalance?", gyro.getPitch() <= 1);
    SmartDashboard.putData(m_field);
    SmartDashboard.putNumber("Pitch Of Robot", gyro.getPitch());
    
    m_field.setRobotPose(odometer.getPoseMeters());
  }

}

