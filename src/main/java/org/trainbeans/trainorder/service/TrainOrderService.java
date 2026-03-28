package org.trainbeans.trainorder.service;

import org.springframework.stereotype.Service;
import org.trainbeans.trainorder.data.TrainOrderRepository;
import org.trainbeans.trainorder.model.TrainOrder;

import java.util.List;
import java.util.Optional;

@Service
public class TrainOrderService {

    private final TrainOrderRepository repository;

    public TrainOrderService(TrainOrderRepository repository) {
        this.repository = repository;
    }

    public List<TrainOrder> getAllOrders() {
        return repository.findAll();
    }

    public Optional<TrainOrder> getOrderById(Long id) {
        return repository.findById(id);
    }

    /** Returns an empty {@link TrainOrder} to back a blank new-order form. */
    public TrainOrder newOrder() {
        return TrainOrder.builder()
                .orderNumber(String.valueOf(repository.nextOrderNumberForToday()))
                .build();
    }

    public TrainOrder saveOrder(TrainOrder order) {
        return repository.save(order);
    }

    public void updateOrder(TrainOrder order) {
        repository.update(order);
    }


    public void deleteOrder(Long id) {
        repository.deleteById(id);
    }
}
