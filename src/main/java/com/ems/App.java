package com.ems;

import java.util.Scanner;

import com.ems.menu.MainMenu;

public class App 
{
    public static void main( String[] args )
    {
        	Scanner scanner = new Scanner(System.in);
    		System.out.println("Welcome to the Event management system");
    		new MainMenu(scanner);
    		scanner.close();
    }
}
