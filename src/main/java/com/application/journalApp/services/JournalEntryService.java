package com.application.journalApp.services;

import com.application.journalApp.entity.JournalEntry;
import com.application.journalApp.entity.User;
import com.application.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//saari services mein app ki functionality likhi hoti hai
// It is a layer of the application that is responsible for carrying out the bussiness logic and encapsulating the app's functionality

@Component
public class JournalEntryService {

    @Autowired
//    we are autowiring the journalEntryRepository to do the DML operations
    private JournalEntryRepository journalEntryRepository;
    @Autowired
//    user service hum log isliye autowire kr rhe hai kyuki hmhe user ko find krna pd rha hai yaha
    private UserService userService;
    @Transactional
    public void saveEntry(JournalEntry journalEntry, String userName) {
        try {
//            ek journal entry to the corresponding user save krne ke liye sabse pehle username fetch kro
            User user = userService.findByUserName(userName);

//            phir us journal entry ka time set kro
            journalEntry.setDate(LocalDateTime.now());

//            phir use journal entry repo mein save kr do
            JournalEntry saved = journalEntryRepository.save(journalEntry);

//          ab usi journal entry ko get krke user ke journal entry waale array mein save kr do
            user.getJournalEntries().add(saved);

//            ab ab user service ki help se naya user db mein save kr do
            userService.saveNewUser(user);
        }
        catch(Exception e){
            System.out.println(e);
            throw new RuntimeException("An exception occurred while saving the entry");
        }
    }

//    repository ke upar jo bhi function lgate hai wo sbhi pehle se defined hote hai ListCrud interface mein

    public void saveEntry(JournalEntry journalEntry) {
        journalEntryRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public boolean deleteById(ObjectId id, String userName) {
        boolean removed;
        try {
            // Fetch user
            User user = userService.findByUserName(userName);
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            if (removed) {
                userService.saveUser(user);
                journalEntryRepository.deleteById(id);

            }
            return removed;
        }
        catch(Exception e){
            System.out.println(e);
            throw new RuntimeException("An error occured while deleting the entry ",e);
        }
    }

//    public List<JournalEntry> findByUserName(String userName){
//
//    }
}



