package org.usfirst.frc.team2976.robot.subsystems;
import java.util.Timer;
import java.util.TimerTask;
//import org.usfirst.frc.team2976.robot.commands.readPotentiometer;

import edu.wpi.first.wpilibj.command.Command;

/**
 * PID Controller Object
 * <p> Starts a runnable class PIDCompute that executes the PID algorithm 
 * <p> Method Signature: public PIDMain(Object pidsource,int setpoint, int sampleTime, double kp, double ki, double kd)
 * <p> pidsource->Class that implements PIDSource, allows controller to get input
 * <p> setpoint->Target value of the controller.  
 * <p> sampleTime-> how often to recalculate center
 * <p> kp,ki,kd ->tuning parameter
 *  @author NeilHazra
 */ 

public class PIDMain {
	private boolean enabled = true;	//allows the pid algorithm to stop computing
	private Timer pidtimer; //PID runs concurrently, timer class executes the algorithm uniformly
	private int sampleTime; //how quickly to sample and calculate
	private double outputMax = 1; 
	private double outputMin = -1;
	private PIDSource pidsource;
	private double output; //value to send to the motor
	
	/** The process error */
	private double error; 
	/** The input of the PIDController */
	private double input;
	private double setpoint;
	
	private double prevInput;
	
	private double proportional; //P term
	private double integral; //I term
	private double derivative;// D term
	
	private double kp, ki, kd; //tuning parameters, the hardest part of PID
	
	//not good
	//public readPotentiometer readpot = new readPotentiometer();
	
	public PIDMain(Object pidsource,int setpoint, int sampleTime, double kp, double ki, double kd) {
		this.pidsource = (PIDSource) pidsource;
		this.setSampleTime(sampleTime);
		this.setpoint = setpoint;
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;
		pidtimer = new Timer();//create the timer
		pidtimer.scheduleAtFixedRate(new PIDCompute(), 0, sampleTime);// set the timer
	}
	public void isEnabled(boolean enabled)	{
		this.enabled = enabled;
	}
	public double getOutput()	{
		return output;
	}
	
	//For debug and tuning
	public double getInput() {
		return input;
	}
	public double getError() {
		return error;
	}
	public double getSetpoint() {
		return setpoint;
	}
	public void setSetpoint(double setpoint) {
		this.setpoint = setpoint;
	}
	public void setOutputLimits(double min, double max)	{
		outputMax = max;
		outputMin = min;
	}
	/*
	 * No code to make use of sample time changes yet
	 */
	public int getSampleTime() {
		return sampleTime;
	}
	public void setSampleTime(int sampleTime) {
		this.sampleTime = sampleTime;
	}
	/**
	 * PID Algorithm calculates in this TimerTask created by PIDMain
	 * @author NeilHazra
	 *
	 */	
	private class PIDCompute extends TimerTask	{
		public void run()	{  
			if(!enabled) return;	
			    
				input = pidsource.getInput();
				//input = readpot.getSteeringPot();
				error = input - setpoint; //Calculate Error
				proportional = kp *error;
				
				integral += ki *error;
					if(integral>outputMax)	integral = outputMax;
					if(integral<outputMin) integral = outputMin;
					
				derivative = kd*(input-prevInput);
				
				output = proportional + integral + derivative;
					if (output>outputMax) output = outputMax;
					if (output<outputMin) output = outputMin;
			prevInput = input;
			
		}				
	}
}