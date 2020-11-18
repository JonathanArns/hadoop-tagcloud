package com.e7.tagcloud.web;

import com.e7.tagcloud.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    private FileRepository fileRepository;

    @Autowired
    public RequestHandler(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @GetMapping("/")
    public String home(Model model) throws IOException {
        model.addAttribute("files", fileRepository.getFileNames());
        model.addAttribute("message", "Hello World!");
        return "index";
    }

    @GetMapping(value = "/tagcloud/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public Resource getTagcloud(@PathVariable("filename") String filename) {
        return this.fileRepository.getTagcloud(filename);
    }

    @GetMapping(value = "/tagcloud/global", produces = MediaType.IMAGE_JPEG_VALUE)
    public Resource getTagcloud() {
        return this.fileRepository.getGlobalTagcloud();
    }

    @PostMapping("/uploadFile")
    @ResponseStatus(value = HttpStatus.OK)
    public void uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        fileRepository.saveMultipart(multipartFile);
    }

    @PostMapping("/makeGlobal")
    public void makeGlobalTagcloud() {
        // TODO
    }

    @ExceptionHandler(IOException.class)
    public ModelAndView handleIOException(IOException e) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", 500);
        return mav;
    }
}
