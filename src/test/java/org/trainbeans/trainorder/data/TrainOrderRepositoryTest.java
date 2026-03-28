package org.trainbeans.trainorder.data;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.trainbeans.trainorder.model.TrainOrder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(TrainOrderRepository.class)
class TrainOrderRepositoryTest {

    @Autowired
    TrainOrderRepository repository;

    @Test
    void saveThenFindById() {
        TrainOrder saved = repository.save(buildOrder("99", "Northtown"));

        assertThat(saved.getId()).isNotNull();

        Optional<TrainOrder> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getOrderNumber()).isEqualTo("99");
        assertThat(found.get().getFromLocation()).isEqualTo("Northtown");
    }

    @Test
    void findAllReturnsSavedRecords() {
        repository.save(buildOrder("1", "Alpha"));
        repository.save(buildOrder("2", "Beta"));

        List<TrainOrder> all = repository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void updateChangesFields() {
        TrainOrder saved = repository.save(buildOrder("10", "OldStation"));

        saved.setFromLocation("NewStation");
        saved.setOrderDate("March 28, 2026");
        repository.update(saved);

        TrainOrder updated = repository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getFromLocation()).isEqualTo("NewStation");
        assertThat(updated.getOrderDate()).isEqualTo("March 28, 2026");
    }

    @Test
    void deleteRemovesRecord() {
        TrainOrder saved = repository.save(buildOrder("77", "Junction"));
        Long id = saved.getId();

        repository.deleteById(id);

        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void instructionsRoundTrip() {
        String body = "Engine 1234 will meet Engine 5678 at Midway Siding.\n\nWait for clearance before proceeding.";
        TrainOrder saved = repository.save(TrainOrder.builder()
                .orderNumber("55")
                .instructions(body)
                .build());

        String retrieved = repository.findById(saved.getId())
                .map(TrainOrder::getInstructions)
                .orElseThrow();
        assertThat(retrieved).isEqualTo(body);
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private TrainOrder buildOrder(String number, String from) {
        return TrainOrder.builder()
                .orderNumber(number)
                .fromLocation(from)
                .orderDate("March 27, 2026")
                .toLine("Conductor Smith and Engineer Jones of Engine 42 Train 7")
                .operatorInitials("PJD")
                .timeIssued("9:15 A")
                .atLocation("Midway Siding")
                .instructions("No. 8 West will take siding at Midway and wait for No. 5 East.")
                .completeTime("9:22 A")
                .completeOperator("PJD")
                .build();
    }
}

