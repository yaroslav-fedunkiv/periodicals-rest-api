package epam.com.periodicals.services;

import epam.com.periodicals.dto.users.CreateUserDto;
import epam.com.periodicals.dto.users.FullUserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void addUser(CreateUserDto createUserDto);
    List<FullUserDto> getAll();
    Optional<FullUserDto> getByEmail(String email);
    void deactivateUser(String email);
    Double replenishBalance(String newBalance, String email);
    boolean isActive(String email);
}
