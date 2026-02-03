package ru.yandex.practicum.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dal.model.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {
}