package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.xml.stream.events.StartDocument;
import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;


/**
 * This class uses JDBC to perform CRUD operations on the project tables
 */
public class ProjectDao extends DaoBase {
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";


/** Insert a project row into the project table.
 * 
 * @param The project object to insert.
 * @return The project object with the primary key.
 * @throws DbException Thrown if an error occurs inserting the row.
 */
		
public Project insertProject(Project project) {
 //@formatter:off
	String sql = ""
		+ "INSERT INTO " + PROJECT_TABLE + " "
		+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
		+ "VALUES "
		+"(?, ?, ?, ?, ?)";
	//@formatter:on
	/**
	 * This code establishes a connection with the database, prepares and executes SQL,
	 * Executes update, retrieve and set project ID, commits the transaction and has 
	 * exception handling
	 * 
	 */
	try(Connection conn = DbConnection.getConnection()) {
	  startTransaction(conn);
	
	try(PreparedStatement stmt =  conn.prepareStatement(sql)) {
		setParameter(stmt, 1, project.getProjectName(),String.class);
		setParameter(stmt, 2, project.getEstimatedHours(),BigDecimal.class);
		setParameter(stmt, 3, project.getActualHours(),BigDecimal.class); 
		setParameter(stmt, 4, project.getDifficulty(),Integer.class);
		setParameter(stmt, 5, project.getNotes(),String.class);
		
		stmt.executeUpdate();
		
		Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
		commitTransaction(conn);
		
		project.setProjectId(projectId);
		return project;
	}
	catch(Exception e ) {
	  rollbackTransaction(conn);
	  throw new DbException(e);
	}
  }
  catch(SQLException e) {
	  throw new DbException(e);
	  
  }
 }

//method uses JDBC and is designed to retrieve all project (rows) records from connecting to PROJECT TABLE, a database table within Projects. It constructs a
// an SQL query to select all columns from the table and order them by project name and throws an exception 
public List<Project> fetchAllProjects() {
	String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY Project_name";
	
	try(Connection conn = DbConnection.getConnection()) {
		startTransaction(conn);
	  try(PreparedStatement stmt = conn.prepareStatement(sql)) {
		  try(ResultSet rs = stmt.executeQuery()) {
		    List<Project> projects = new LinkedList<>();
		    
		    while(rs.next()) {
		    	projects.add(extract(rs, Project.class)); 
		    	
		    }
		  
		    return projects;
		    
		  }
	   }
	   catch(Exception e) {
		   rollbackTransaction(conn);
		   throw new DbException(e);
	
	   }
  }
  catch(SQLException e) {
	  throw new DbException(e);
  }


}//methods used JDBC method to call and retrieve a single project row Project for the database along with its associated materials, steps, and 
//categories using JDBC it handles potential null values.

public Optional<Project> fetchProjectById(Integer projectId) {
	  String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
	  
	  try(Connection conn = DbConnection.getConnection()) {
	    startTransaction(conn);
	  
	    /*
	       * This try block is used to wrap all code to return the project row and accompanying
	       * materials, steps and categories so that, if an error occurs at any place, the transaction
	       * can be rolled back correctly.
	       */	    
	    
	    
	    try {
		 Project project = null; 
		 
		 try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class); 
			
			 /*
	           * Alternate approach. If you know your parameter will never be null you can set the
	           * parameter on the statement directly using JDBC. If the parameter might be null, you
	           * must perform a null check and call rs.setNull() if it is null.
	           */	
			
			
			
			try(ResultSet rs = stmt.executeQuery()) {
				if(rs.next()) {
				 project = extract(rs, Project.class);	
				}
			}
		
		 }
		 /*
	         * This null check isn't expressly needed because if the project ID is invalid, each method
	         * will simply return an empty list. However, it avoids three unnecessary database calls.
	         */
	  
	  if(Objects.nonNull(project)) {
		 project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
		 
		 project.getSteps().addAll(fetchProjectSteps(conn, projectId));
		 project.getCategories().addAll(fetchProjectCategories(conn, projectId));
	  
	  }
	  /*
       * Optional.ofNullable() is used because project may be null at this point if the given
       * project ID is invalid.
       */
	  return Optional.ofNullable(project);
	  
	  }
	  
	    catch(Exception e ) {
		  rollbackTransaction(conn);
		  throw new DbException(e);
	  }
	  }
		catch (SQLException e) {
			throw new DbException(e);
	  }
  }
/**
 * This method retrieves all the categories associated with the given project ID. Note the inner
 * join to join the category rows to the project_category join table. The join table is needed
 * because projects and categories have a many-to-many relationship. Categories can exist on their
 * own without having associated projects and projects can exist on their own without having any
 * categories. The join table links the project and category tables together.
 * 
 * The connection is supplied by the caller so that the categories can be returned within the
 * current transaction.
 * 
 * @param conn The Connection object supplied by the caller.
 * @param projectId The project ID to use for the categories.
 * @return A list of categories if successful.
 * @throws DbException Thrown if an exception is thrown by the driver.
 */

private List< Category> fetchProjectCategories(Connection conn, Integer projectId) throws SQLException {
	// @formatter: off
	String sql =""
		+ "SELECT c.* "
		+ "FROM " + CATEGORY_TABLE + " c "
	    + "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
	    + "WHERE project_id = ? ";
	    
	  // @formatter: on
	  try(PreparedStatement stmt = conn.prepareStatement(sql)) {
		  setParameter(stmt, 1, projectId, Integer.class);
		  
	  try(ResultSet rs = stmt.executeQuery()) {
		  List<Category> categories = new LinkedList<Category>();
		  
		 while(rs.next()) {
			 categories.add(extract(rs, Category.class));
	  } 
		 return categories; 
	  }
  }
}
/**
 * This method uses JDBC method calls to retrieve project steps for the given project ID. The
 * connection is supplied by the caller so that steps can be retrieved on the current transaction.
 * 
 * @param conn The caller-supplied connection.
 * @param projectId The project ID used to retrieve the steps.
 * @return A list of steps in step order.
 * @throws SQLException Thrown if the database driver encounters an error.
 */
// Method retrieves a list of step objects associated with a specific project in a database. It has an SQL Exception.
// it also has a SQL query to select all columns from the STEP_TABLE. he use of PreparedStatement helps prevent 
// SQL injection by safely setting query parameters.
private List<Step> fetchProjectSteps(Connection conn, Integer projectId) throws SQLException {
	  String sql = "SELECT * FROM "+ STEP_TABLE + " s WHERE s.project_id = ?";
	  
	  try(PreparedStatement stmt = conn.prepareStatement(sql)) {
		  setParameter(stmt, 1, projectId, Integer.class);
		  try(ResultSet rs = stmt.executeQuery()) {
			List<Step> steps = new LinkedList<Step>();  
			
			while(rs.next()) {
				steps.add(extract(rs, Step.class));
			}
			
			return steps;
			  
		  }
	  }
}

/**
 * This method uses JDBC method calls to retrieve project materials for the given project ID. The
 * connection is supplied by the caller so that project materials can be retrieved on the current
 * transaction.
 * 
 * @param conn The caller-supplied connection.
 * @param projectId The project ID used to retrieve the materials.
 * @return A list of materials.
 * @throws SQLException Thrown if the database driver encounters an error.
 */

private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
	 String sql = "SELECT * FROM "+ MATERIAL_TABLE + " m WHERE m.project_id = ?";
	  
	  try(PreparedStatement stmt = conn.prepareStatement(sql)) {
		  setParameter(stmt, 1, projectId, Integer.class);
		  try(ResultSet rs = stmt.executeQuery()) {
			List<Material> materials = new LinkedList<Material>();  
			
			
			while(rs.next()) {
				materials.add(extract(rs, Material.class));
			}
			
			return materials;
			  
		  }
	  }
}
/**
 * This method uses JDBC calls to modify the project details. An UPDATE statement is used for
 * this.
 * 
 * @param project The project object with modified details.
 * @return {@code true} if the project was modified successfully. {@code false} if an invalid
 *         project ID is supplied.
 * @throws DbException Thrown if a SQLException is thrown by the driver.
 */
public boolean modifyProjectDetails(Project project)  {
//@formatter:off
	String sql = "" + "UPDATE " + PROJECT_TABLE + " SET "
					+ "project_name = ?, "
					+ "estimated_hours = ?, "
					+ "actual_hours = ?, "
					+ "difficulty = ?, "
					+ "notes = ? "
					+ "WHERE project_id = ? ";
//@formatter:on
System.out.println(project);	
try(Connection conn = DbConnection.getConnection()) {
	startTransaction(conn);
	
	try(PreparedStatement stmt = conn.prepareStatement(sql)) {
	  setParameter(stmt, 1, project.getProjectName(), String.class);
	  setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
	  setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
	  setParameter(stmt, 4, project.getDifficulty(), Integer.class);
	  setParameter(stmt, 5, project.getNotes(), String.class);
	  setParameter(stmt, 6, project.getProjectId(), Integer.class);
	  
	  boolean modified = stmt.executeUpdate() == 1;
	  commitTransaction(conn);
	  return modified;
		
	}
	catch (Exception e) {
		rollbackTransaction(conn);
		throw new DbException(e);	
      }
	}
	catch(SQLException e) {
		throw new DbException(e);
	}
}
/**
 * This method deletes the project row from the project table if the project ID is found in an
 * existing row. All child rows (materials, steps and category associations) are deleted as well
 * because the foreign keys in those tables were created by specifying ON DELETE CASCADE.
 * 
 * @param projectId The project ID of the project to delete.
 * @return {@code true} if the project was deleted. {@code false} if an invalid project ID is
 *         supplied.
 * @throws DbException Thrown if the driver throws{@link SQLException}.
   */
public boolean deleteProject(Integer projectId) {
	String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";
	System.out.println(sql);
	try(Connection conn = DbConnection.getConnection()) {
		startTransaction(conn);
		
		
	try(PreparedStatement stmt = conn.prepareStatement(sql)) {
	setParameter(stmt, 1, projectId, Integer.class);
	
	boolean deleted = stmt.executeUpdate() == 1;
	
	 commitTransaction(conn);
	  return deleted;
	}
	 catch(Exception e) {
	        rollbackTransaction(conn);
	        throw new DbException(e);
	 }
	}
	 catch(SQLException e) {
	      throw new DbException(e);
	 }
	
	}
	
	
	
	
}


