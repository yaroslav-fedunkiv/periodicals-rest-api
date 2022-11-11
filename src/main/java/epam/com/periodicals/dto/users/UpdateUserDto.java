package epam.com.periodicals.dto.users;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UpdateUserDto {
    private String oldEmail;

    private String email;

    private String fullName;

    private String address;

    private String balance;

}