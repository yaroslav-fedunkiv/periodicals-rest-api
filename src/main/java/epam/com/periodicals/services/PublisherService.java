package epam.com.periodicals.services;


import epam.com.periodicals.dto.publishers.CreatePublisherDto;
import epam.com.periodicals.dto.publishers.FullPublisherDto;
import epam.com.periodicals.dto.publishers.UpdatePublisherDto;
import epam.com.periodicals.dto.subscriptions.SubscribeDto;

import java.util.List;
import java.util.Optional;

public interface PublisherService {
    void createPublisher(CreatePublisherDto createPublisherDto);
    List<FullPublisherDto> getAll();
    List<FullPublisherDto> search(String title);
    void subscribe(String email, String title, SubscribeDto subscribeDto);
    Optional<FullPublisherDto> getByTitle(String title);
    FullPublisherDto getById(String id);
    void deactivatePublisher(String title);
    void updatePublisher(UpdatePublisherDto updatePublisher);
    boolean isActive(String title);
    List<FullPublisherDto> getAllByPages(String page);
    List<FullPublisherDto> sortingBy(String sortingOption, String page);
    List<FullPublisherDto> getByTopic(String topic, String page);
}
