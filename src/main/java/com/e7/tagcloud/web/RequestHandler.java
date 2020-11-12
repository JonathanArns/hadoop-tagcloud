package com.e7.tagcloud.web;

import com.e7.tagcloud.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RequestHandler {

    @Value("${paths.upload}")
    private String uploadPath;

    private ResourceLoader resourceLoader;
    private FileRepository fileRepository;

    @Autowired
    public RequestHandler(ResourceLoader resourceLoader, FileRepository fileRepository) {
        this.resourceLoader = resourceLoader;
        this.fileRepository = fileRepository;
    }

    @PostMapping("/uploadFile")
    @ResponseStatus(value = HttpStatus.OK)
    public void uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        fileRepository.saveMultipart(multipartFile);
    }

    @GetMapping("/")
    public ModelAndView home() throws IOException {
        ModelAndView model = new ModelAndView("index");
        model.addObject("files", fileRepository.getFileNames());
        return model;
    }

    @ExceptionHandler(IOException.class)
    public ModelAndView handleIOException(IOException e) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", 500);
        return mav;
    }
}
