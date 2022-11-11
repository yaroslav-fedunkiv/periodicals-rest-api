package epam.com.periodicals.dto.publishers;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class CreatePublisherDto {
    private String title;

    private String topic;

    private String price;

    private String description;
}
