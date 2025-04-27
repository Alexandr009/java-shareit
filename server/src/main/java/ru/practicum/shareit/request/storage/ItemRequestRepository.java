package ru.practicum.shareit.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Collection<ItemRequest> getItemRequestsByRequestor_Id(Integer requestorId);

    ItemRequest getItemRequestsById(Integer id);

    @Query("SELECT ir FROM ItemRequest ir ORDER BY ir.created DESC")
    Collection<ItemRequest> findAllItemRequestsOrderByDateDesc();

}
