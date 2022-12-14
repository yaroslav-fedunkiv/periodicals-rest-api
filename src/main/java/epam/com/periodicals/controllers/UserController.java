package epam.com.periodicals.controllers;

import epam.com.periodicals.dto.users.CreateUserDto;
import epam.com.periodicals.dto.users.FullUserDto;
import epam.com.periodicals.dto.users.UpdateUserDto;
import epam.com.periodicals.exceptions.NoSuchUserException;
import epam.com.periodicals.services.UserService;
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
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Log4j2
@RestController
@RequestMapping("/users")
public class UserController {
    @Resource
    private UserService userService;

    @Operation(summary = "Create a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was created",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request. User wasn't created",
                    content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<Object> createUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "user object to be created")
                                             @Valid @RequestBody CreateUserDto createUserDto) {
        log.info("start method createUser() in userController: " + createUserDto.getEmail());
        userService.addUser(createUserDto);
        log.info("user was register {}", createUserDto.getEmail());
        return new ResponseEntity<>(createUserDto.getEmail() + ": user was created", HttpStatus.OK);
    }

    @Operation(summary = "Replenish user's balance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance was replenished"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    @PatchMapping("/replenish-balance/{email}")
    public ResponseEntity<Object> replenish(@Valid @RequestBody UpdateUserDto user,
                                            @PathVariable("email") String email) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        try {
            userService.replenishBalance(user.getBalance(), email);
            log.info("replenished balance {}", user.getBalance());
            return new ResponseEntity<>(email + " ??? balance of this user was replenished", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            responseBody.put("message", email + " ??? such email was not found");
            responseBody.put("status", HttpStatus.NOT_FOUND.value() + " NOT_FOUND");
            log.info("replenished balance {}", user.getBalance());
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Update a user by its email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was updated"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    @PatchMapping("/update")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UpdateUserDto user) {
        try{
            userService.updateUser(user);
            log.info("user was updated {}", user.getOldEmail());
            return new ResponseEntity<>("User " + user.getOldEmail() + " was apdated", HttpStatus.OK);
        }catch (NoSuchUserException e){
            return new ResponseEntity<>("User with such email doesn't exist " + user.getOldEmail(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all users",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FullUserDto.class))})
    })
    @GetMapping("/get-all")
    public List<FullUserDto> getAllUsers() {
        List<FullUserDto> list = userService.getAll();
        log.info("got all users");
        return list;
    }

    @Operation(summary = "Get a user by its email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FullUserDto.class))}),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping("/get-by/{email}")
    public ResponseEntity<Object> getByEmail(@Parameter(description = "email of user to be searched")
                                             @PathVariable("email") String email) {
        log.info("getting user by email {}", email);
            return new ResponseEntity<>(userService.getByEmail(email), HttpStatus.OK);
    }

    @Operation(summary = "Deactivate a user by its email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was deactivated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "User is already deactivated")
    })
    @DeleteMapping("/deactivate/{email}")
    public ResponseEntity<Object> deactivateUser(@PathVariable("email") String email) {
        try {
            if (!userService.isActive(email)){
                log.warn("user {} is already deactivated!", email);
                return new ResponseEntity<>(email + " ??? user is already deactivated", HttpStatus.CONFLICT);
            } else{
                userService.deactivateUser(email);
                log.info("deactivate user by email {}", email);
                return new ResponseEntity<>(email + " ??? user was deactivated", HttpStatus.OK);
            }
        } catch(NoSuchUserException e){
            log.error("user {} doesn't exist", email);
            return new ResponseEntity<>(email + " ??? user with such email doesn't exist", HttpStatus.NOT_FOUND);
        }
    }
}
