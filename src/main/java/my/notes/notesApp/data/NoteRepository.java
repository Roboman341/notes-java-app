package my.notes.notesApp.data;

import my.notes.notesApp.biz.model.Note;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NoteRepository extends CrudRepository<Note, Long> {}
