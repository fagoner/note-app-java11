package com.dev.task.mappers;

import com.dev.task.models.NoteModel;
import org.apache.ibatis.annotations.*;

import java.util.Collection;

@Mapper
public interface NoteMapper {

    @Select("SELECT * FROM note_app.note;")
    Collection<NoteModel> findAll();

    @Insert("INSERT INTO note_app.note(name, description) VALUES (#{name}, #{description});")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(NoteModel note);

    @Select("SELECT * FROM note_app.note WHERE id = #{id};")
    NoteModel findById(int id);

    @Update("UPDATE note_app.note SET name = #{note.name}, description =#{note.description} WHERE id = #{id};")
    void update(int id, NoteModel note);

    @Delete("DELETE FROM note_app.note WHERE id = #{id};")
    void delete(int id);

}
