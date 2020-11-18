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
    @Value("${paths.tagclouds}")
    private String tagcloudPath;
    @Value("${paths.tagclouds.global}")
    private String globalTagcloudPath;

    private ResourceLoader resourceLoader;
    private TagcloudService tagcloudService;

    @Autowired
    public FileRepository(ResourceLoader resourceLoader, TagcloudService tagcloudService) {
        this.resourceLoader = resourceLoader;
        this.tagcloudService = tagcloudService;
    }

    public void saveMultipart(MultipartFile multipartFile) throws IOException, InterruptedException, ClassNotFoundException {
        String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
        File file = new File(uploadPath + fileName);
        file.mkdirs();
        file.createNewFile();
        multipartFile.transferTo(file);
        tagcloudService.makeTagcloud(fileName);
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

    public Resource getTagcloud(String name) {
        return resourceLoader.getResource("file:" + tagcloudPath + name);
    }

    public Resource getGlobalTagcloud() {
        return resourceLoader.getResource("file:" + globalTagcloudPath);
    }
}
