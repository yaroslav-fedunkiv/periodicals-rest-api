package epam.com.periodicals.services;

import epam.com.periodicals.dto.publishers.CreatePublisherDto;
import epam.com.periodicals.dto.publishers.FullPublisherDto;
import epam.com.periodicals.dto.publishers.UpdatePublisherDto;
import epam.com.periodicals.dto.subscriptions.SubscribeDto;
import epam.com.periodicals.dto.users.FullUserDto;
import epam.com.periodicals.exceptions.NoSuchPublisherException;
import epam.com.periodicals.exceptions.UserIsAlreadySubscribedException;
import epam.com.periodicals.model.Publisher;
import epam.com.periodicals.model.Subscriptions;
import epam.com.periodicals.model.Topics;
import epam.com.periodicals.repositories.PublisherRepository;
import epam.com.periodicals.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {
    private final PublisherRepository publisherRepository;
    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;
    private final ModelMapper mapper;

    @Override
    public Optional<FullPublisherDto> createPublisher(CreatePublisherDto createPublisherDto) {
        Publisher publisher = publisherRepository.save(mapper.map(createPublisherDto, Publisher.class));
        log.info("publisher is saved {}", createPublisherDto);
        return Optional.of(mapper.map(publisher, FullPublisherDto.class));
    }

    @Override
    public Optional<FullPublisherDto> getByTitle(String title) {
        log.info("start method getByTitle() in publisher service {}", title);
        try {
            Publisher publisher = publisherRepository.findByTitle(title);
            return Optional.of(mapper.map(publisher, FullPublisherDto.class));
        } catch (IllegalArgumentException e) {
            log.info("This title was not found {}", title);
            throw new NoSuchPublisherException();
        }
    }

    @Override
    public List<FullPublisherDto> getAll() {
        log.info("start method getAll() in publisher service");
        return publisherRepository.findAll().stream()
                .map(e -> mapper.map(e, FullPublisherDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UpdatePublisherDto updatePublisher(UpdatePublisherDto updatePublisher, String title){
        FullPublisherDto publisherDto = getByTitle(title).orElseThrow();
        Optional<Publisher> publisher = publisherRepository.findById(Long.parseLong(publisherDto.getId()));

        String newTitle = (updatePublisher.getNewTitle() == null ? title : updatePublisher.getNewTitle());
        String topic = (updatePublisher.getTopic() == null ? publisherDto.getTopic() : updatePublisher.getTopic());
        String price = (updatePublisher.getPrice() == null ? publisherDto.getPrice() : updatePublisher.getPrice());
        String description = (updatePublisher.getDescription() == null ? publisherDto.getDescription() : updatePublisher.getDescription());

        publisher.orElseThrow().setTitle(newTitle);
        publisher.orElseThrow().setTopic(Topics.valueOf(topic));
        publisher.orElseThrow().setPrice(Double.parseDouble(price));
        publisher.orElseThrow().setDescription(description);

        Publisher updatedPublisher = publisherRepository.save(publisher.orElseThrow());

        log.warn("update "+title+" with fields:\n"+newTitle+"\n"+price+"\n"+topic+"\n"+description);
        return mapper.map(updatedPublisher, UpdatePublisherDto.class);
    }

    @Override
    public List<FullPublisherDto> getAllByPages(String page) {
        Pageable pagesWithThreeElements = PageRequest.of(Integer.parseInt(page), 3);
        log.info("start method getAll() in publisher service");
        return publisherRepository.findAll(pagesWithThreeElements)
                .stream()
                .map(e -> mapper.map(e, FullPublisherDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<FullPublisherDto> sortingBy(String sortingOption, String page) {
        Pageable pagesWithThreeElements = PageRequest.of(Integer.parseInt(page), 3, Sort.by(sortingOption));
        log.info("start method getAll() in publisher service");
        return publisherRepository.findAll(pagesWithThreeElements)
                .stream()
                .map(e -> mapper.map(e, FullPublisherDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<FullPublisherDto> getByTopic(String topic, String page) {
        Pageable pagesWithThreeElements = PageRequest.of(Integer.parseInt(page), 3);
        log.info("start method getAll() in publisher service");
        return publisherRepository.findByTopic(pagesWithThreeElements, Topics.valueOf(topic))
                .stream()
                .map(e -> mapper.map(e, FullPublisherDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<FullPublisherDto> search(String title) {
        log.info("start method search() in publisher service");
        return publisherRepository.searchByTitle(title).stream()
                .map(e -> mapper.map(e, FullPublisherDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FullPublisherDto deactivatePublisher(String title) {
        log.info("start method deletePublisher() by title in publisher service {}", title);
        FullPublisherDto publisherDto = getByTitle(title).orElseThrow();
        Optional<Publisher> publisher = publisherRepository.findById(Long.parseLong(publisherDto.getId()));
        publisher.orElseThrow().setIsActive(false);
        Publisher deactivatedPublisher = publisherRepository.save(publisher.orElseThrow());
        log.info("publisher {} was deactivated", title);
        return mapper.map(deactivatedPublisher, FullPublisherDto.class);
    }

    @Override
    public boolean isActive(String title){
        log.info("check if user with such email {} is active", title);
        return Boolean.parseBoolean(getByTitle(title).orElseThrow().getIsActive());
    }

    @Transactional
    @Override
    public SubscribeDto subscribe(String email, String title, SubscribeDto subscribeDto){
        FullUserDto user = userService.getByEmail(email).orElseThrow();
        FullPublisherDto publisher = getByTitle(title).orElseThrow();
        subscribeDto.setUserId(user.getId());
        subscribeDto.setPublisherId(publisher.getId());
        if (!isSubscribed(email, title)){
            userService.writeOffFromBalance(publisher.getPrice(), user.getEmail());
            subscriptionRepository.save(mapper.map(subscribeDto, Subscriptions.class));
            log.info("user was subscribe inside subscribe() method in publisherServiceImpl {} ", subscribeDto);
            return subscribeDto;
        } else{
            log.error("user is already subscribed");
            throw new UserIsAlreadySubscribedException();
        }
    }
    private boolean isSubscribed(String email, String title){
        FullUserDto user = userService.getByEmail(email).orElseThrow();
        FullPublisherDto publisher = getByTitle(title).orElseThrow();
        List<Subscriptions> subscriptions = subscriptionRepository.findByPublisherIdAndUserId(Long.parseLong(publisher.getId()), Long.parseLong(user.getId()));
        return !subscriptions.isEmpty();
    }
}
