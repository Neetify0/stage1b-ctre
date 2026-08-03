package first.robot.mechanisms;

import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.hardware.TalonFX;

import first.robot.simulation.SingleFlywheelSim;



public class Feeder extends Mechanism {
    
    private TalonFX motor = new TalonFX(5, CANBus.systemcore(0));
    private final SingleFlywheelSim sim = new SingleFlywheelSim(motor, "Feeder");

    public void periodic() {
        sim.periodic();
    }

    public Feeder() {setDefaultCommand(idle());}
    
    public Command feed() {
        return run(coroutine -> {
            while (true) {
                motor.setThrottle(0.75);
                coroutine.yield();
            }
        })
        .named("Set Throttle to 0.75");
    }

    public Command intake() {
        return run(coroutine -> {
            while (true) {
                motor.setThrottle(-1);
                coroutine.yield();
            }
        })
        .named("Set Throttle to -1");
    }

    public Command outtake() {
        return run(coroutine -> {
            while (true) {
                motor.setThrottle(1);
                coroutine.yield();
            }
        })
        .named("Set Throttle to 1");
    }

    public Command idle() {
        return run(coroutine -> {
            while (true) {
                motor.setThrottle(0);
                coroutine.yield();
            }
        })
        .named("Set Throttle to 0");
    }
}

