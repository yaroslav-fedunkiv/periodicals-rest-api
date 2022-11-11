package epam.com.periodicals.controllers;

import epam.com.periodicals.dto.publishers.CreatePublisherDto;
import epam.com.periodicals.dto.publishers.FullPublisherDto;
import epam.com.periodicals.dto.publishers.UpdatePublisherDto;
import epam.com.periodicals.exceptions.NoSuchPublisherException;
import epam.com.periodicals.model.Topics;
import epam.com.periodicals.services.PublisherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/publishers")
public class PublisherController {
    @Resource
    private PublisherService publisherService;

    @Operation(summary = "Create a publisher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publisher was created",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request. Publisher wasn't created",
                    content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<Object> createPublisher(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "publisher object to be created")
                                                  @RequestBody @Valid CreatePublisherDto publisher) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        publisherService.createPublisher(publisher);
        log.info("publisher is created {}", publisher);

        responseBody.put("message", publisher.getTitle() + "– publisher was created");
        responseBody.put("status", HttpStatus.OK);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @Operation(summary = "Updated a publisher by its title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publisher was updated"),
            @ApiResponse(responseCode = "404", description = "Publisher not found")})
    @PatchMapping("/update")
    public ResponseEntity<Object> updatePublisher(@RequestBody @Valid UpdatePublisherDto updatePublisherDto) {
        try{
            publisherService.updatePublisher(updatePublisherDto);
            log.info("updated publisher by title {}", updatePublisherDto.getOldTitle());
            return new ResponseEntity<>(updatePublisherDto.getOldTitle() + " was updated", HttpStatus.OK);
        }catch (NoSuchPublisherException e){
            log.error("{} not found", updatePublisherDto.getOldTitle());
            return new ResponseEntity<>(updatePublisherDto.getOldTitle() + " not found", HttpStatus.NOT_FOUND);
        }
    }


    @Operation(summary = "Get a publisher by its title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the publisher",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FullPublisherDto.class))}),
            @ApiResponse(responseCode = "404", description = "Publisher not found",
                    content = @Content)})
    @GetMapping("/get-by/{title}")
    public ResponseEntity<Object> getByTitle(@Parameter(description = "title of publisher to be searched")
                                             @PathVariable("title") String title) {
        log.info("getting publisher by title {}", title);
        try {
            return new ResponseEntity<>(publisherService.getByTitle(title), HttpStatus.OK);
        } catch (NoSuchPublisherException e) {
            log.error("Publisher with such title not found");
            return new ResponseEntity<>(title + " — the publisher with such a title not found",
                    HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Deactivate a publisher by its title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publisher was deactivated"),
            @ApiResponse(responseCode = "404", description = "Publisher not found")})
    @DeleteMapping("/deactivate/{title}")
    public ResponseEntity<Object> deactivateByTitle(@PathVariable("title") String title) {
        try{
            if (!publisherService.isActive(title)){
                log.warn("publisher {} is already deactivated!", title);
                return new ResponseEntity<>(title + " – publisher is already deactivated", HttpStatus.CONFLICT);
            } else{
                publisherService.deactivatePublisher(title);
                log.info("deactivated publisher by title {}", title);
                return new ResponseEntity<>(title + " was deactivated", HttpStatus.OK);
            }
        } catch (NoSuchPublisherException e){
            log.error("{} not found", title);
            return new ResponseEntity<>(title + " not found", HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get all publishers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all publishers",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FullPublisherDto.class))})
    })
    @GetMapping("/get-all")
    public ResponseEntity<Object> getAll() {
        log.info("getting all publishers");
        return new ResponseEntity<>(publisherService.getAll(), HttpStatus.OK);
    }

    @Operation(summary = "Get all publishers by pages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all publishers by pages",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FullPublisherDto.class))})
    })
    @GetMapping("/get/all/{page}")
    public ResponseEntity<Object> getAllByPages(@PathVariable String page) {
        log.info("getting all publishers by pages");
        return new ResponseEntity<>(publisherService.getAllByPages(page), HttpStatus.OK);
    }

    @Operation(summary = "Sorting publishers by price pr title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sorted publishers by price pr title"),
            @ApiResponse(responseCode = "400", description = "Incorrect sorting type")})
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

    @Operation(summary = "Getting all publishers by topic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "got all publishers by topic"),
            @ApiResponse(responseCode = "404", description = "Not found topic")})
    @GetMapping("/get/by/{topic}/{page}")
    public ResponseEntity<Object> getByTopic(@PathVariable String topic, @PathVariable String page) {
        try {
            Topics.valueOf(topic);
            log.info("sorting all publishers");
            return new ResponseEntity<>(publisherService.getByTopic(topic, page), HttpStatus.OK);
        } catch (IllegalArgumentException e){
            log.error("This topic doesn't exist: {}", topic);
            return new ResponseEntity<>(topic + " — Topic doesn't exist", HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get all publishers which match the search pattern")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all publishers by searching pattern",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FullPublisherDto.class))})
    })
    @GetMapping("/search/{title}")
    public ResponseEntity<Object> search(@PathVariable String title) {
        log.info("getting searched publishers");
        return new ResponseEntity<>(publisherService.search(title), HttpStatus.OK);
    }
}
