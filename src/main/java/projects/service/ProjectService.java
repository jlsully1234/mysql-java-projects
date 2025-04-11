package projects.service;

import java.util.List;
import java.util.NoSuchElementException;

import projects.dao.ProjectDao;
import projects.entity.Project;



public class ProjectService {
private ProjectDao projectDao = new ProjectDao();

/**
 * this method calls the DAO class to insert a project row.
 * @param project the {@link Project} object
 * @return The Project object with the newly generated primary key value
 */
public Project addProject (Project project) {
	return projectDao.insertProject(project);
}
//Returns a list of projects he DOA method provides 
public List<Project> fetchAllProjects() {
	return projectDao.fetchAllProjects();
}

//This method retrieves a single project based on its ID from the fetchProjectById method in projectDao. 
// there is also a exception message that uses the ID to indicate which project was not found
public Project fetchProjectById(Integer projectId) {
	return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException(
		"Project with projectID =" + projectId + " does not exist"));
}


}


