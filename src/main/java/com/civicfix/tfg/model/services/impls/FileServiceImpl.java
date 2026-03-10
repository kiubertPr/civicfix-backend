package com.civicfix.tfg.model.services.impls;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.civicfix.tfg.model.entities.Post;
import com.civicfix.tfg.model.entities.User;
import com.civicfix.tfg.model.services.FileService;
import com.civicfix.tfg.model.services.exceptions.ForbiddenFileTypeException;
import com.civicfix.tfg.model.services.exceptions.MaxFileSizeException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class FileServiceImpl implements FileService {

    private final Cloudinary cloudinary;

    private static final List<String> DEFAULT_IMAGE = List.of(
        "https://res.cloudinary.com/civicfix/image/upload/v1744723480/296fe121-5dfa-43f4-98b5-db50019738a7_hln3ql.jpg",
        "parque-infantil",
        "parque-descuidado",
        "inauguracion-parque",
        "facultad-abandono",
        "edificio-abandonado",
        "contenedores-sucios",
        "banco-roto",
        "bache",
        "arbol-caido",
        "acera-rota",
        "limpieza-comunitaria",
        "peatonal-obras",
        "obras-centro-civico",
        "alcantarilla-rota",
        "sombra-parque",
        "fuga-agua",
        "taller-compostaje"
        );

    public FileServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public void uploadFile(Post post, List<MultipartFile> files) throws IOException, ForbiddenFileTypeException, MaxFileSizeException {
        if (files == null || files.isEmpty()) return;

        for (MultipartFile file : files) {
            validateFileExtension(file);

            try {
                uploadToCloudinary(post, file);
            } catch (Exception e) {
                throw new IOException(e.getMessage(), e);
            }
        }
    }

    private void validateFileExtension(MultipartFile file) throws ForbiddenFileTypeException, MaxFileSizeException {
        String filename = file.getOriginalFilename();
        if (filename == null) return;

        String extension = getExtension(filename);
        List<String> allowedMimeTypes = Arrays.asList("jpeg", "png", "jpg", "pdf");

        if (!allowedMimeTypes.contains(extension)) {
            throw new ForbiddenFileTypeException(
                String.format("Extensión '%s' no permitida.", extension)
            );
        }

        if (file.getSize() > 15 * 1024 * 1024) {
            throw new MaxFileSizeException("El archivo excede el tamaño máximo permitido de 15MB.");
        }
    }

    @SuppressWarnings("unchecked")
    private void uploadToCloudinary(Post post, MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) return;

        String extension = getExtension(filename);
        String folder = determineFolder(post.getCategory(), extension);
        Map<String, Object> uploadOptions = ObjectUtils.asMap(
            "folder", folder,
            "use_filename", true,
            "unique_filename", true,
            "overwrite", false
        );

        Map<String, Object> resultUpload = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
        String url = resultUpload.get("secure_url").toString();
        String publicId = resultUpload.get("public_id").toString();

        storeUpload(post, extension, publicId, url);
    }

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase().trim();
    }

    private String determineFolder(Post.Category category, String extension) {
        if (category == Post.Category.USER) {
            return "posts";
        } else if (category == Post.Category.ADMINISTRATION) {
            return extension.equals("pdf") ? "administration/files" : "administration/images";
        } else {
            throw new IllegalArgumentException("Categoría de post no válida.");
        }
    }

    private void storeUpload(Post post, String extension, String publicId, String url) {
        if (post.getCategory() == Post.Category.ADMINISTRATION && extension.equals("pdf")) {
            post.getFiles().put(publicId, url);
        } else {
            post.getImages().put(publicId, url);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void uploadAvatar(User user, MultipartFile avatar) throws IOException, ForbiddenFileTypeException, MaxFileSizeException {

        List<String> allowedMimeTypes = Arrays.asList("jpeg", "png", "jpg");

        if (avatar == null || avatar.getOriginalFilename() == null) {
            throw new IllegalArgumentException("Archivo no válido.");
        }

        String extension = avatar.getOriginalFilename()
                                .substring(avatar.getOriginalFilename().lastIndexOf(".") + 1)
                                .toLowerCase();

        if (!allowedMimeTypes.contains(extension)) {
            throw new ForbiddenFileTypeException(
                String.format("Extensión '%s' no permitida.", extension)
            );
        }

        if( avatar.getSize() > 15 * 1024 * 1024) {
            throw new MaxFileSizeException("El archivo excede el tamaño máximo permitido de 15MB.");
        }
        

        try {
                Map<String, Object> resultUpload;             

                resultUpload = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("folder", "avatars",
                    "use_filename", true,
                    "unique_filename", true,     
                    "overwrite", false));

                String url = resultUpload.get("secure_url").toString();
                String publicId = resultUpload.get("public_id").toString();
    
                user.setAvatar(url);
                user.setAvatarId(publicId);

                
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }


    @Override
    public void deleteFile(String publicId) throws IOException {
        try {
            if (!DEFAULT_IMAGE.contains(publicId)) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

}
