package com.ems;

import java.util.Scanner;

import com.ems.menu.MainMenu;

/*
 * Application entry point.
 *
 * Responsibilities:
 * - Bootstraps the console-based application
 * - Initializes the main menu flow
 * - Ensures shared resources are closed on exit
 */
public class App 
{
    public static void main( String[] args ) {
    	Scanner scanner = new Scanner(System.in);
		System.out.println("Welcome to the Event management system");
		MainMenu mainMenu = new MainMenu(scanner);
		scanner.close();
    }
}
