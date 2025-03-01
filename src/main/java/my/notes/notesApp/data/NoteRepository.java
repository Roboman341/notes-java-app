package my.notes.notesApp.data;

import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.biz.model.Note;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends CrudRepository<Note, Long> {
    Iterable<Note> findByCreator(Customer currentUser);
}
