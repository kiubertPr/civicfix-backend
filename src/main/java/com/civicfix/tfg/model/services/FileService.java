package com.civicfix.tfg.model.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.civicfix.tfg.model.entities.Post;
import com.civicfix.tfg.model.entities.User;
import com.civicfix.tfg.model.services.exceptions.ForbiddenFileTypeException;
import com.civicfix.tfg.model.services.exceptions.MaxFileSizeException;

public interface FileService {

    void uploadFile(Post post, List<MultipartFile> file) throws IOException, ForbiddenFileTypeException, MaxFileSizeException;
    void uploadAvatar(User user, MultipartFile avatar) throws IOException, ForbiddenFileTypeException, MaxFileSizeException;
    void deleteFile(String publicId) throws IOException;
} 
