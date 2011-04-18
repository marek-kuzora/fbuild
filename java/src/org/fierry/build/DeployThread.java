package org.fierry.build;

import org.fierry.build.projects.CurrentProject;

public class DeployThread extends Thread {
	private static Boolean deploy = false;
	
	private CurrentProject current;
	
	public DeployThread(CurrentProject current) {
		this.current = current;
	}
	
	@Override public void run() {
		while(true) {
			try {
				synchronized (deploy) {
					System.out.println("Trying deploy!");
					while(deploy == false) { deploy.wait(); }
				}
			} catch(InterruptedException e) { throw new RuntimeException(e); }
			
			System.out.println("Triggered deploy!");
			current.deploy(false);
			
			synchronized (deploy) {
				deploy = false;
			}
		}
	}
	
	public static void triggerDeploy() {
		deploy = true;
		System.out.println("Triggering deploy");
		synchronized (deploy) {
			deploy.notifyAll();
		}
	}
}
