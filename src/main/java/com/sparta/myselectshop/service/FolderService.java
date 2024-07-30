package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.FolderResponseDto;
import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository; // 주입

    public void addFolders(List<String> folderNames, User user) {
        // 폴더 중복 확인
        // findAll : 전체 찾기
        // ByUser : User 기준
        // Name : 이름
        // In : 여러개
        List<Folder> existFolderList = folderRepository.findAllByUserAndNameIn(user,folderNames);

        List<Folder> folderList = new ArrayList<>();

        for (String folderName : folderNames) {
            if(!isExistFolderName(folderName,existFolderList)){
                //중복된 폴더가 아니라면 새로운 폴더를 만든다.
                Folder folder = new Folder(folderName,user); // 폴더 이름과 회원 정보
                folderList.add(folder);
            }else{
                throw new IllegalArgumentException("폴더명이 중복되었습니다.");
            }
        }

        //folder가 잘 생성되었으면 저장
        folderRepository.saveAll(folderList);
    }

    //폴더 가져오기
    public List<FolderResponseDto> getFolders(User user) {
        List<Folder> folderList = folderRepository.findAllByUser(user);
        List<FolderResponseDto> responseDtoList = new ArrayList<>();

        // 하나씩 뽑아온 폴더를 responseDtoList에 저장
        for (Folder folder : folderList) {
            //생성자에 의해서 folder를 받아와서 folder의 정보를 responseDto의 필드쪽에 넣어주고, 객체가 생성되면서 responseDtoList 담긴다.
            responseDtoList.add(new FolderResponseDto(folder));

        }
        // 다 만들어진 FolderResponseDto를 반환
        return responseDtoList;
    }

    private boolean isExistFolderName(String folderName, List<Folder> existFolderList) {
        for (Folder existFolder : existFolderList) {
            //받아온 폴더 이름(folderName)과 이름 별로 찾아온 폴더(folderNames)와 같은지 비교
            if(folderName.equals(existFolder.getName())){
                return true;
            }
        }
        return false; // 일치하는 폴더가 없으면 즉, 중복되지 않는다면 false반환
    }
}
