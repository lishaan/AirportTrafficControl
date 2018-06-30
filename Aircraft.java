public class Aircraft {
	private static int ID_INC = 1;
	private static final String STATUS_FLYING = "Flying";
	private static final String STATUS_LANDED = "Landed";
	private static final String STATUS_PARKED = "Parked";
	private static final String STATUS_RUNWAY = "Runway";

	private final int ID;
	private final String name;
	private final String destination;

	private int stage;
	
	private volatile String status; // Flying -> Runway -> Parked -> Landed -> Runway
	private volatile String prevStatus;

	private long releaseTime = -1;

	public Aircraft() {
		this("Null", "Null", false);
	}

	public Aircraft(String name, String destination, boolean addLog) {
		this.name = name;
		this.destination = destination;

		status = (Util.getRandomInt(0, 100) > 50) ? STATUS_FLYING : STATUS_LANDED;
		prevStatus = null;

		if (addLog) {
			this.ID = Aircraft.ID_INC++;
			stage = 1;

			if (isLanded()) {
				Util.addLog(String.format("[ID: %d] (1/5) %s created and is waiting for depature to %s", ID, name, destination), ID);
			} else if (isFlying()) {
				Util.addLog(String.format("[ID: %d] (1/5) %s created and is arriving soon from %s", ID, name, destination), ID);
			}
		} else {
			this.ID = 0;
		}
	}

	public synchronized void switchStatus() {
		if (status.equals(STATUS_FLYING)) {
			status = STATUS_LANDED;
		} else {
			status = STATUS_FLYING;
		}
	}

	public synchronized void setAtRunway() {
		prevStatus = status;
		status = STATUS_RUNWAY;
	}

	public synchronized void unsetAtRunway() {
		if (prevStatus == null) {
			prevStatus = status;
		} else {
			status = prevStatus;
		}
	}

	public synchronized void park(long parkTime) {
		setParked();
		releaseTime = System.currentTimeMillis() + parkTime;
	}

	public synchronized void checkParking() {
		if (releaseTime != -1) {
			if (System.currentTimeMillis() >= releaseTime) {
				unsetParked();
				releaseTime = -1;
			}
		} 
	}

	public synchronized void setParked() {
		prevStatus = status;
		status = STATUS_PARKED;
	}

	public synchronized void unsetParked() {
		if (prevStatus == null) {
			prevStatus = status;
		} else {
			status = prevStatus;
		}
	}

	public @Override String toString() {
		return String.format("%s [ID: %d]", getName(), getID());
	}

	public synchronized void setStage(int newStage) { stage = newStage; } 

	public synchronized boolean isFlying() { return status.equals(STATUS_FLYING); }
	public synchronized boolean isLanded() { return status.equals(STATUS_LANDED); }
	public synchronized boolean isRunway() { return status.equals(STATUS_RUNWAY); }
	public synchronized boolean isParked() { return status.equals(STATUS_PARKED); }

	public synchronized int getID() { return ID; }
	public synchronized int getStage() { return stage; }
	public synchronized String getName() { return name; }
	public synchronized String getStatus() { return status; }
	public synchronized String getDestination() { return destination; }
}