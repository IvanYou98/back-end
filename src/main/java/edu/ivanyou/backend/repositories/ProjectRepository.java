package edu.ivanyou.backend.repositories;

import edu.ivanyou.backend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    Project findByProjectIdentifier(String identifier);
}
