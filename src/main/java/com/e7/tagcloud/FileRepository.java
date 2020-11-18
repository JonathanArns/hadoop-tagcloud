package com.e7.tagcloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FileRepository {

    @Value("${paths.upload}")
    private String uploadPath;

    private ResourceLoader resourceLoader;

    @Autowired
    public FileRepository(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void saveMultipart(MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        File file = new File("/tmp/" + uploadPath + fileName);
//        file.getParentFile().mkdirs();
        file.createNewFile();
        multipartFile.transferTo(file);
        System.out.println(uploadPath + fileName);
    }

    public List<String> getFileNames() throws IOException {
        Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources("file:" + uploadPath + "*");
        List<String> files = new ArrayList<>();
        for (Resource resource : resources) {
            files.add(resource.getFilename());
        }
        return files;
    }

    public Resource getFile(String name) {
        return resourceLoader.getResource("file:" + uploadPath + name);
    }

    public Resource getImage(String name) {
        return resourceLoader.getResource("file:" + uploadPath + name);
    }
}
