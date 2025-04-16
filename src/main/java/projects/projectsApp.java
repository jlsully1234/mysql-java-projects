package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

/**
 * This class is a menu driven application that accepts user input from the console. 
 * It performs CRUD operations on the project table.
 */

public class ProjectsApp {
  private Scanner scanner = new Scanner (System.in);
  private ProjectService projectService = new ProjectService(); 
  private Project curProject;
	
	//formatter:off
	private List<String> operations = List.of(
		"1) Add a project",
		"2) List projects",
		"3) Select a project",
		"4) Update project details",
		"5) Delete a project"
		
	);
	 /**
	  * @formatter:on
	  * 
	  * Entry point for Java application
	  * 
	  * @param args Unused
	  */
  public static void main(String[] args)  {
		 new ProjectsApp().processUserSelections();
		}
		/**
		 * This method prints the operations, gets a user selection, and performs the requested
		 * operation. It repeats until the user requests that the application terminate.
		 */
	private void processUserSelections() {
		boolean done = false;
		
		while(!done)  {
		
			try {
				int selection = getUserSelection();		
		        
				switch(selection) {
				case -1:
					done = exitMenu();
					break;
					
				case 1:
					createProject();
					break;
					
				case 2:
					listProjects();
					break;
					
				case 3: 
					selectProject();
				
				case 4:
					updateProjectDetails();
					break;
					
				case 5:
					deleteProject();
					break;
					
				default:
					System.out.println("\n" + selection + "is not a valid selection Try again.");
				break;
				}
			}
		
		catch(Exception e) {
			System.out.println("\nError " + e + " Try again." );
		}
	  }
		
/* This method deletes a row from the project table if the project ID is found in an existing row.
 * This method deletes a project and all the project child records. First the method lists
 * the projects, then allows the user to input a project ID of the project to delete. If
 * the user enters a project ID that is not in the project table, the service throws an exception.
 */
	} private void deleteProject() {
	listProjects();
	
	Integer projectId = getIntInput("Enter the ID of the project to delete");
	
	projectService.deleteProject(projectId);
	System.out.println("Project " +projectId + " was deleted successfully.");
	
	if(Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
		curProject = null;
	}
	}
	
	/**
	 * This method allows the user to modify project details. The user is asked to 
	 * modify a field in the project. The value of the current project is displayed as a default.
	 * If the user presses Enter without entering a value, the value in the current project is 
	 * unchanged
	 */
	private void updateProjectDetails() {
		//if there is no current project selected , return to menu
				if(Objects.isNull(curProject)) {
					System.out.println("\nPlease select a project.");
					return;
	}
	/**
	 *  Collect input from user . If the user presses enter without entering a value the local variable
	 * will be null
	 */			 
	String projectName = getStringInput("Enter the project name["+ curProject.getProjectName() + "]");
	
	BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [" 
		+ curProject.getEstimatedHours() + "]");
	
	BigDecimal actualHours = getDecimalInput("Enter the actual hours + ["
		+curProject.getActualHours() + "]");
	
	Integer difficulty = getIntInput("Enter the project difficulty 1-5 ["
		+ curProject.getDifficulty() + "]");
	
	String notes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");
	/* Use the value supplied by user if user enter something. if user did not enter a value, set
	 * the value to that to that which is in the currently selected project
	 */
	
	Project project = new Project();
	project.setProjectName(Objects.isNull(projectName)
	? curProject.getProjectName() : projectName);
	
	project.setProjectId(curProject.getProjectId());
	project.setProjectName(curProject.getProjectName());
	project.setEstimatedHours(curProject.getEstimatedHours());
	project.setActualHours(curProject.getActualHours());
	project.setDifficulty(curProject.getDifficulty());
	project.setNotes(curProject.getNotes());
	
	/* call the project service to update the project details*/
	projectService.modifyProjectDetails(project);
	
	/* Re-read the current project, which will display the current details.*/
	curProject = projectService.fetchProjectById(curProject.getProjectId());
	
			
	
			
			
			
		
		
		
		
		
		}
	// The following 2 methods are designed to list all available projects and allow the user to select one
	//by using its ID. The selected project is fetched and stored in the curProject variable.
 		private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		
		curProject = null;
		
		curProject = projectService.fetchProjectById(projectId);
		
		}
		private void listProjects() {
			List<Project> projects = projectService.fetchAllProjects();
			
			System.out.println("\nProjects:");
			
			projects.forEach(project -> System.out.println("   " + project.getProjectId()
			+ ": " + project.getProjectName()));
			
 		}
		/**
 		 * Gathers user input for a project row then call the project service to create a new row.
 		 */
	private void createProject() {
		String projectName = getStringInput("Enter the project name"); 
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualhours = getDecimalInput ("Enter the actual hours");
		Integer difficulty = getIntInput ("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");
		
		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualhours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created projects " + dbProject);
	}

		/**
		 * Gets the user's input from the console and converts it to a BigDecimal
		 * 
		 * @param the prompt to display on console
		 * @return BigDecimal value if successful
		 * @throws DbException thrown if error occurs converting the number to a BigDecimal
		 *
		 */
		private BigDecimal getDecimalInput(String prompt) {
		  String input = getStringInput(prompt);
		  
		  if(Objects.isNull(input)) {
			  return null;
		  
		}
		
		try {
			/* Create BigDecimal object and set it to two decimal places (the scale).*/
			return new BigDecimal(input).setScale(2);
		}		
		catch(NumberFormatException e) {
		  throw new DbException(input + " is not a valid decimal number.");
		}
	}	
		/**
		 * Called when the user wants to exit the application. It prints a message and returns the 
		 * {@code true} to terminate the app.
		 * 
		 *  @return{@code true}
		 */
		
		private boolean exitMenu() {
		System.out.println("Exiting the menu.");
			return true;
		}
		/**
		 * This method prints the available menu selections. It then gets user's menu selection
		 * from the console and converts it into a int
		 * 
		 * @return The menu selection as int or -1 if nothing is selected.
		 */
		private int getUserSelection() {
			printOperations();
			
			Integer input = getIntInput("Enter a new selection");
			
			return Objects.isNull(input) ? -1 : input; 
		}
		
		/** Prints a prompt on the console and then gets the user's input from the console. It then converts
		 * the input to an integer
		 * 
		 * @param prompt the prompt to print
		 * @return If user enters nothing, {@code null} is returned. Otherwise the input is converted
		 * to an integer
		 * @throws DbExceptiom throw if the input is not a valid integer
		 */
		
		
		
		private Integer getIntInput(String prompt) {
			String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.valueOf(input);
		}
		catch(NumberFormatException e) {
		  throw new DbException(input + " is not a valid number");
	  }
	}
			
		/** Prints a prompt on the console and then gets the user's input from the console. If user enters
		 * nothing {@code null} is returned. Otherwise, the trimmed input is returned
		 *
		 * @param prompt the prompt to print
		 * @return If user enters nothing, {@code null} is returned. Otherwise the input is converted
		 * to an integer
		 */
		private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		
		return input.isBlank() ? null : input.trim();
		
		}
		
	/**
	 * 	Print the menu selections
	 */
	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");
	operations.forEach(line -> System.out.println(" "+ line));
	
	if(Objects.isNull(curProject)) {
		System.out.println(("\nYou are not working with a project."));
	}
	else {
		System.out.println(("\nYou are working with project " + curProject));
	}
	}
		
		 }	 
			  
		
	
	
	
	
