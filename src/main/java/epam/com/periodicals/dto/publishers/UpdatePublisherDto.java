package epam.com.periodicals.dto.publishers;

import lombok.*;

import javax.validation.constraints.NotBlank;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UpdatePublisherDto {
    @NotBlank
    private String oldTitle;

    private String title;

    private String topic;

    private String price;
    private String description;

}
