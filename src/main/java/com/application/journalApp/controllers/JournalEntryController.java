package com.application.journalApp.controllers;

import com.application.journalApp.entity.JournalEntry;
import com.application.journalApp.entity.User;
import com.application.journalApp.services.JournalEntryService;
import com.application.journalApp.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.events.CollectionEndEvent;

import javax.print.attribute.standard.JobHoldUntil;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {
    @Autowired
    private JournalEntryService journalEntryService;
    @Autowired
    private UserService userService;
    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser(){

//        ab aapko function mein @PathVariable krke username bhejne ki zAROORAT nahi hai
//        ab aap postman se jo bhi request bhejege journal entries get krne ke liye to usmein authorization waale part mein jo username and password aapne dala
//        hoga wo directly SecurityContextHolder ke through authentication mein store ho jaayeg and uske baad getName() method ki help se hum username ko fetch kr lenge

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);

//        uske baad humhe jo bhi user mil rha hai uski saari journal entry with the help getter from the user
//        all variable jismein JournalEntry type ki list store hoti hai usmeni store kr lenge
        List<JournalEntry> all = user.getJournalEntries();

//        agar journal entry mil jaati hai to return kr denge aur uske saath http response ok bhi bhej denge
        if(all!=null && !all.isEmpty()){
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

//            yaha pr corresponding username pr humhe jo bhi journal entrries request body mein aa rhi hai
//            usko save kra lenge
            journalEntryService.saveEntry(myEntry, userName);
            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/id/{myId}")
    public ResponseEntity<?> getJournalEntryById(@PathVariable ObjectId myId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);

//      sbse pehle user ki journal entries ko fetch kr liya uspr stream lgaya jo ki filter ko chalane mein help krega then hmne apni id se match krne waali
//        journal entries ko match kr diya and collect ki help se hmne phir se apni entries ko list mein convert krke collect variable mein save kr liya
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
        if(!collect.isEmpty()) {
            Optional<JournalEntry> journalEntry = journalEntryService.findById(myId);
            if (journalEntry.isPresent()) {
                return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
            }
        }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);


    }

    @DeleteMapping("/id/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId myId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        boolean removed = journalEntryService.deleteById(myId, userName);
        if(removed) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/id/{myId}")
    public ResponseEntity<?> updateJournalEntryById(@PathVariable ObjectId myId, @RequestBody JournalEntry newEntry){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
        if(!collect.isEmpty()){

//          We are making it optional kyuki particular id ke corresponding humhe journal entry miil bhi skti hai aur nahi bhi mil skti hai
//          to ye optional mein value ho bhi skti hai aur nahi bhi ho skti hai
            Optional<JournalEntry> journalEntry = journalEntryService.findById(myId);

//            agar journal entry present hoti hai to
            if(journalEntry.isPresent()){

//                get ek optional container object ka member function hai jo ki Journal entry type ke variable mein jo ki old hai usmein value laakr dega
                JournalEntry old = journalEntry.get();

//                old ka title set krna hai agar new entry ka title null ya blank nahi hai to new entry ka title get krke old mein set kr do
//                agar null hai to old waale ka hi title set kr do
                old.setTitle(newEntry.getTitle()!=null && !newEntry.getTitle().equals("")?newEntry.getTitle():old.getTitle());

//                same is the mechanism for the setContent
                old.setContent(newEntry.getContent()!=null && !newEntry.equals("")?newEntry.getContent(): old.getContent());

//                ab journalEntryService ke saveEntry method ki help se old wali entry hi save kra do
                journalEntryService.saveEntry(old);

//                aur http status ok send kr do
                return new ResponseEntity<>(old, HttpStatus.OK);
            }
        }

//        agar humhe koi journal entry nahi mili hai to not found return kr do

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }
}
