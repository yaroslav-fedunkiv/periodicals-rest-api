package epam.com.periodicals.dto.publishers;

import lombok.*;

import javax.validation.constraints.NotBlank;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UpdatePublisherDto {
    private String newTitle;
    private String topic;
    private String price;
    private String description;
}

