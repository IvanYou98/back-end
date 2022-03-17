package edu.ivanyou.backend.services;

import edu.ivanyou.backend.exception.ProjectIdException;
import edu.ivanyou.backend.model.Backlog;
import edu.ivanyou.backend.model.ProjectTask;
import edu.ivanyou.backend.repositories.BacklogRepository;
import edu.ivanyou.backend.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {
    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
        // PTs should be added to a specific project,(project != null && backlog != null)
        Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
        if (backlog == null) {
            throw new ProjectIdException("Project id: " + projectIdentifier + " does not exist!");
        }
        // set the backlog to project task
        projectTask.setBacklog(backlog);
        //  // we want our project sequence to be: PROJ1-1 PROJ1-2, PROJ1-3
        //  (which means the first, second, third task of project1)
        Integer backlogSequence = backlog.getPTSequence();
        backlogSequence++;
        backlog.setPTSequence(backlogSequence);
        projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence);
        projectTask.setProjectIdentifier(projectIdentifier);

        // INITIAL priority when priority null
        if (projectTask.getPriority() == 0 || projectTask.getPriority() == null) {
            projectTask.setPriority(3);
        }
        //INITIAL status to "TO_DO" when status is null
        if (projectTask.getStatus().equals("") || projectTask.getStatus() == null) {
            projectTask.setStatus("TO_DO");
        }

        return projectTaskRepository.save(projectTask);
    }


    public List<ProjectTask> findByProjectIdentifier(String project_id) {
        Backlog backlog = backlogRepository.findByProjectIdentifier(project_id);
        if (backlog == null) {
            throw new ProjectIdException("Project id: " + project_id + " does not exist!");
        }
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(project_id);
    }

    public ProjectTask getProjectTask(String projectIdentifier, String projectSequence) {
        // 1. make sure that we're searching on an existing backlog(project)
        Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);

        if (backlog == null) {
            throw new ProjectIdException("Project id: " + projectIdentifier + " not found");
        }

        // 2. make sure that our tasks exists
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(projectSequence);
        if (projectTask == null) {
            throw new ProjectIdException("Project Task Sequence: " + projectSequence + " not found");
        }

        // 3. make sure the project tasks belongs to the project we specify in the path
        if (!projectIdentifier.equals(projectTask.getProjectIdentifier())) {
            throw new ProjectIdException("Project Task : " + projectSequence + " does not belong to project: " + projectIdentifier);
        }
        return projectTaskRepository.findByProjectSequence(projectSequence);
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask,
                                             String projectIdentifier,
                                             String projectSequence) {
        getProjectTask(projectIdentifier, projectSequence);
        projectTaskRepository.save(updatedTask);
        return updatedTask;
    }

    public ProjectTask deleteByProjectSequence(String projectIdentifier,
                                        String projectSequence) {
        ProjectTask projectTask = getProjectTask(projectIdentifier, projectSequence);
        projectTaskRepository.delete(projectTask);
        return projectTask;
    }
}
