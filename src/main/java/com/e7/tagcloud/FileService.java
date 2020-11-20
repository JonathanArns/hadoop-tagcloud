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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FileService {

    private Paths paths;
    private ResourceLoader resourceLoader;
    private TagcloudService tagcloudService;

    @Autowired
    public FileService(Paths paths, ResourceLoader resourceLoader, TagcloudService tagcloudService) {
        this.paths = paths;
        this.resourceLoader = resourceLoader;
        this.tagcloudService = tagcloudService;
    }

    public void saveMultipart(MultipartFile multipartFile) throws IOException, InterruptedException, ClassNotFoundException {
        String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
        File file = new File(paths.getUpload() + fileName);
        file.mkdirs();
        file.createNewFile();
        multipartFile.transferTo(file);
        tagcloudService.makeWordCount(fileName);
    }

    public Map<String, String> getFileNames() throws IOException {
        Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources("file:" + paths.getUpload() + "*");
        Map<String, String> files = new HashMap<>();
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            files.put(filename, filename.substring(filename.indexOf('_') + 1));
        }
        return files;
    }

    public Resource getFile(String name) {
        return resourceLoader.getResource("file:" + paths.getUpload() + name);
    }

    public Resource getTagcloud(String name) {
        return resourceLoader.getResource("file:" + paths.getTagclouds() + name + ".png");
    }

    public Resource getGlobalTagcloud() {
        return resourceLoader.getResource("file:" + paths.getGlobalTagcloud());
    }
}
