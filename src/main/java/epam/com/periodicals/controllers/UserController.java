package epam.com.periodicals.controllers;

import epam.com.periodicals.dto.users.CreateUserDto;
import epam.com.periodicals.dto.users.FullUserDto;
import epam.com.periodicals.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/users")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Object> createUser(@RequestBody CreateUserDto createUserDto) {
        log.info("start method createUser() in userController: " + createUserDto.getEmail());
        userService.addUser(createUserDto);
        log.info("user was register {}", createUserDto.getEmail());
        return new ResponseEntity<>(createUserDto.getEmail() + ": user was created", HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public List<FullUserDto> getAllUsers() {
        List<FullUserDto> list = userService.getAll();
        log.info("got all users");
        return list;
    }

    @GetMapping("/get-by/{email}")
    public ResponseEntity<Object> getByEmail(@PathVariable("email") String email) {
        log.info("getting user by email {}", email);
            return new ResponseEntity<>(userService.getByEmail(email), HttpStatus.OK);
    }

    @DeleteMapping("/deactivate/{email}")
    public ResponseEntity<Object> deactivateUser(@PathVariable("email") String email) {
        if (!userService.isActive(email)){
            log.warn("user {} is already deactivated!", email);
            return new ResponseEntity<>(email + " – user is already deactivated", HttpStatus.CONFLICT);
        } else{
            userService.deactivateUser(email);
            log.info("deactivate user by email {}", email);
            return new ResponseEntity<>(email + " – user was deactivated", HttpStatus.OK);
        }
    }
}
