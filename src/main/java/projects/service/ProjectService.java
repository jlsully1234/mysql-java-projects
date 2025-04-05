package projects.service;

import projects.dao.ProjectDao;
import projects.entity.Project;



public class ProjectService {
private ProjectDao projecDao = new ProjectDao();

/**
 * this method calls the DAO class to insert a project row.
 * @param project the {@link Project} object
 * @return The Project object with the newly generated primary key value
 */
public Project addProject (Project project) {
	return projecDao.insertProject(project);
}

}
