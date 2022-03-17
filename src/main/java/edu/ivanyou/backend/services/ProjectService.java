package edu.ivanyou.backend.services;

import edu.ivanyou.backend.exception.ProjectIdException;
import edu.ivanyou.backend.model.Backlog;
import edu.ivanyou.backend.model.Project;
import edu.ivanyou.backend.repositories.BacklogRepository;
import edu.ivanyou.backend.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    public Project saveOrUpdateProject(Project project) {
        // if we create a new project, then we also create a new backlog
        if (project.getId() == null) {
            Backlog backlog = new Backlog();
            // one to one binding
            project.setBacklog(backlog);
            backlog.setProject(project);
            backlog.setProjectIdentifier(project.getProjectIdentifier());
        }

        // If we are updating an existing project, we don't want to change the backlog
        if (project.getId() != null) {
            Backlog backlog = backlogRepository.findByProjectIdentifier(project.getProjectIdentifier());
            project.setBacklog(backlog);
        }

        return projectRepository.save(project);
    }

    public Project findProjectByIdentifier(String projectIdentifier) {
        Project project = projectRepository.findByProjectIdentifier(projectIdentifier);
        if (project == null) {
            throw new ProjectIdException("Project id: " + projectIdentifier + " does not exist!");
        }
        return project;
    }

    public List<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public void deleteProject(String projectIdentifier) {
        Project theProject = projectRepository.findByProjectIdentifier(projectIdentifier);
        if (theProject == null) {
            throw new ProjectIdException("Project id: " + projectIdentifier + " does not exist!");
        }
        projectRepository.delete(theProject);
    }

}
