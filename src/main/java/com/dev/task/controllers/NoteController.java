package com.dev.task.controllers;

import com.dev.task.exceptions.NoteNotFoundException;
import com.dev.task.mappers.NoteMapper;
import com.dev.task.models.NoteModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("api/notes")
public class NoteController {

    private NoteMapper noteMapper;

    public NoteController(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    Collection<NoteModel> findAll() {
        return this.noteMapper.findAll();
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public NoteModel insert(@RequestBody NoteModel noteModel) {
        this.noteMapper.insert(noteModel);
        return noteModel;
    }

    @GetMapping("{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public NoteModel findById(@PathVariable int id) throws NoteNotFoundException {
        NoteModel note = this.noteMapper.findById(id);
        if (note == null)
            throw new NoteNotFoundException();
        return note;
    }

    @PutMapping("{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void update(@PathVariable int id, @RequestBody NoteModel note) throws NoteNotFoundException {
        if (this.noteMapper.findById(id) == null)
            throw new NoteNotFoundException();
        this.noteMapper.update(id, note);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void delete(@PathVariable int id) throws NoteNotFoundException {
        if (this.noteMapper.findById(id) == null)
            throw new NoteNotFoundException();
        this.noteMapper.delete(id);
    }
}
