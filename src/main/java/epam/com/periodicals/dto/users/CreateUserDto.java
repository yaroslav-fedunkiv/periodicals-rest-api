package epam.com.periodicals.dto.users;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class CreateUserDto {
    private String role;

    private String email;

    private String fullName;

    private String password;

    private String confirmPassword;
}
