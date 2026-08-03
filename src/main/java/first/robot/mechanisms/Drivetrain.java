package first.robot.mechanisms;

import java.util.function.DoubleSupplier;

import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
import org.wpilib.drive.DifferentialDrive;
import org.wpilib.hardware.imu.OnboardIMU;
import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.controls.Follower;

import first.robot.simulation.DrivetrainSim;

public class Drivetrain extends Mechanism {
    private static final int leftLeaderID = 0, rightLeaderID = 2;
    private final TalonFX leftLeader = new TalonFX(
        leftLeaderID, CANBus.systemcore(0)),
        leftFollower = new TalonFX(1, CANBus.systemcore(0)),
        rightLeader = new TalonFX(rightLeaderID, CANBus.systemcore(0)),
        rightFollower = new TalonFX(3, CANBus.systemcore(0));
    
    private final OnboardIMU imu = new OnboardIMU(MountOrientation.FLAT);
    private final DifferentialDrive differentialDrive = 
        new DifferentialDrive(leftLeader::setThrottle, rightLeader::setThrottle);

    private final DrivetrainSim drivetrainSim = new DrivetrainSim(leftLeader, rightLeader);

    public void periodic() {
        drivetrainSim.periodic();
    }
    
    public Drivetrain() {
        setDefaultCommand(idle());
        var leftConfig = new TalonFXConfiguration();
        leftConfig.MotorOutput.withInverted(InvertedValue.Clockwise_Positive);
        leftLeader.getConfigurator().apply(leftConfig);
        leftFollower.getConfigurator().apply(leftConfig);

        leftFollower.setControl(new Follower(leftLeaderID, MotorAlignmentValue.Aligned));

        var rightConfig = new TalonFXConfiguration();
        rightConfig.MotorOutput.withInverted(InvertedValue.CounterClockwise_Positive);
        rightLeader.getConfigurator().apply(rightConfig);
        rightLeader.getConfigurator().apply(rightConfig);
        rightFollower.getConfigurator().apply(rightConfig);

        rightFollower.setControl(new Follower(rightLeaderID, MotorAlignmentValue.Aligned));
    }

    public Command idle() {
        return run(coroutine -> {
            while (true) {
                differentialDrive.arcadeDrive(0.0, 0.0);
                coroutine.yield();
            }
        })
        .named("Set speed and rotation to 0.0");
    }

    public Command arcadeDrive(DoubleSupplier forwardThrottle, DoubleSupplier rotationThrottle) {
        return run(coroutine -> {
            while (true) {
                differentialDrive.arcadeDrive(forwardThrottle.getAsDouble(), rotationThrottle.getAsDouble());
                coroutine.yield();
            }
        })
        .named("Set speed and rotation");
    }

    public Command rotateInPlace(double angleDegrees, DoubleSupplier rotationThrottle) {
        return run(coroutine -> {
            double targetAngle = imu.getRotation2d().getDegrees() + angleDegrees;
            while (true) {
                while (imu.getRotation2d().getDegrees() < targetAngle) {
                    differentialDrive.arcadeDrive(0.0, rotationThrottle.getAsDouble());
                }
            }
        })
        .named("Rotate at throttle until reaching target angle");
    }
}
