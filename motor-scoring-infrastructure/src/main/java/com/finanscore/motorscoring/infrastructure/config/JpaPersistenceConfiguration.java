package com.finanscore.motorscoring.infrastructure.config;

import com.finanscore.motorscoring.infrastructure.persistence.entity.EvaluacionCrediticiaJpaEntity;
import com.finanscore.motorscoring.infrastructure.persistence.springdata.EvaluacionCrediticiaSpringDataRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Registra explícitamente los adaptadores de persistencia del módulo de
 * infraestructura. La clase principal se encuentra en el paquete bootstrap, por
 * lo que el auto-configuration package de Spring Boot no incluye por defecto
 * los paquetes hermanos de infraestructura.
 */
@Configuration(proxyBeanMethods = false)
@EntityScan(basePackageClasses = EvaluacionCrediticiaJpaEntity.class)
@EnableJpaRepositories(basePackageClasses = EvaluacionCrediticiaSpringDataRepository.class)
public class JpaPersistenceConfiguration {
}
