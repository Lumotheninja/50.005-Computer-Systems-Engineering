import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

// package Week3;

public class Banker {
	private int numberOfCustomers;	// the number of customers
	private int numberOfResources;	// the number of resources

	private int[] available; 	// the available amount of each resource
	private int[][] maximum; 	// the maximum demand of each customer
	private int[][] allocation;	// the amount currently allocated
	private int[][] need;		// the remaining needs of each customer

	/**
	 * Constructor for the Banker class.
	 * @param resources          An array of the available count for each resource.
	 * @param numberOfCustomers  The number of customers.
	 */
	public Banker (int[] resources, int numberOfCustomers) {
		// TODO: set the number of resources
		this.numberOfResources = resources.length;
		// TODO: set the number of customers
		this.numberOfCustomers = numberOfCustomers;
		// TODO: set the value of bank resources to available
		this.available = resources;
		// TODO: set the array size for maximum, allocation, and need
		this.maximum = new int[numberOfCustomers][numberOfResources];
		this.allocation = new int[numberOfCustomers][numberOfResources];
		this.need = new int[numberOfCustomers][numberOfResources];
	}

	/**
	 * Sets the maximum number of demand of each resource for a customer.
	 * @param customerIndex  The customer's index (0-indexed).
	 * @param maximumDemand  An array of the maximum demanded count for each resource.
	 */
	public void setMaximumDemand(int customerIndex, int[] maximumDemand) {
		// TODO: add customer, update maximum and need
		this.maximum[customerIndex] = maximumDemand.clone();
		this.need[customerIndex] = maximumDemand.clone();
	}

	/**
	 * Prints the current state of the bank.
	 */
	public void printState() {
		System.out.println("Current state:\nAvailable:");
		// TODO: print available
		System.out.println(Arrays.toString(this.available));
		// TODO: print maximum
		System.out.println("Maximum:");
		for (int[] arrays : this.maximum){
			System.out.println(Arrays.toString(arrays));
		}
		// TODO: print allocation
		System.out.println("Allocation:");
		for (int[] arrays : this.allocation){
			System.out.println(Arrays.toString(arrays));
		}
		// TODO: print need
		System.out.println("Need:");
		for (int[] arrays : this.need){
			System.out.println(Arrays.toString(arrays));
		}
	}

	/**
	 * Requests resources for a customer loan.
	 * If the request leave the bank in a safe state, it is carried out.
	 * @param customerIndex  The customer's index (0-indexed).
	 * @param request        An array of the requested count for each resource.
	 * @return true if the requested resources can be loaned, else false.
	 */
	public synchronized boolean requestResources(int customerIndex, int[] request) {
		// TODO: print the request
		System.out.println("Customer " + customerIndex + "\nrequesting" + Arrays.toString(request));
		// TODO: check if request larger than need
		for (int x = 0; x < this.numberOfResources; x++){
			if (this.need[customerIndex][x] < request[x]){
				return false;
			}
			else if (this.available[x] < request[x]){
				return false;
			}
		}
		// TODO: check if request larger than available
		
		// TODO: check if the state is safe or not
		
		// TODO: if it is safe, allocate the resources to customer customerNumber
		if (checkSafe(customerIndex,request)){
			for (int x = 0; x < this.numberOfResources; x++){
				this.available[x] -= request[x];
				this.allocation[customerIndex][x] += request[x];
				this.need[customerIndex][x] -= request[x];
			}
			return true;
		}
		return false;
	}

	/**
	 * Releases resources borrowed by a customer. Assume release is valid for simplicity.
	 * @param customerIndex  The customer's index (0-indexed).
	 * @param release        An array of the release count for each resource.
	 */
	public synchronized void releaseResources(int customerIndex, int[] release) {
		// TODO: print the release
		System.out.println("Customer " + customerIndex + "\nreleasing " + Arrays.toString(release));
		// TODO: release the resources from customer customerNumber
		for (int x = 0; x < this.numberOfResources; x++){
			this.available[x] += release[x];
			this.allocation[customerIndex][x] -= release[x];
			this.need[customerIndex][x] += release[x];
		}
	}

	/**
	 * Checks if the request will leave the bank in a safe state.
	 * @param customerIndex  The customer's index (0-indexed).
	 * @param request        An array of the requested count for each resource.
	 * @return true if the requested resources will leave the bank in a
	 *         safe state, else false
	 */
	private synchronized boolean checkSafe(int customerIndex, int[] request) {
		// TODO: check if the state is safe
		int[] temp_avail = this.available.clone();
		int[][] temp_need = new int[this.numberOfCustomers][this.numberOfResources];
		int[][] temp_allocation = new int[this.numberOfCustomers][this.numberOfResources];
		for (int i = 0; i < this.numberOfCustomers; i++){
     		temp_need[i] = Arrays.copyOf(need[i], need[i].length);
     		temp_allocation[i] = Arrays.copyOf(allocation[i], allocation[i].length);
		}
		for (int j = 0; j < this.numberOfResources; j++){
			temp_avail[j] = this.available.clone()[j] - request[j];
			temp_need[customerIndex][j] = this.need.clone()[customerIndex][j] - request[j];
			temp_allocation[customerIndex][j] = this.allocation.clone()[customerIndex][j] + request[j];
		}
		int[] finish = new int[this.numberOfCustomers];
		Boolean possible = true;
		while(possible) {
			possible = false;
			for (int y = 0; y < this.numberOfCustomers; y++){
				Boolean enoughWork = true;
				for (int x = 0; x < this.numberOfResources; x++){
					if (temp_need[y][x] > temp_avail[x]){
						enoughWork = false;
					}
				}
				if (finish[y] == 0 && enoughWork) {
					possible = true;
					finish[y] = 1;
					for (int z = 0; z < this.numberOfResources; z++){
						temp_avail[z] += temp_allocation[y][z];
					}
				}	
			}
		}
		int[] finalState = new int[this.numberOfCustomers];
		Arrays.fill(finalState, 1);
		return(Arrays.equals(finish, finalState));
	}


	/**
	 * Parses and runs the file simulating a series of resource request and releases.
	 * Provided for your convenience.
	 * @param filename  The name of the file.
	 */
	public static void runFile(String filename) {

		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(filename));

			String line = null;
			String [] tokens = null;
			int [] resources = null;

			int n, m;

			try {
				n = Integer.parseInt(fileReader.readLine().split(",")[1]);
			} catch (Exception e) {
				System.out.println("Error parsing n on line 1.");
				fileReader.close();
				return;
			}

			try {
				m = Integer.parseInt(fileReader.readLine().split(",")[1]);
			} catch (Exception e) {
				System.out.println("Error parsing n on line 2.");
				fileReader.close();
				return;
			}

			try {
				tokens = fileReader.readLine().split(",")[1].split(" ");
				resources = new int[tokens.length];
				for (int i = 0; i < tokens.length; i++)
					resources[i] = Integer.parseInt(tokens[i]);
			} catch (Exception e) {
				System.out.println("Error parsing resources on line 3.");
				fileReader.close();
				return;
			}

			Banker theBank = new Banker(resources, n);

			int lineNumber = 4;
			while ((line = fileReader.readLine()) != null) {
				tokens = line.split(",");
				if (tokens[0].equals("c")) {
					try {
						int customerIndex = Integer.parseInt(tokens[1]);
						tokens = tokens[2].split(" ");
						resources = new int[tokens.length];
						for (int i = 0; i < tokens.length; i++)
							resources[i] = Integer.parseInt(tokens[i]);
						theBank.setMaximumDemand(customerIndex, resources);
					} catch (Exception e) {
						System.out.println("Error parsing resources on line "+lineNumber+".");
						fileReader.close();
						return;
					}
				} else if (tokens[0].equals("r")) {
					try {
						int customerIndex = Integer.parseInt(tokens[1]);
						tokens = tokens[2].split(" ");
						resources = new int[tokens.length];
						for (int i = 0; i < tokens.length; i++)
							resources[i] = Integer.parseInt(tokens[i]);
						theBank.requestResources(customerIndex, resources);
					} catch (Exception e) {
						System.out.println("Error parsing resources on line "+lineNumber+".");
						fileReader.close();
						return;
					}
				} else if (tokens[0].equals("f")) {
					try {
						int customerIndex = Integer.parseInt(tokens[1]);
						tokens = tokens[2].split(" ");
						resources = new int[tokens.length];
						for (int i = 0; i < tokens.length; i++)
							resources[i] = Integer.parseInt(tokens[i]);
						theBank.releaseResources(customerIndex, resources);
					} catch (Exception e) {
						System.out.println("Error parsing resources on line "+lineNumber+".");
						fileReader.close();
						return;
					}
				} else if (tokens[0].equals("p")) {
					theBank.printState();
				}
			}
			fileReader.close();
		} catch (IOException e) {
			System.out.println("Error opening: "+filename);
		}

	}

	/**
	 * Main function
	 * @param args  The command line arguments
	 */
	public static void main(String [] args) {
		if (args.length > 0) {
			runFile(args[0]);
		}
	}

}