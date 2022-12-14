package epam.com.periodicals.services;

import epam.com.periodicals.dto.users.CreateUserDto;
import epam.com.periodicals.dto.users.FullUserDto;
import epam.com.periodicals.dto.users.UpdateUserDto;
import epam.com.periodicals.exceptions.NotEnoughMoneyException;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void addUser(CreateUserDto createUserDto);
    List<FullUserDto> getAll();
    Optional<FullUserDto> getByEmail(String email);
    void deactivateUser(String email);
    void updateUser(UpdateUserDto updatedUser);
    Double replenishBalance(String newBalance, String email);
    Double writeOffFromBalance(String price, String email) throws NotEnoughMoneyException;
    boolean isActive(String email);
}
