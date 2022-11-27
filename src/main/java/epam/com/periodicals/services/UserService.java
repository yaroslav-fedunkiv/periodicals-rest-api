package epam.com.periodicals.services;

import epam.com.periodicals.dto.users.CreateUserDto;
import epam.com.periodicals.dto.users.FullUserDto;
import epam.com.periodicals.dto.users.UpdateUserDto;
import epam.com.periodicals.exceptions.NotEnoughMoneyException;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<FullUserDto> addUser(CreateUserDto createUserDto);
    List<FullUserDto> getAll();
    Optional<FullUserDto> getByEmail(String email);
    FullUserDto deactivateUser(String email);
    UpdateUserDto updateUser(UpdateUserDto updatedUser, String email);
    FullUserDto replenishBalance(String newBalance, String email);
    FullUserDto writeOffFromBalance(String price, String email) throws NotEnoughMoneyException;
    boolean isActive(String email);
}
