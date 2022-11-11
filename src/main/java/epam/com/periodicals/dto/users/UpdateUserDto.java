package epam.com.periodicals.dto.users;

import lombok.*;

import javax.validation.constraints.Email;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UpdateUserDto {
    @Email(message = "{user.wrong.email}")
    private String oldEmail;

    private String email;

    private String fullName;

    private String address;

    private String balance;

}