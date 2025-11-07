package com.example.vehiculos.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    private final Path rootLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir){
        this.rootLocation = Paths.get(uploadDir);
        try{
            Files.createDirectories(rootLocation);
        }
        catch( IOException e){
            throw new RuntimeException("No se pudo inicializar el directorio de almacenamiento", e);
        }
    }

    public String store(MultipartFile file){
        if(file.isEmpty()){
            throw new RuntimeException("No se puede guardar archivos vacios");

        }
        try{
            String originalFileName = file.getOriginalFilename();
            String extension ="";
            if (originalFileName !=null && originalFileName.contains(".")){
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            //resolviendo ruta completa
            Path destinitionFile = this.rootLocation.resolve(uniqueFilename);
            //Copiar u obtener el stream generado para llevarlo al destino
            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream, destinitionFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return uniqueFilename;
        }catch( IOException e){
            throw new RuntimeException("Fallo al guardar el archivo", e);
        }
    }

    public void delete(String filename){
        try{
            Path file = rootLocation.resolve(filename);
            Files.deleteIfExists(file);
        }catch( IOException e){
            throw new RuntimeException("fallo al eliminar el archivo", e);
        }
    }



}
