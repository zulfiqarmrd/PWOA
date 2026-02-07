package org.cloudbus.cloudsim.examples.Algorithms;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.Math;

public class PWOA {
	
	private static String category = "GoCJ";
	private static int subCategory = 5;
	private static int noOfWhales = 60; 				// Also determines the concurrency
	private static int maxItr = 120;
	
	private static HashMap<Integer, Double> bestWhalesMap = new HashMap<>();
	private static HashMap<Integer, HashMap <Cloudlet, Vm>> gBWhaleMap = new HashMap<>();
	private static HashMap<Integer, HashMap<Integer, Double>> VMRTMap = new HashMap<>();
	private static HashMap<Integer, HashMap<Cloudlet, Vm>> whalesPopulationMap = new HashMap<>();
	private static double bestWhaleMakespan = Double.MAX_VALUE;						
	private static double gBestWhaleValue = Double.MAX_VALUE; 				
	
	/*********** Added for Multi-threading - Getters and Setters *************/
	public static HashMap<Integer, HashMap<Cloudlet, Vm>> get_whalesPopulationMap() // reference of the map is returned
	{	return whalesPopulationMap;	}
	
	public static HashMap<Integer, HashMap<Integer, Double>> get_VMRTMap() 
	{	return VMRTMap;	}
	
	public static HashMap<Integer, HashMap <Cloudlet, Vm>> get_gBWhaleMap() 
	{	return gBWhaleMap;	}
	
	public static HashMap<Integer, Double> get_bestWhalesMap() 
	{	return bestWhalesMap;	}

	public static double get_bestWhaleMakespan() 
	{	return bestWhaleMakespan;	}

	public static double get_gBestWhaleValue() 
	{	return gBestWhaleValue;	}

	public static void set_bestWhaleMakespan(double bWhaleMk) 
	{	bestWhaleMakespan = bWhaleMk;}
	
	public static void set_gBestWhaleValue(double gBWhaleValue) 
	{	gBestWhaleValue = gBWhaleValue;}
	/**************************************************************************/
	
	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;

	/** The vmlist. */
	private static List<Vm> vmlist;

	/************* Added for Multi-threading - Getters and Setters *************/
	public static List<Vm> get_vmlist() 
	{	return vmlist;	}

	public static List<Cloudlet> get_cloudletList() 
	{	return cloudletList;	}
	/***************************************************************************/
	
	private static List<Vm> createGocjVm(int userId, int vms, int idShift) {
		//Creates a container to store VMs. This list is passed to the broker later
		LinkedList<Vm> list = new LinkedList<Vm>();

		//VM Parameters
		long size = 10000; 	//image size (MB)
		int ram = 512; 		//vm memory (MB) 
		int mips = 400;		// keeps changing in the lower code
		long bw = 1000;		//fix for all vms
		int pesNumber = 1; 	//number of cpus
		String vmm = "Xen"; //VMM name

		//create VMs
		Vm[] vm = new Vm[10];

		vm[0] = new Vm(idShift + 0, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
		list.add(vm[0]);
		Log.printLine("VM #0 configured with mips_"+mips);
		
		vm[1] = new Vm(idShift + 1, userId, 440, pesNumber, 560, bw, size, vmm, new CloudletSchedulerTimeShared());
		list.add(vm[1]);
		Log.printLine("VM #1 configured with mips_440");
		
		vm[2] = new Vm(idShift + 2, userId, 600, pesNumber, 720, bw, size, vmm, new CloudletSchedulerTimeShared());
		list.add(vm[2]);
		Log.printLine("VM #2 configured with mips_600");
		
		vm[3] = new Vm(idShift + 3, userId, 800, pesNumber, 960, bw, size, vmm, new CloudletSchedulerTimeShared());
		list.add(vm[3]);
		Log.printLine("VM #3 configured with mips_800");
		
		vm[4] = new Vm(idShift + 4, userId, 900, pesNumber, 1088, bw, size, vmm, new CloudletSchedulerTimeShared());
		list.add(vm[4]);
		Log.printLine("VM #4 configured with mips_900");
		
		vm[5] = new Vm(idShift + 5, userId, 1200, pesNumber, 1440, bw, size, vmm, new CloudletSchedulerTimeShared());
		list.add(vm[5]);
		Log.printLine("VM #5 configured with mips_1200");

		vm[6] = new Vm(idShift + 6, userId, 2000, pesNumber, 2384, bw, size, vmm, new CloudletSchedulerTimeShared());
		list.add(vm[6]);
		Log.printLine("VM #6 configured with mips_2000");
		
		vm[7] = new Vm(idShift + 7, userId, 4000, pesNumber, 4768, bw, size, vmm, new CloudletSchedulerTimeShared());
		list.add(vm[7]);
		Log.printLine("VM #7 configured with mips_4000");
		
		vm[8] = new Vm(idShift + 8, userId, 10000, pesNumber, 11936, bw, size, vmm, new CloudletSchedulerTimeShared());
		list.add(vm[8]);
		Log.printLine("VM #8 configured with mips_10000");
		
		vm[9] = new Vm(idShift + 9, userId, 12000, pesNumber, 14336, bw, size, vmm, new CloudletSchedulerTimeShared());
		list.add(vm[9]);
		Log.printLine("VM #9 configured with mips_12000");
		
		return list;
	}
	
	private static List<Vm> createHcspVM(int userId, int vms, int idShift) {
		//Creates a container to store VMs. This list is passed to the broker later
		LinkedList<Vm> list = new LinkedList<Vm>();

		//VM Parameters
		long size = 10000; 	//image size (MB)
		int ram = 512; 		//vm memory (MB) 
		int mips = 0;		// keeps changing in the lower code
		long bw = 1000;		
		int pesNumber = 1; 	//number of cpus
		String vmm = "Xen"; //VMM name

		//create VMs
		Vm[] vm = new Vm[1024];
		
		String fileToRead = "D:/CloudSim-3/Eclipse/newPWhale/newPWhale/examples/workload/HCSP/vm_MIPS/VM_"+subCategory+".txt";
		try (BufferedReader reader = new BufferedReader(new FileReader(fileToRead))) 
        {
        	String line;
        	int i = 0;
            while ((line = reader.readLine()) != null) 
            {
        		mips = Integer.parseInt(line);
            	vm[i] = new Vm(idShift + i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            	list.add(vm[i]);
        		Log.printLine("VM #" + i + " configured with mips_"+mips);
            	i++;
            }
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
		return list;
	}

	private static List<Vm> createNasaHpc2nVM(int userId, int vms, int idShift) {	//VM Parameters- According to Jaya
		//Creates a container to store VMs. This list is passed to the broker later
		LinkedList<Vm> list = new LinkedList<Vm>();

		
		long size = 10240; 	//image size (MB)
		int ram = 512; 		//vm memory (MB) 
		int mips = 500;		// keeps changing in the lower code
		long bw = 1024;		
		int pesNumber = 1;	//number of cpus
		String vmm = "Xen"; //VMM name

		//create VMs
		Vm[] vm = new Vm[750];	// Jaya has 600 vms but used two test scenarios. I am using only 1 test scenario, so consider 750.
		
		for (int i = 0; i < 750; i++) 
		{
			if (i >= 0 && i < 50) {mips = 500;}
			else if (i >= 50 && i < 150) {mips = 1000; ram = ram+48; bw = bw+48;}
			else if (i >= 150 && i < 300) {mips = 1500; ram = ram+48; bw = bw+48;}
			else if (i >= 300 && i < 500) {mips = 2000; ram = ram+48; bw = bw+48;}
			else if (i >= 500 && i < 750) {mips = 2500; ram = ram+48; bw = bw+48;}

    		vm[i] = new Vm(idShift + i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
        	list.add(vm[i]);
//    		System.out.println("VM No. " + i + " created: " + mips);	
		} //    			mips = 500+(i*(1600-500)/599);	// exact estimation for Jaya

    	return list;
	}
	
	private static List<Cloudlet> createGocjCloudlet(int userId, int cloudlets, int idShift)
	{
		// Creates a container to store Cloudlets
		LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();
		
		//cloudlet parameters
		long length = 0;
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;
		UtilizationModel utilizationModel = new UtilizationModelFull();

		Cloudlet[] cloudlet = new Cloudlet[cloudlets];
		long [] cloudletlengths = new long [subCategory];

		String fileToRead = "D:/CloudSim-3/Eclipse/newPWhale/newPWhale/examples/workload/GoCJ/GoCJ_Dataset_"+subCategory+".txt";
//		System.out.println(fileToRead);
		
		try (BufferedReader reader = new BufferedReader(new FileReader(fileToRead))) 
        {
        	String line;
        	int i = 0;
            while ((line = reader.readLine()) != null) 
            {
            	cloudletlengths[i] = Long.parseLong(line);
    			i++;
            }
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
            
        for (int i=0; i < cloudlets; i++)
        {
        	length = cloudletlengths[i];
        	cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, 
										utilizationModel, utilizationModel, utilizationModel);
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);
        }    
        return list;
	}

	private static List<Cloudlet> createHcspCloudlet(int userId, int cloudlets, int idShift)
	{
		// Creates a container to store Cloudlets
		LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();
		
		//cloudlet parameters
		long length = 0;
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;
		UtilizationModel utilizationModel = new UtilizationModelFull();

		Cloudlet[] cloudlet = new Cloudlet[cloudlets];
		long [] cloudletlengths = new long [cloudlets];

		String fileToRead = "D:/CloudSim-3/Eclipse/newPWhale/newPWhale/examples/workload/HCSP/cloudlets_MI/Ds_"+subCategory+".txt";
//		System.out.println(fileToRead);
		
		try (BufferedReader reader = new BufferedReader(new FileReader(fileToRead))) 
        {
        	String line;
        	int i = 0;
            while ((line = reader.readLine()) != null) 
            {
            	cloudletlengths[i] = (long)(Float.parseFloat(line));
//    			System.out.println(cloudletlengths[i]);
            	i++;
            }
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
            
        for (int i=0; i < cloudlets; i++)
        {
        	length = cloudletlengths[i];
        	cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, 
										utilizationModel, utilizationModel, utilizationModel);
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);
        }    
        return list;
        }
        
	
	/**
	 * Creates main() to run this example
	 */
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		Log.printLine("Starting PWOA...");

		try {

			if (args.length > 0) 
			{
				category = args[0];
				
				if (category == "GoCJ") 
				{
					subCategory = Integer.parseInt(args[1]); 
					noOfWhales = Integer.parseInt(args[2]);
					maxItr = Integer.parseInt(args[3]);
				}
				else if (category == "HCSP") 
				{
					subCategory = Integer.parseInt(args[1]);
					noOfWhales = Integer.parseInt(args[2]);
					maxItr = Integer.parseInt(args[3]);
				}
			}
			
			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities.
			int num_user = 1;   // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag); // producing output in milliseconds

			// Second step: Create Datacenters
			//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
			@SuppressWarnings("unused")
			Datacenter datacenter0 = createDatacenter("Datacenter_0", category);
			
			//Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			//Fourth step: Create VMs and Cloudlets and send them to broker

			if (category == "GoCJ") 
			{
				vmlist = createGocjVm(brokerId, 10, 0); // 10 VMs as given in Parallel random matrix PSO
				cloudletList = createGocjCloudlet(brokerId, subCategory, 0); // 100-1000 tasks
			}
			else if (category == "HCSP") 
			{
				vmlist = createHcspVM(brokerId, 32, 0); // 32 VMs
				cloudletList = createHcspCloudlet(brokerId, 1024, 0); // 1024 tasks
			}
			
			/* Whale Optimization Algorithm */
			double whaleStartTime = System.currentTimeMillis();
			
			int itr = 1;
			
//			double bestWhaleMakespan = Double.MAX_VALUE;						
//			double gBestWhaleValue = Double.MAX_VALUE; 				
//			double a = 0.0;					// 2 (exploration) linearly decreases to 0 (exploitation). Coefficient for the encircling prey mechanism. It affects the size of the search space and the intensity of exploitation.
//			double A = 0.0;					// -a (clockwise spiral) to a (counter clockwise). Maximum distance of a whale's position update during the encircling prey mechanism. It controls the size of the search space and influences the movement of the whales in a circular or spiral pattern around a potential solution. This parameter determines the radius of the circular movement or spiral pattern.
//			double C = 0.0;					// Linear or exponential decreasing value. Problem dependent. Coefficient for the bubble-net prey encircling mechanism. How fast the algorithm transitions from exploration to exploitation.
//			double p = 0.0;					// 0(not used) to 1(always used). Probability of a whale using the bubble-net prey encircling mechanism. 
//			double b = 0.0;					// Position vector of a whale (candidate solution). Coefficient for the spiral updating mechanism. 
//			double l = 0.0;					// [-1, 1] Negative or Positive Step size 
//			double r1 = 0.0;
//			double r2 = 0.0;
//		    double a2 = 0.0; 				// a2 linearly decreases from -1 to -2 - Additional variable by Mirjallili which is not listed in the paper itself.
			
			for (int w = 0; w < noOfWhales; w++)
			{
				HashMap<Integer, Double> innerVMRTMap = new HashMap<>();
				for (Vm vm:vmlist)
	    		{
					innerVMRTMap.put(vm.getId(), 0.0);
	    		}
				VMRTMap.put(w, innerVMRTMap);
			}

			HashMap<Cloudlet, Vm> innergbFMap = new HashMap<>();
			for (Cloudlet cld:cloudletList)
    		{
				Random r = new Random();
				int ranVmId = r.nextInt(vmlist.size());
				Vm randVm = vmlist.get(ranVmId); 		
				innergbFMap.put(cld, randVm);
    		}
			gBWhaleMap.put(0, innergbFMap);

			bestWhalesMap = initializeWhales(cloudletList, vmlist, bestWhalesMap, whalesPopulationMap, VMRTMap, gBWhaleMap, noOfWhales);
			gBestWhaleValue = getGBest(bestWhalesMap);
			
			System.out.println("			The first gBestWhale is: " + gBestWhaleValue+"\n");

			/********************************** Parallelize Section **************************************/
			Random rand = new Random();

			while(itr <= maxItr)					
			{
				ExecutorService es = Executors.newFixedThreadPool(noOfWhales);
				List<Future<String>> futureList = new ArrayList<>();
	
				for (int i = 0; i < noOfWhales; i++) 
				{
					futureList.add(es.submit(new whalesBody(i, itr, maxItr, rand)));
				}
	
				es.shutdown(); 
				es.awaitTermination(10, TimeUnit.SECONDS);

				itr++;
			}// End of Iterations loop

			/*******************************************************************************************/			
			double whaleEndTime = System.currentTimeMillis();
			double executionTime = whaleEndTime-whaleStartTime;
			System.out.println("The running duration of PWOA is: " + executionTime);
			System.out.println("\n			The last gBestWhale is: " + gBestWhaleValue + "\n");

//			  graphs g1 = new graphs(); g1.printGraph(bestWhalesMap);
			
			broker.submitVmList(vmlist);
			broker.submitCloudletList(cloudletList);

			// Final Mapping of cloudlets to VMs			
			for (HashMap.Entry<Cloudlet, Vm> entry : gBWhaleMap.get(0).entrySet()) 
			{
				Cloudlet fcld = entry.getKey();
				Vm fvm = entry.getValue(); 
				broker.bindCloudletToVm(fcld.getCloudletId(), fvm.getId());
			}
	
			// Fifth step: Starts the simulation
			CloudSim.startSimulation();
			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			CloudSim.stopSimulation();
			printCloudletList(newList);
			double allTasksRunningTime = 0;

			// Iterate through each cloudlet in the simulation
			for (Cloudlet tasks : cloudletList) {
			    // Check if the cloudlet has completed execution
			    if (tasks.getCloudletStatus() == Cloudlet.SUCCESS) {
			        // Get the cloudlet's finish time and add it to the total completion time
			        double finishTime = tasks.getFinishTime();
			        allTasksRunningTime += finishTime;
			    }
			}
			Log.printLine("\nAll Cloudlets Running Time: " + allTasksRunningTime);
			
			Cloudlet cld0 = newList.get(0);						// First cloudlet
			double minStartTime = cld0.getExecStartTime();

			Cloudlet cldLast= newList.get(newList.size()-1);	// Last cloudlet
			double maxFinishTime = cldLast.getExecStartTime(); 

			for (Cloudlet cld : cloudletList) 
			{
			    if (cld.getExecStartTime() < minStartTime) 
			    {
			    	minStartTime = cld.getExecStartTime();
			    }
			}
			for (Cloudlet cld : cloudletList) 
			{
			    if (cld.getFinishTime() > maxFinishTime) 
			    {
			        maxFinishTime = cld.getFinishTime();
			    }
			}
			
			Log.printLine("Start time of first executed cloudlet: " + minStartTime + " ms");
			Log.printLine("Finish time of last executed cloudlet: " + maxFinishTime + " ms\n");
			double makespan = maxFinishTime-minStartTime;
			Log.printLine("Makespan of all cloudlets: " + makespan + " ms");
			Log.printLine("ARU of all VMs: " + avgRU(makespan));	// cloudsim calculated makespan is sent as parameter
			Log.printLine("Average Response Time of all cloudlets: " + avgRespT() + " ms");
	    	Log.printLine("Throughput: " + cloudletList.size()/makespan+"\n");            	
	    	Log.printLine("PWOA finished!");
		
	    	try
	    	{
	    		String fileToWrite = null;
				
	    		if (category == "GoCJ") 
				{
		    		fileToWrite = "D:/CloudSim-3/Eclipse/newPWhale/newPWhale/examples/reports/PWOA/GoCJ/output_"+ subCategory + ".txt";
//		    		fileToWrite = "D:/CloudSim-3/Eclipse/newPWhale/newPWhale/examples/reports/PWOA/GoCJ/output_"+ noOfWhales + ".txt";
//		    		fileToWrite = "D:/CloudSim-3/Eclipse/newPWhale/newPWhale/examples/reports/PWOA/GoCJ/output_"+ maxItr + ".txt";
				}
				else if (category == "HCSP") 
				{
		    		fileToWrite = "D:/CloudSim-3/Eclipse/newPWhale/newPWhale/examples/reports/PWOA/HCSP/output_"+ subCategory + ".txt";
				}

	    		FileWriter fWriter = new FileWriter(fileToWrite,true);
	    		BufferedWriter bWriter = new BufferedWriter(fWriter);
	    		bWriter.write(makespan + ", "+ avgRU(makespan)+ ", "+ avgRespT()+", "+ cloudletList.size()/makespan + "," + executionTime + "\n");
	    		bWriter.close();
	    		fWriter.close();
	    	}
	    	catch(IOException e)
	    	{
	    		System.out.println("An error occurred while writing to the file: " + e.getMessage());
	    	}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	// WOA Functions Definitions
	private static HashMap<Integer, Double> initializeWhales(
	List<Cloudlet> cloudletList, List<Vm> vmlist,
	HashMap<Integer, Double> bestWhalesMap, HashMap<Integer, HashMap<Cloudlet, Vm>> whalesPopulationMap, HashMap<Integer, HashMap<Integer, Double>> VMRTMap, HashMap<Integer, HashMap <Cloudlet, Vm>> gBWhaleMap, int noOfWhales)
	{	
		Random random = new Random();
		
		for (int w = 0; w < noOfWhales; w++)
		{
			HashMap<Cloudlet, Vm> singleWhaleMap = new HashMap<>();
	        for (int c = 0; c < cloudletList.size(); c++) 		// For each particle, there are "cloudletList.size" no of entries in the particleMap.
	        {
	            Cloudlet cloudlet = cloudletList.get(c);

	            int vmIndex = random.nextInt(vmlist.size());
	            Vm vm = vmlist.get(vmIndex);

	            /***** LC and MT Matrices ****/
//	            int[] vms = new int[vmlist.size()];		// Simulating LC_MTX
//	            for (int i = 0; i < vmlist.size(); i++) 
//	            {	
//	            	vms[i] = random.nextInt();
//	            }
//            	int max = 0; int index = 0;				// Simulating MP_MTX
//            	for (int i = 0; i < vmlist.size(); i++) 
//	            {	
//	            	if (vms[i]> max){max = vms[i]; index = i;}
//	            }
//	            int vmIndex = index;
//	            Vm vm = vmlist.get(vmIndex);

	            /***********************************************************/
	            
	            singleWhaleMap.put(cloudlet, vm);
	        
				double eTime = cloudlet.getCloudletLength()/vm.getMips();
				int vmId = vm.getId();
				
				if (VMRTMap.get(w).containsKey(vmId))
				{
					double prevLoad = VMRTMap.get(w).get(vmId);
					eTime = eTime + prevLoad;
					VMRTMap.get(w).put(vmId, eTime);
				}
				else
				{
					VMRTMap.get(w).put(vmId, eTime);
				}
		    }
//	        double fitness = calculateFitness(singleWhaleMap);		// 1 particleMap generated for all cloudlets for 1 particle
	        whalesPopulationMap.put(w, singleWhaleMap);
	        bestWhalesMap.put(w, getpbMap(VMRTMap.get(w))); // earlier fitness was included instead of VRTMap-later reverse this change
	   }
	       
		int gBestWhaleIndex = getGBestIndex(bestWhalesMap);
		gBWhaleMap.put(0, whalesPopulationMap.get(gBestWhaleIndex));

		return bestWhalesMap;
	}


	public static double calculateFitness(HashMap<Cloudlet, Vm> whalesPopulationMap) 
	{
		double[] vmLoads = new double [vmlist.size()];
		
		for (HashMap.Entry<Cloudlet, Vm> entry : whalesPopulationMap.entrySet()) 
		{
			Cloudlet cloudlet = entry.getKey();
            Vm vm = entry.getValue();
            double clength = cloudlet.getCloudletLength();
            int vmid = vm.getId();
            vmLoads[vmid] = vmLoads[vmid]+clength;
		}

		double makespan = vmLoads[0];
		for (int i = 1; i < vmLoads.length; i++) 
        {
            if (makespan < vmLoads[i]) 
            {
            	makespan = vmLoads[i];
            }
        }
//		System.out.println(makespan);
        return -makespan;
	}

	public static double getGBest(HashMap<Integer, Double> bestWhalesMap) {
	    double minParticleMakespan = bestWhalesMap.get(0);			// Selecting the particle having the minimum makespan.
		
	    for(int i=0; i < bestWhalesMap.size(); i++)
		{
			if (bestWhalesMap.get(i) < minParticleMakespan)
			{
			    minParticleMakespan = bestWhalesMap.get(i);
			}
		}
		return minParticleMakespan;
	}

	private static int getGBestIndex(HashMap<Integer, Double> bestWhalesMap) {
    double minParticleMakespan = Double.MAX_VALUE;			// Selecting the particle' index having the minimum makespan.
	int index = 0;
	
    for(int i=0; i < bestWhalesMap.size(); i++)
	{
		if (bestWhalesMap.get(i) < minParticleMakespan)
		{
		    minParticleMakespan = bestWhalesMap.get(i);
		    index = i;
		}
	}
//    System.out.println("The gBest index is: "+index);
	return index;
}
	
	public static double getVMReadyTime(HashMap<Integer, Double> vmRemMap, Vm VM) 
	{
		int vmid = VM.getId();
		double vmReadyT = 0.0;
		
		if (vmRemMap.get(vmid) != null)
		{
			vmReadyT = vmRemMap.get(vmid);
		}
		return vmReadyT;
	}	
	
	public static void updateUpVMRTMap(HashMap<Integer, Double> vmRemMap, Vm VM, double execTime) 
	{
//		System.out.println("Adding " + execTime + " to "+ VM.getId());
		int vmid = VM.getId();
		double prevLoad = 0.0;
		double newLoad = execTime;
		
		if (vmRemMap.get(vmid) == null)
		{
			vmRemMap.put(vmid, newLoad);
		}
		else
		{
			prevLoad = vmRemMap.get(vmid);
			double totalLoad = prevLoad + newLoad;
			vmRemMap.put(vmid, totalLoad);
		}
	}
	
	public static void updateDownVMRTMap(HashMap<Integer, Double> vmRemMap, Vm VM, double execTime) 
	{
//		System.out.println("Subtracting " + execTime + " from "+ VM.getId()+"\n");
		int vmid = VM.getId();
		double prevLoad = 0.0;
		
		
		if (vmRemMap.get(vmid) == null)
		{
			vmRemMap.put(vmid, prevLoad);
		}
		else
		{
			prevLoad = vmRemMap.get(vmid);
			double totalLoad = prevLoad - execTime;
			vmRemMap.put(vmid, totalLoad);
		}
	}
	
	public static void updatepMap(HashMap<Integer, Double> vmRemMap,
	  HashMap<Cloudlet, Vm> VmMap, double pos, int c) 
	  { 
		  int ranPos = (int) pos; 
		  Vm newRandVm = vmlist.get(ranPos); 
		  Cloudlet cloudlet = cloudletList.get(c);
		  VmMap.put(cloudlet, newRandVm); 
	  }
	 	
	public static double getpbMap(HashMap<Integer, Double> VMRTMap) 	// Returns the busiest VM -- It is also a fitness function
	{
		double busyVm = 0.0;
		
		for (HashMap.Entry<Integer, Double> entry : VMRTMap.entrySet()) 
		{
			double vmRt = entry.getValue();
			
			if (vmRt > busyVm)
	        {
				busyVm = vmRt; 
	        }
	    }
//		System.out.println("The bestValue of whale is: "+ busyVm);
		return busyVm;
	}	

	/* WOA Functions Definitions End*/

	private static double avgRU(double mk) 	// Average Resource Utilization according to OG-RADL eq.(10)
	{
		double ARU = 0.0, avgMakespan = 0.0, sum = 0.0;
		for(Vm vm:vmlist)
		  {
			double singleVmMakespan = 0.0;
			for (int i = 0; i<cloudletList.size(); i++)
			  {
				  if (cloudletList.get(i).getVmId() == vm.getId())
				  {
					  if(cloudletList.get(i).getFinishTime() > singleVmMakespan)
					  {
						  singleVmMakespan = cloudletList.get(i).getFinishTime(); // returns the lastly executed cloudlet by the vm
					  }
				  }				  
			  }
			  sum = sum + singleVmMakespan; // summing up the finish-times of last cloudlet's execution by all vms
		  }
		avgMakespan = sum/vmlist.size();
		ARU = avgMakespan/mk;
		return ARU;
	}	

	private static double avgRespT() 	// Average Response Time of All Tasks according to OG-RADL eq.(13)
	{
		double avgRTAllVms = 0.0, sumAvgRTSingleVms = 0;
		for(Vm vm:vmlist)
		{
			double avgRTSingleVm = 0;
			int count = 0;
			for(int i=0; i<cloudletList.size(); i++)
			{
				if (cloudletList.get(i).getVmId() == vm.getId())
				{
					avgRTSingleVm = avgRTSingleVm + cloudletList.get(i).getExecStartTime();    
					count++;
				}
			}
			if (avgRTSingleVm!=0 || count!=0)
			{
				avgRTSingleVm = avgRTSingleVm/count;
			}
			sumAvgRTSingleVms = sumAvgRTSingleVms + avgRTSingleVm;
		}
		avgRTAllVms = sumAvgRTSingleVms/vmlist.size();
		return avgRTAllVms;
	}	

	private static Datacenter createDatacenter(String name, String category)
	{		

        List<Host> hostList = new ArrayList<>();
 
        if (category == "GoCJ" || category == "HCSP")
        {
            /************ GoCJ and HCSP Start *************/
    		// 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
    		//    create a list to store these PEs before creating
    		//    a Machine.
    		List<Pe> peList1 = new ArrayList<Pe>();

    		int mips = 12000;	// the largest vm(9) requirment is 12000 mips.
    							//  If any of the machine is below 12000, then vm-9 will not be provisioned successfully.
    		
    		// 3. Create PEs and add these into the list.
    		//for a quad-core machine, a list of 4 PEs is required:
    		peList1.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
    		peList1.add(new Pe(1, new PeProvisionerSimple(mips)));
    		peList1.add(new Pe(2, new PeProvisionerSimple(mips)));
    		peList1.add(new Pe(3, new PeProvisionerSimple(mips)));

    		//Another list, for a hexa-core machine
    		List<Pe> peList2 = new ArrayList<Pe>();
    		peList2.add(new Pe(0, new PeProvisionerSimple(mips)));
    		peList2.add(new Pe(1, new PeProvisionerSimple(mips)));
    		peList2.add(new Pe(2, new PeProvisionerSimple(mips)));
    		peList2.add(new Pe(3, new PeProvisionerSimple(mips)));
    		peList2.add(new Pe(4, new PeProvisionerSimple(mips)));
    		peList2.add(new Pe(5, new PeProvisionerSimple(mips)));

    		//Another list, for a octa-core machine
    		List<Pe> peList3 = new ArrayList<Pe>();
    		peList3.add(new Pe(0, new PeProvisionerSimple(mips)));
    		peList3.add(new Pe(1, new PeProvisionerSimple(mips)));
    		peList3.add(new Pe(2, new PeProvisionerSimple(mips)));
    		peList3.add(new Pe(3, new PeProvisionerSimple(mips)));
    		peList3.add(new Pe(4, new PeProvisionerSimple(mips)));
    		peList3.add(new Pe(5, new PeProvisionerSimple(mips)));
    		peList3.add(new Pe(6, new PeProvisionerSimple(mips)));
    		peList3.add(new Pe(7, new PeProvisionerSimple(mips)));
    		
    		
    		//4. Create Hosts with its id and list of PEs and add them to the list of machines
    		int hostId=0;
    		int ram = 16896; // If any of the machine's RAM is below 16896, then vm-9 will not be provisioned successfully.
    		long storage = 1000000; //host storage
    		int bw = 15000;

    		hostList.add(new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw),storage, peList1, new VmSchedulerTimeShared(peList1))); // First machine

    		hostId++;
    		hostList.add(new Host(hostId,new RamProvisionerSimple(ram),new BwProvisionerSimple(bw),storage,peList2,new VmSchedulerTimeShared(peList2))); // Second machine
    		
    		hostId++;
    		hostList.add(new Host(hostId,new RamProvisionerSimple(ram),new BwProvisionerSimple(bw),storage,peList3,new VmSchedulerTimeShared(peList3))); // Third machine
    		
    		 /****************End ****************/
        
        }

		String arch = "x86";      			// system architecture
		String os = "Linux";          // operating system
		String vmm = "Xen";
		double time_zone = 10.0;         // time zone this resource located
		double cost = 3.0;              // the cost of using processing in this resource
		double costPerMem = 0.05;		// the cost of using memory in this resource
		double costPerStorage = 0.1;	// the cost of using storage in this resource
		double costPerBw = 0.1;			// the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now
		
		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);
		
		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.printLine(datacenter.getName());

		return datacenter;
	}

	//We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
	//to the specific rules of the simulated scenario
	private static DatacenterBroker createBroker(){

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects
	 * @param list  list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				Log.print("SUCCESS");

				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
						indent + indent + dft.format(cloudlet.getFinishTime()));
			}
		}
	}
}

class whalesBody implements Callable<String>
{
	private static final Object lock = new Object();
	int w;
	int itr; 
	int maxItr;
	Random rand;
	
	public whalesBody(int w, int itr, int maxItr, Random r) 
	{
		this.w = w;
		this.itr = itr;
		this.maxItr = maxItr;
		this.rand = r;
	}
	
	@Override
	public String call() throws Exception 
	{
		double a = 0.0;
		double r1 = 0.0; 
		double r2 = 0.0;
		double A = 0.0; 	// Eq. 2.3 - Facilitating exploitation (shrinking encircling mechanism) by decreasing the value of a.
		double C = 0.0;
		double p = 0.0;
		double l = 0.0;
		double b = 1;

		r1 = rand.nextDouble(); 
		r2 = rand.nextDouble();

		a = 3.0 - (double) itr * ((2.0) / maxItr);
		A = (2.0 * a * r1) - a; 	// Eq. 2.3 - Facilitating exploitation (shrinking encircling mechanism) by decreasing the value of a.
		C = 2.0 * r2;
		p = rand.nextDouble();
		l = (rand.nextDouble() * 2.25 ) - 1.0;
		double x = 1 +(w/100)+ w % 2.25;	// second best with minor degradation
//		double x = 1 +(w/100)+ w % 1.75;	// The best with minor improvement
		b = x*itr/maxItr;
//		b = 1;
		
		HashMap<Cloudlet, Vm> tmpWhaleMap = new HashMap<>(PWOA.get_whalesPopulationMap().get(w));		// Taking out the first whale

		for (int c = 0; c < PWOA.get_cloudletList().size(); c++)
		{
			Cloudlet cld = PWOA.get_cloudletList().get(c);
			Vm VM = PWOA.get_whalesPopulationMap().get(w).get(cld);	
			
			double dToRandVm = 0.0;
			double dToGB = 0.0;
			double dToAGB = 0.0;
			double newWhalePosition = 0.0;
			double randVm = 0.0;

			if (p < 0.5)		// Encircling Prey
			{
				if (Math.abs(A) >= 1)	// |A|>1 facilitates Exploration
				{
					randVm = rand.nextInt(PWOA.get_vmlist().size());
					dToRandVm = Math.abs(C*randVm - VM.getId());																	// Equation 2.7
					newWhalePosition = randVm - A*dToRandVm;																		// Equation 2.8
				}
				else if (Math.abs(A) < 1) // |A|<1 facilitates Exploitation
				{
					dToGB = Math.abs(C*PWOA.get_gBWhaleMap().get(0).get(cld).getId() - tmpWhaleMap.get(cld).getId());  			//Equation 2.1
					newWhalePosition = PWOA.get_gBWhaleMap().get(0).get(cld).getId() - A*dToGB;										// Equation 2.2
				}
			}
			else if (p >= 0.5)	// Spiral Bubble Net Attacking- facilitates Exploitation
			{
				dToAGB = Math.abs(PWOA.get_gBWhaleMap().get(0).get(cld).getId() - tmpWhaleMap.get(cld).getId());					 // Calculates D'	
				newWhalePosition = dToAGB * Math.exp(b*l) * Math.cos(2*Math.PI*l) + PWOA.get_gBWhaleMap().get(0).get(cld).getId(); // Equation 2.5
//					b = b+2; // the even values of b have been tested but not good.
			}	

			
			if (newWhalePosition > PWOA.get_vmlist().size() || newWhalePosition < 0)
			{
				newWhalePosition = rand.nextInt(PWOA.get_vmlist().size());

		            /********************* Swap for the above 1 instruction *********************/ 
//					int[] vms = new int[PWOA.get_vmlist().size()];		// Simulating LC_MTX
//		            for (int i = 0; i < PWOA.get_vmlist().size(); i++) 
//		            {	
//		            	vms[i] = rand.nextInt();
//		            }
//	            	int max = 0; int index = 0;							// Simulating MP_MTX
//	            	for (int i = 0; i < PWOA.get_vmlist().size(); i++) 
//		            {	
//		            	if (vms[i]> max){max = vms[i]; index = i;}
//		            }
//		            newWhalePosition = index;
		            /**************************************************************************/
			}
			int intnewWhalePosition = (int) newWhalePosition; 
			Vm newRandVm = PWOA.get_vmlist().get(intnewWhalePosition);
			double oldLoad = cld.getCloudletLength()/VM.getMips();
			PWOA.updateDownVMRTMap(PWOA.get_VMRTMap().get(w), VM, oldLoad);
			double newLoad = cld.getCloudletLength()/newRandVm.getMips();
			PWOA.updateUpVMRTMap(PWOA.get_VMRTMap().get(w), newRandVm, newLoad);
			tmpWhaleMap.put(cld, newRandVm);							// tmpWhaleMap contains the adjusted VMs of all cloudlets for 1 whale
		} // End of Cloudlets loop

		PWOA.get_whalesPopulationMap().put(w, tmpWhaleMap);			// Update the position of the whale.

		synchronized (lock) 
		{	
			/************************** Synchronization needed ************************/
			PWOA.set_bestWhaleMakespan(PWOA.getpbMap(PWOA.get_VMRTMap().get(w)));		// returning the recently filled VMRTMap to return the busiest vm's value (makespan)
			if (PWOA.get_bestWhaleMakespan() < PWOA.get_bestWhalesMap().get(w))
			{
				PWOA.get_bestWhalesMap().put(w, PWOA.get_bestWhaleMakespan());
		
				if (PWOA.get_bestWhaleMakespan() < PWOA.get_gBestWhaleValue())
				{
					PWOA.set_gBestWhaleValue(PWOA.get_bestWhaleMakespan());
					System.out.println("The g-Best is: " + PWOA.get_gBestWhaleValue() + "\n");
					PWOA.get_gBWhaleMap().put(0, PWOA.get_whalesPopulationMap().get(w));
				}
			}
			/*************************************************************************/
		}
		//		System.out.println("Whale No. " +w +", Iteration No. "+itr+ " completed.");

	System.out.println(											"Thread: "+ w);
		
	return "nothing";
	}
}
