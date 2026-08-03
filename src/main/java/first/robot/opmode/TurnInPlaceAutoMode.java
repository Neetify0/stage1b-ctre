package first.robot.opmode;

import first.robot.Robot;

import org.wpilib.command3.Scheduler;
import org.wpilib.opmode.PeriodicOpMode;

public class TurnInPlaceAutoMode extends PeriodicOpMode {
    private final Robot robot;

    public TurnInPlaceAutoMode(Robot robot) {
        this.robot = robot;
    }

    @Override
    public void start() {
        Scheduler.getDefault().schedule(robot.drivetrain.rotateInPlace(90, () -> 0.2));
    }
}
