package com.padimasso.autocasting.application.notes.repository;

import com.padimasso.autocasting.application.notes.model.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NoteRepository extends JpaRepository<NoteEntity, UUID> {

}
