package first.robot.mechanisms;

import static org.wpilib.units.Units.Seconds;

import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.hardware.TalonFX;

import first.robot.simulation.SingleFlywheelSim;

public class IntakeLauncher extends Mechanism {
    public IntakeLauncher() {setDefaultCommand(idle());}
    private TalonFX motor = new TalonFX(4, CANBus.systemcore(0));
    private final SingleFlywheelSim sim = new SingleFlywheelSim(motor, "IntakeLauncher");

    public void periodic() {
        sim.periodic();
    }

    public Command shoot() {
        return run(coroutine -> {
            coroutine.wait(Seconds.of(5));
            while (true) {
                motor.setThrottle(0.9);
                coroutine.yield();
            }
        })
        .named("Wait 5 Seconds Set Throttle to 0.9");
    }

    public Command intake() {
        return run(coroutine -> {
            while (true) {
                motor.setThrottle(0.8);
                coroutine.yield();
            }
        })
        .named("Set Throttle to 0.8 Forever");
    }

    public Command outtake() {
        return run(coroutine -> {
            while (true) {
                motor.setThrottle(-0.8);
                coroutine.yield();
            }
        })
        .named("Set Throttle to -0.8 Forever");
    }

    public Command idle() {
        return run(coroutine -> {
            while (true) {
                motor.setThrottle(0);
                coroutine.yield();
            }
        })
        .named("Set Throttle to 0 Forever");
    }
}
