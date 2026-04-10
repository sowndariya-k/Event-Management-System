/*
 * Author : Mythily
 * RegistrationResult is a model class used as a response
 * object after an event registration attempt, carrying
 * success status, result message, and the generated
 * registration ID.
 */
package com.ems.model;

public class RegistrationResult {
    private boolean success;
    private String message;
    private int registrationId;
    private double finalAmount;
    
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(int registrationId) {
		this.registrationId = registrationId;
	}
	public double getFinalAmount() {
		return finalAmount;
	}
	public void setFinalAmount(double finalAmount) {
		this.finalAmount = finalAmount;
	}
    
    
}