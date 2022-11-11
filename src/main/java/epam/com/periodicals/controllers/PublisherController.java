package epam.com.periodicals.controllers;

import epam.com.periodicals.dto.publishers.CreatePublisherDto;
import epam.com.periodicals.model.Topics;
import epam.com.periodicals.services.PublisherService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/publishers")
public class PublisherController {
    @Resource
    private PublisherService publisherService;

    @PostMapping("/create")
    public ResponseEntity<Object> createPublisher(@RequestBody CreatePublisherDto publisher) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        publisherService.createPublisher(publisher);
        log.info("publisher is created {}", publisher);

        responseBody.put("message", publisher.getTitle() + "– publisher was created");
        responseBody.put("status", HttpStatus.OK);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


    @GetMapping("/get-by/{title}")
    public ResponseEntity<Object> getByTitle(@PathVariable("title") String title) {
        log.info("getting publisher by title {}", title);
        return new ResponseEntity<>(publisherService.getByTitle(title), HttpStatus.OK);
    }


    @GetMapping("/get-all")
    public ResponseEntity<Object> getAll() {
        log.info("getting all publishers");
        return new ResponseEntity<>(publisherService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/get/all/{page}")
    public ResponseEntity<Object> getAllByPages(@PathVariable String page) {
        log.info("getting all publishers by pages");
        return new ResponseEntity<>(publisherService.getAllByPages(page), HttpStatus.OK);
    }

    @GetMapping("/sort/by/{sort}/{page}")
    public ResponseEntity<Object> sortBy(@PathVariable String sort, @PathVariable String page) {
        if (sort.equals("price") || sort.equals("title")){
            log.info("sorting all publishers");
            return new ResponseEntity<>(publisherService.sortingBy(sort, page), HttpStatus.OK);
        }else {
            log.warn("Incorrect sorting type (must be price or title)");
            return new ResponseEntity<>("Incorrect sorting type (must be price or title)", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get/by/{topic}/{page}")
    public ResponseEntity<Object> getByTopic(@PathVariable String topic, @PathVariable String page) {
        Topics.valueOf(topic);
        log.info("sorting all publishers by topic");
        return new ResponseEntity<>(publisherService.getByTopic(topic, page), HttpStatus.OK);
    }

    @GetMapping("/search/{title}")
    public ResponseEntity<Object> search(@PathVariable String title) {
        log.info("getting searched publishers");
        return new ResponseEntity<>(publisherService.search(title), HttpStatus.OK);
    }
}
