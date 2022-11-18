package epam.com.periodicals.repositories;

import epam.com.periodicals.model.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscriptions, Long> {
    List<Subscriptions> findByPublisherIdAndUserId(Long publisherId, Long userId);

}
