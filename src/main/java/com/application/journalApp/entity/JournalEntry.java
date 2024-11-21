package com.application.journalApp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

//POJO - Plain Old Java Object
//if this collection is not written inside the parentheses of Document then it searches/creates for class name in the database
@Document(collection="journal_entries")
@Data
@NoArgsConstructor
public class JournalEntry {

//    journal entry class ko likha hai entity package ke andar ismein hmari journal entry ka structure kaisa hoga ye likha hua hai

    @Id
    private ObjectId id;
    @NonNull
    private String title;
    private String content;
    private LocalDateTime date;

}
