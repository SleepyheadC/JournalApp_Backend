package com.application.journalApp.repository;
import com.application.journalApp.entity.JournalEntry;
import com.application.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

//Mongo Repo takes two things one is entity and second one is id
public interface UserRepository extends MongoRepository<User, ObjectId> {
  User findByUserName(String userName);

  void deleteByUserName(String username);
}
