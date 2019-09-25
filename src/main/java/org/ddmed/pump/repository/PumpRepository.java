package org.ddmed.pump.repository;

import org.ddmed.pump.domain.Pump;

import org.springframework.data.repository.CrudRepository;

public interface PumpRepository extends CrudRepository<Pump, String> {
}
