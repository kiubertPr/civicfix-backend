package com.civicfix.tfg.model.services.impls;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.civicfix.tfg.model.services.FileService;
import com.civicfix.tfg.model.services.ScheduledService;
import com.civicfix.tfg.model.entities.daos.PostDao;
import com.civicfix.tfg.model.entities.daos.UserDao;

@Service
public class ScheduledServiceImpl implements ScheduledService {

    private final DataSource dataSource;

    private final PostDao postDao;
    private final UserDao userDao;
    private final FileService fileService;

    

    public ScheduledServiceImpl(PostDao postDao, UserDao userDao, FileService fileService, DataSource dataSource) {
        this.postDao = postDao;
        this.userDao = userDao;
        this.fileService = fileService;
        this.dataSource = dataSource;
    }

    @Override
    @Scheduled(cron = "0 */3 * * * ?")
    public void executeScheduledReset() {

        System.out.println("\n\n\nEjecutando tarea programada: Reinicio de archivos en la nube...\n\n\n");

        List<String> publicIds = new java.util.ArrayList<>();

        publicIds.addAll(postDao.findAllImagePublicIds());
        publicIds.addAll(postDao.findAllFilePublicIds());
        publicIds.addAll(userDao.findAllAvatarIds());

         for (String publicId : publicIds) {
             try {
                 fileService.deleteFile(publicId);
             } catch (Exception e) {
                 System.err.println("Error al eliminar el archivo con publicId: " + publicId);
                 e.printStackTrace();
             }
         }

         resetDatabase();

        System.out.println("\n\n\nTarea programada ejecutada.\n\n\n");
    }

    private void resetDatabase() {

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();

        populator.addScript(new ClassPathResource("schema.sql"));
        populator.addScript(new ClassPathResource("data.sql"));

        populator.execute(dataSource);
    }
    
}
