package epam.com.periodicals.services;

import epam.com.periodicals.dto.publishers.CreatePublisherDto;
import epam.com.periodicals.dto.publishers.FullPublisherDto;
import epam.com.periodicals.dto.publishers.UpdatePublisherDto;
import epam.com.periodicals.dto.subscriptions.SubscribeDto;
import epam.com.periodicals.model.Publisher;
import epam.com.periodicals.model.Subscriptions;
import epam.com.periodicals.model.Topics;
import epam.com.periodicals.repositories.PublisherRepository;
import epam.com.periodicals.repositories.SubscriptionRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class PublisherServiceImpl implements PublisherService {
    @Resource
    private PublisherRepository publisherRepository;
    @Resource
    private SubscriptionRepository subscriptionRepository;

    @Resource
    private ModelMapper mapper;

    @Override
    public void createPublisher(CreatePublisherDto createPublisherDto) {
        publisherRepository.save(mapper.map(createPublisherDto, Publisher.class));
        log.info("publisher is saved {}", createPublisherDto);
    }

    @Transactional
    @Override
    public void updatePublisher(UpdatePublisherDto updatePublisher){
        FullPublisherDto publisher = getByTitle(updatePublisher.getOldTitle()).get();

        String oldTitle = updatePublisher.getOldTitle();
        String newTitle = (updatePublisher.getTitle() == null ? updatePublisher.getOldTitle() : updatePublisher.getTitle());
        String topic = (updatePublisher.getTopic() == null ? publisher.getTopic() : updatePublisher.getTopic());
        String price = (updatePublisher.getPrice() == null ? publisher.getPrice() : updatePublisher.getPrice());
        String description = (updatePublisher.getDescription() == null ? publisher.getDescription() : updatePublisher.getDescription());

        log.warn("update "+oldTitle+" with fields:\n"+newTitle+"\n"+price+"\n"+topic+"\n"+description);
        publisherRepository.updatePublisher(newTitle, topic, Double.parseDouble(price), description, oldTitle);
    }

    @Override
    public List<FullPublisherDto> getAll() {
        log.info("start method getAll() in publisher service");
        return publisherRepository.findAll().stream()
                .map(e -> mapper.map(e, FullPublisherDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<FullPublisherDto> getAllByPages(String page) {
        Pageable pagesWithThreeElements = PageRequest.of(Integer.parseInt(page), 3);
        log.info("start method getAllByPages() in publisher service");
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
        log.info("start method getByTopic() in publisher service");
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
    public Optional<FullPublisherDto> getByTitle(String title) {
        log.info("start method getByTitle() in publisher service {}", title);
        Publisher publisher = publisherRepository.getByTitle(title);
        return Optional.of(mapper.map(publisher, FullPublisherDto.class));
    }

    @Override
    public FullPublisherDto getById(String id) {
        log.info("start method getById() in publisher service {}", id);
        FullPublisherDto fullPublisherDto = mapper.map(publisherRepository.getById(Long.parseLong(id)), FullPublisherDto.class);
        return fullPublisherDto;
    }

    @Override
    public void subscribe(SubscribeDto subscribeDto){
        subscriptionRepository.save(mapper.map(subscribeDto, Subscriptions.class));
        log.info("user was subscribe inside subscribe() method in publisherServiceImpl {} ", subscribeDto);
    }

    @Override
    @Transactional
    public void deactivatePublisher(String title) {
        log.info("start method deletePublisher() by title in publisher service {}", title);
            publisherRepository.deactivatePublisher(title);
    }
    @Override
    public boolean isActive(String title){
        log.info("check if user with such email {} is active", title);
        return Boolean.parseBoolean(getByTitle(title).get().getIsActive());
    }
}
