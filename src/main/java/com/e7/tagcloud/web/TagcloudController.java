package com.e7.tagcloud.web;

import com.e7.tagcloud.processing.batch.BatchService;
import com.e7.tagcloud.util.FileService;
import com.e7.tagcloud.processing.TagcloudService;
import com.e7.tagcloud.processing.speed.SpeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class TagcloudController {

    private FileService fileService;
    private SpeedService speedService;
    private BatchService batchService;

    @Autowired
    public TagcloudController(FileService fileService, SpeedService speedService, BatchService batchService) {
        this.fileService = fileService;
        this.batchService = batchService;
        this.speedService = speedService;
    }

    @GetMapping("/")
    public String home(Model model) throws IOException {
        model.addAttribute("tagclouds", fileService.getTagcloudNames());
        model.addAttribute("files", fileService.getFileNames());
        if (fileService.hasGlobalTagcloud())
            model.addAttribute("global", "flag");
        return "index";
    }

    @GetMapping(value = "/files/{filename}", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public Resource getFile(@PathVariable("filename") String filename) {
        return this.fileService.getFile(filename);
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
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException, ClassNotFoundException, InterruptedException {
        String fileName = fileService.saveMultipart(multipartFile);
        speedService.createTagcloud(fileName);
        return "redirect:/";
    }

    @PostMapping("/makeGlobal")
    public String makeGlobalTagcloud() throws InterruptedException, IOException, ClassNotFoundException {
        batchService.run();
        return "redirect:/";
    }

//    @ExceptionHandler(FileNotFoundException.class)
//    public ModelAndView handleIOException(IOException e) {
//        ModelAndView mav = new ModelAndView("error");
//        mav.addObject("status", 404);
//        return mav;
//    }
}
