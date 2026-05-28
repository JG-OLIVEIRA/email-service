package dev.jorge.projects.email.repositories;

import dev.jorge.projects.email.entities.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailRepository extends JpaRepository<Email, UUID> {}
