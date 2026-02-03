package ru.yandex.practicum.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dal.model.Action;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
}