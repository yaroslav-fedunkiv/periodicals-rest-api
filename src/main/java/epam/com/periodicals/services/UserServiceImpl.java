package epam.com.periodicals.services;

import epam.com.periodicals.dto.users.CreateUserDto;
import epam.com.periodicals.dto.users.FullUserDto;
import epam.com.periodicals.model.User;
import epam.com.periodicals.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    @Resource
    private UserRepository userRepository;

    @Resource
    private ModelMapper mapper;

    @Override
    public void addUser(CreateUserDto createUserDto) {
        log.info("start method addUser() in userService: " + createUserDto.getEmail());
        userRepository.save(mapper.map(createUserDto, User.class));
        log.info("added new user");
    }

    @Override
    public List<FullUserDto> getAll() {
        log.info("start method getAll() in user service");
        return userRepository.findAll().stream()
                .map(e -> mapper.map(e, FullUserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FullUserDto> getByEmail(String email) {
        log.info("start method getByEmail() in user service {}", email);
            User user = userRepository.findByEmail(email);
            return Optional.of(mapper.map(user, FullUserDto.class));
    }

    @Override
    @Transactional
    public Double replenishBalance(String newBalance, String email) {
        log.info("start replenish the balance {}", email);
        FullUserDto user = getByEmail(email).get();
        Double balance = Double.parseDouble(newBalance) + Double.parseDouble(user.getBalance());
        userRepository.updateBalance(balance, email);
        return balance;
    }

    @Override
    @Transactional
    public void deactivateUser(String email) {
        userRepository.deactivateUser(email);
        log.info("user {} is deactivated", email);
    }

    @Override
    public boolean isActive(String email){
        log.info("check if user with such email {} is active", email);
        return Boolean.parseBoolean(getByEmail(email).get().getIsActive());
    }
}
