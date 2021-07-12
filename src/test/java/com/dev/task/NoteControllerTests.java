package com.dev.task;


import com.dev.task.models.NoteModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class NoteControllerTests {

    public static final String API_NOTES_PATH = "/api/notes";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final int ZERO = 0;

    @Test
    void findAll() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(API_NOTES_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<NoteModel> noteList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), List.class);
        assertThat(noteList).isNotEmpty();
    }

    @Test
    void insertAndGetId() throws Exception {
        NoteModel note = populateNote();
        MvcResult mvcResult = performInsert(note);
        NoteModel expectedNote = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), NoteModel.class);

        assertThat(expectedNote.getId()).isNotEqualTo(ZERO);
    }

    @Test
    void insertAndThenFindById() throws Exception {
        NoteModel note = populateNote();
        MvcResult mvcResultInsert = performInsert(note);

        NoteModel newNote = objectMapper.readValue(mvcResultInsert.getResponse().getContentAsString(), NoteModel.class);

        MvcResult mvcResultGet = performGet(newNote);

        NoteModel expectedNote = objectMapper.readValue(mvcResultGet.getResponse().getContentAsString(), NoteModel.class);

        assertThat(expectedNote.getName()).isEqualTo(note.getName());
        assertThat(expectedNote.getDescription()).isEqualTo(note.getDescription());
    }

    @Test
    void findByIdWithInvalidId_ThenFindById_ShouldReturnNotFound() throws Exception {
        NoteModel note = new NoteModel();
        String apiPathWithId = getApiNotesPathWithId(note);

        mockMvc.perform(MockMvcRequestBuilders.get(apiPathWithId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
    }

    @Test
    void insertThenUpdateThenFindById_ShouldReturnNewValues() throws Exception {
        NoteModel note = populateNote();
        MvcResult mvcResultInsert = performInsert(note);

        NoteModel originalNote = objectMapper.readValue(mvcResultInsert.getResponse().getContentAsString(), NoteModel.class);

        NoteModel updateNote = populateNote();
        String apiPathWithId = getApiNotesPathWithId(originalNote);
        mockMvc.perform(
                MockMvcRequestBuilders.put(apiPathWithId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateNote))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        MvcResult mvcResultGet = performGet(originalNote);
        NoteModel expectedNote = objectMapper.readValue(mvcResultGet.getResponse().getContentAsString(), NoteModel.class);

        assertThat(expectedNote.getName()).isEqualTo(updateNote.getName());
        assertThat(expectedNote.getDescription()).isEqualTo(updateNote.getDescription());
    }

    @Test
    void updateIdWithInvalidId_ShouldReturnNotFound() throws Exception {
        NoteModel note = new NoteModel();
        String apiPathWithId = getApiNotesPathWithId(note);

        mockMvc.perform(MockMvcRequestBuilders.put(apiPathWithId)
                .content(objectMapper.writeValueAsString(note))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
    }

    @Test
    void insertThenDeleteThenFindById_ShouldReturnNotFound() throws Exception {
        NoteModel note = populateNote();
        MvcResult mvcResultInsert = performInsert(note);

        NoteModel originalNote = objectMapper.readValue(mvcResultInsert.getResponse().getContentAsString(), NoteModel.class);

        String apiPathWithId = getApiNotesPathWithId(originalNote);

        mockMvc.perform(MockMvcRequestBuilders.delete(apiPathWithId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.get(apiPathWithId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
    }

    @Test
    void deleteThenFindById_StatusShouldReturnNotFound() throws Exception {
        NoteModel note = populateNote();
        String apiPathWithId = getApiNotesPathWithId(note);

        mockMvc.perform(MockMvcRequestBuilders.delete(apiPathWithId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
    }

    MvcResult performInsert(NoteModel note) throws Exception {
        String content = objectMapper.writeValueAsString(note);

        return mockMvc.perform(
                MockMvcRequestBuilders.post(API_NOTES_PATH)
                        .content(content)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
    }

    MvcResult performGet(NoteModel note) throws Exception {
        String apiNotesPathWithId = getApiNotesPathWithId(note);

        return mockMvc.perform(MockMvcRequestBuilders.get(apiNotesPathWithId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    private NoteModel populateNote() {
        NoteModel note = new NoteModel();
        note.setName(randomString("Name "));
        note.setDescription(randomString("Desc "));
        return note;
    }

    private String randomString(String prefix) {
        String output = String.format("%s%o", prefix, new Date().getTime());
        return output;
    }

    private String getApiNotesPathWithId(NoteModel newNote) {
        return String.format("%s/%d", API_NOTES_PATH, newNote.getId());
    }
}
