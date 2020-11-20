package com.e7.tagcloud.web;

import com.e7.tagcloud.FileService;
import com.e7.tagcloud.TagcloudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.FileNotFoundException;
import java.io.IOException;

@Controller
public class TagcloudController {

    @Value("${paths.upload}")
    private String uploadPath;

    private FileService fileService;
    private TagcloudService tagcloudService;

    @Autowired
    public TagcloudController(FileService fileService, TagcloudService tagcloudService) {
        this.fileService = fileService;
        this.tagcloudService = tagcloudService;
    }

    @GetMapping("/")
    public String home(Model model) throws IOException {
        model.addAttribute("files", fileService.getFileNames());
        model.addAttribute("message", "Hello World!");
        return "index";
    }

    @GetMapping(value = "/tagcloud/{filename}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public Resource getTagcloud(@PathVariable("filename") String filename) {
        return this.fileService.getTagcloud(filename);
    }

    @GetMapping(value = "/tagcloud/global", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public Resource getTagcloud() {
        return this.fileService.getGlobalTagcloud();
    }

    @PostMapping("/uploadFile")
    @ResponseStatus(value = HttpStatus.OK)
    public void uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException, ClassNotFoundException, InterruptedException {
        fileService.saveMultipart(multipartFile);
    }

    @PostMapping("/makeGlobal")
    public void makeGlobalTagcloud() {
        // TODO
    }

//    @ExceptionHandler(FileNotFoundException.class)
//    public ModelAndView handleIOException(IOException e) {
//        ModelAndView mav = new ModelAndView("error");
//        mav.addObject("status", 404);
//        return mav;
//    }
}
