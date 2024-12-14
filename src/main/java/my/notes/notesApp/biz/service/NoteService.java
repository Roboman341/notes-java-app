package my.notes.notesApp.biz.service;

import my.notes.notesApp.biz.model.Note;
import my.notes.notesApp.data.NoteRepository;
import org.springframework.stereotype.Service;

@Service
public class NoteService {
    private final NoteRepository noteRepository;

    public NoteService (NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Iterable<Note> getAllNotes () {
        return noteRepository.findAll();
    }

    public Note getNoteByID (Long id) {
        return noteRepository.findById(id).orElse(null);
    }

    public Note saveNote (Note note) {
        Note savedNote = noteRepository.save(note);
        return savedNote;
    }

    public void deleteNoteByID (Long id) {
        noteRepository.deleteById(id);
    }
}
