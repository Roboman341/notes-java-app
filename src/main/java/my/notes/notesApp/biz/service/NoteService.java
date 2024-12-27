package my.notes.notesApp.biz.service;

import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.biz.model.Note;
import my.notes.notesApp.data.NoteRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.MissingResourceException;
import java.util.stream.Stream;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final CustomerService customerService;

    public NoteService (NoteRepository noteRepository, CustomerService customerService) {
        this.noteRepository = noteRepository;
        this.customerService = customerService;
    }

    public Iterable<Note> getAllNotes () {
        Customer currentUser = customerService.getCurrentUser();

        // If the user is an admin, return all notes
        if (currentUser.getAuthorities().contains("ROLE_ADMIN")) { //TODO: think of adding exception handling here
            return noteRepository.findAll();
        }

        // Otherwise, return only the notes created by the current user
        return Stream.of(noteRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found")))
                .toList();
    }

    public Note getNoteByID(Long id) {
        Customer currentUser = customerService.getCurrentUser();
        Note noteToReturn = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!noteToReturn.getCreator().getId().equals(currentUser.getId()) &&
                !currentUser.getAuthorities().contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("You do not have permission to view this note");
        }
        return noteToReturn;
    }

    public Note saveNote (Note note) {
        Note savedNote = noteRepository.save(note);
        return savedNote;
    }

    public void deleteNoteByID (Long id) {
        noteRepository.deleteById(id);
    }
}
