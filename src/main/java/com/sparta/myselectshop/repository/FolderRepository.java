package com.sparta.myselectshop.repository;

import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    //쿼리 메서드
    // select * from folder ➡️ finaAll
    // select * from folder where ➡️ By
    // select * from folder where user_id ➡️ User
    // select * from folder where user_id = ? and name in (?,?,?,?,?,?)➡️ In
    List<Folder> findAllByUserAndNameIn(User user, List<String> folderNames);

    List<Folder> findAllByUser(User user);
}
