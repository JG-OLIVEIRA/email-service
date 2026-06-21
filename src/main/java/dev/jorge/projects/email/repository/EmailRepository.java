package dev.jorge.projects.email.repository;

import dev.jorge.projects.email.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailRepository extends JpaRepository<Email, UUID> {}
