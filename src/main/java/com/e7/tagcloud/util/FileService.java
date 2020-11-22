package com.e7.tagcloud.util;

import com.e7.tagcloud.util.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class FileService {

    private Paths paths;
    private ResourceLoader resourceLoader;

    @Autowired
    public FileService(Paths paths, ResourceLoader resourceLoader) {
        this.paths = paths;
        this.resourceLoader = resourceLoader;
    }

    public String saveMultipart(MultipartFile multipartFile) throws IOException, InterruptedException, ClassNotFoundException {
        String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
        File file = new File(paths.getUpload() + fileName);
        file.mkdirs();
        file.createNewFile();
        multipartFile.transferTo(file);
        return fileName;
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

    public Map<String, String> getTagcloudNames() throws IOException {
        Map<String, String> files = getFileNames();
        for (String name : files.keySet()) {
            if (!hasTagcloud(name)) {
                files.remove(name);
            }
        }
        return files;
    }

    public Resource getFile(String name) {
        return resourceLoader.getResource("file:" + paths.getUpload() + name);
    }

    public Resource getTagcloud(String name) {
        return resourceLoader.getResource("file:" + paths.getTagclouds() + name + ".png");
    }

    public boolean hasTagcloud(String name) {
        return new File(paths.getTagclouds() + name + ".png").exists();
    }

    public Resource getGlobalTagcloud() {
        return resourceLoader.getResource("file:" + paths.getGlobalTagcloud());
    }

    public boolean hasGlobalTagcloud() {
        return new File(paths.getGlobalTagcloud()).exists();
    }
}
