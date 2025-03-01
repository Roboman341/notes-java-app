package my.notes.notesApp.biz.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.biz.model.Note;
import my.notes.notesApp.data.NoteRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final CustomerService customerService;

    public NoteService (NoteRepository noteRepository, CustomerService customerService) {
        this.noteRepository = noteRepository;
        this.customerService = customerService;
    }

    @Transactional
    public Iterable<Note> getAllNotes (Customer customer) {
        // If the user is an admin, return all notes. Comment out for now.
//        if (customer.getAuthorities().stream().anyMatch(i -> i.getAuthority().equals("ROLE_ADMIN"))) {
//            return noteRepository.findAll();
//        }
        return noteRepository.findByCreator(customer);
    }

    @Transactional
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
        note.setCreator(customerService.getCurrentUser()); // Set creator before saving
        return noteRepository.save(note);
    }

    @Transactional
    public void deleteAllNotesByCreator(Customer customer){
        Iterable<Note> byCreator = noteRepository.findByCreator(customer);
        if (byCreator.iterator().hasNext()) {
            byCreator.forEach(note -> noteRepository.deleteById(note.getId()));
        }
        else {
            log.info("There is no notes for this user: {}", customer.getUsername());
        }
    }

    public void deleteNoteByID (Long id) {
        noteRepository.deleteById(id);
    }
}
