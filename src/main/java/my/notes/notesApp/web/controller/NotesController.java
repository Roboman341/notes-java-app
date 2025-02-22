package my.notes.notesApp.web.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.biz.model.Note;
import my.notes.notesApp.biz.service.CustomerService;
import my.notes.notesApp.biz.service.NoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@Log4j2
@RequestMapping("/notes")
public class NotesController {
    private final NoteService noteService;
    private final CustomerService customerService;

    public NotesController (NoteService noteService, CustomerService customerService) {
        this.noteService = noteService;
        this.customerService = customerService;
    }

    @GetMapping
    public String getAllNotes (Model model) {
        Customer currentUser = customerService.getCurrentUser();
        model.addAttribute("notes", noteService.getAllNotes(currentUser));
        model.addAttribute("currentUser", currentUser);
        log.info("User: {} with authorities: {} requested their notes", currentUser.getUsername(), currentUser.getAuthorities());
        return "notes";
    }

    @GetMapping("/new")
    public String showCreateNewNoteForm (Model model) {
        model.addAttribute("note", new Note());
        return "new";
    }

    @PostMapping
    public String saveNote (@Valid @ModelAttribute Note note, BindingResult result) {
        if (result.hasErrors()) {
            return "new"; // Return form with errors
        }
        note.setCreator(customerService.getCurrentUser());
        noteService.saveNote(note);
        log.info(String.valueOf(note));
        log.info("Note's title: {}", note.getTitle());
        return "redirect:/notes";
    }

    @GetMapping("/{id}/edit")
    public String showEditNoteForm(@PathVariable Long id, Model model) {
        Note note = noteService.getNoteByID(id);
        if (note != null) {
            model.addAttribute("note", note);
            log.info("Note's title: {}", note.getTitle());
            log.info("Note's content: {}", note.getContent());
            return "edit";
        }
        return "redirect:/notes";
    }

    @GetMapping("/{id}/delete")
    public String deleteNote(@PathVariable Long id) {
        noteService.deleteNoteByID(id);
        return "redirect:/notes";
    }
}
