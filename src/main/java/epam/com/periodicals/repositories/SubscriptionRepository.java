package epam.com.periodicals.repositories;

import epam.com.periodicals.model.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscriptions, Long> {
}
